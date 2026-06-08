package com.campus.parking.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色权限注解，标注在 Controller 方法上
 * 用法: @RequireRole("ADMIN") 或 @RequireRole({"ADMIN", "GUARD"})
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    String[] value();
}
