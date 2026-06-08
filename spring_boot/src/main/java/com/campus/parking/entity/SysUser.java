package com.campus.parking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user") // 对应数据库里的 sys_user 表
public class SysUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String realName;

    // 角色: ADMIN, GUARD, TEACHER, STUDENT
    private String role;

    private String phone;

    // 状态: 1正常, 0禁用
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
