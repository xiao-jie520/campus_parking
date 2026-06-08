package com.campus.parking.controller;

import com.campus.parking.common.JwtUtils;
import com.campus.parking.common.Result;
import com.campus.parking.dto.LoginDTO;
import com.campus.parking.entity.SysUser;
import com.campus.parking.service.SysUserService;
import cn.hutool.crypto.digest.BCrypt; // 新增导入
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/api/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        // 1. 查数据库看用户是否存在
        SysUser user = sysUserService.lambdaQuery()
                .eq(SysUser::getUsername, loginDTO.getUsername())
                .one();

        // 2. 用户不存在或密码错误 (使用 Hutool 的 BCrypt.checkpw 替代)
        if (user == null || !BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
            return Result.error("用户名或密码错误");
        }

        // 3. 检查账号状态是否被禁用
        if (user.getStatus() == 0) {
            return Result.error("账号已被禁用，请联系管理员");
        }

        // 4. 校验通过，生成 JWT Token
        String token = JwtUtils.createToken(user.getId(), user.getUsername(), user.getRole());

        // 5. 返回 Token 给前端
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);

        return Result.success("登录成功", data);
    }
}
