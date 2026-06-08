package com.campus.parking.controller;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.parking.common.Result;
import com.campus.parking.config.RequireRole;
import com.campus.parking.entity.SysUser;
import com.campus.parking.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    // 新增用户（仅管理员）
    @PostMapping
    @RequireRole("ADMIN")
    public Result addUser(@RequestBody SysUser user) {
        long count = sysUserService.lambdaQuery()
                .eq(SysUser::getUsername, user.getUsername())
                .count();
        if (count > 0) {
            return Result.error("用户名已存在");
        }
        user.setPassword(BCrypt.hashpw(user.getPassword()));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        boolean saved = sysUserService.save(user);
        return saved ? Result.success("添加成功") : Result.error("添加失败");
    }

    // 删除用户（仅管理员）
    @DeleteMapping("/{id}")
    @RequireRole("ADMIN")
    public Result deleteUser(@PathVariable Long id) {
        boolean removed = sysUserService.removeById(id);
        return removed ? Result.success("删除成功") : Result.error("删除失败");
    }

    // 修改用户（仅管理员）
    @PutMapping
    @RequireRole("ADMIN")
    public Result updateUser(@RequestBody SysUser user) {
        user.setPassword(null);
        user.setUpdateTime(LocalDateTime.now());
        boolean updated = sysUserService.updateById(user);
        return updated ? Result.success("修改成功") : Result.error("修改失败");
    }

    // 根据ID查询（仅管理员）
    @GetMapping("/{id}")
    @RequireRole("ADMIN")
    public Result getUserById(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        if (user != null) user.setPassword(null);
        return user != null ? Result.success(user) : Result.error("未找到该用户");
    }

    // 分页查询（仅管理员）
    @GetMapping("/list")
    @RequireRole("ADMIN")
    public Result getUserList(@RequestParam(defaultValue = "1") Integer currentPage,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(required = false) String username,
                              @RequestParam(required = false) String role) {
        Page<SysUser> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (username != null && !username.trim().isEmpty()) {
            wrapper.like(SysUser::getUsername, username.trim());
        }
        if (role != null && !role.trim().isEmpty()) {
            wrapper.eq(SysUser::getRole, role.trim());
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        sysUserService.page(page, wrapper);
        page.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(page);
    }

    // 修改密码（任意已登录用户，只能改自己的）
    @PutMapping("/password")
    public Result updatePassword(@RequestBody Map<String, String> params, HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");

        if (oldPassword == null || newPassword == null) {
            return Result.error("参数不完整");
        }

        SysUser user = sysUserService.getById(currentUserId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            return Result.error("原密码错误");
        }

        SysUser update = new SysUser();
        update.setId(currentUserId);
        update.setPassword(BCrypt.hashpw(newPassword));
        update.setUpdateTime(LocalDateTime.now());
        boolean updated = sysUserService.updateById(update);
        return updated ? Result.success("密码修改成功") : Result.error("密码修改失败");
    }

    // 获取当前登录用户信息（任意已登录用户）
    @GetMapping("/me")
    public Result getCurrentUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        SysUser user = sysUserService.getById(userId);
        if (user != null) user.setPassword(null);
        return user != null ? Result.success(user) : Result.error("用户不存在");
    }

    // 修改当前用户信息（任意已登录用户，只能改姓名和手机号）
    @PutMapping("/me")
    public Result updateCurrentUser(@RequestBody SysUser user, HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        SysUser update = new SysUser();
        update.setId(currentUserId);
        update.setRealName(user.getRealName());
        update.setPhone(user.getPhone());
        update.setUpdateTime(LocalDateTime.now());
        boolean updated = sysUserService.updateById(update);
        return updated ? Result.success("修改成功") : Result.error("修改失败");
    }
}
