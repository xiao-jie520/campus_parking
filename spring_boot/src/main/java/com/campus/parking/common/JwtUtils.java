package com.campus.parking.common;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {

    // JWT 密钥，从环境变量读取，默认值仅供开发使用
    private static final String SECRET_KEY = System.getenv().getOrDefault("JWT_SECRET_KEY", "your_jwt_secret_key_here");

    // Token 默认有效期为 24 小时
    private static final int EXPIRE_HOURS = 24;

    /**
     * 生成 Token
     * @param userId 用户ID
     * @param username 用户名
     * @param role 角色
     * @return Token字符串
     */
    public static String createToken(Long userId, String username, String role) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("username", username);
        payload.put("role", role);
        
        // 设置过期时间（使用秒级时间戳）
        Date expireTime = DateUtil.offset(new Date(), DateField.HOUR_OF_DAY, EXPIRE_HOURS);
        payload.put("exp", expireTime.getTime() / 1000);

        return JWTUtil.createToken(payload, SECRET_KEY.getBytes());
    }

    /**
     * 校验 Token 是否有效
     * @param token Token字符串
     * @return true=有效，false=无效
     */
    public static boolean verifyToken(String token) {
        try {
            return JWTUtil.verify(token, SECRET_KEY.getBytes());
        } catch (Exception e) {
            return false; // 解析失败、过期等都会抛异常，返回false
        }
    }

    /**
     * 从 Token 中获取用户ID
     */
    public static Long getUserId(String token) {
        JWT jwt = JWTUtil.parseToken(token);
        return Long.valueOf(jwt.getPayload("userId").toString());
    }
}