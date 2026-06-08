package com.campus.parking.config;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.alibaba.fastjson2.JSON;
import com.campus.parking.common.Result;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 跨域预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            renderUnauthorized(response, "未登录或Token缺失");
            return false;
        }

        String token = authHeader.substring(7);

        // 从环境变量读取 JWT 密钥
        String jwtSecret = System.getenv().getOrDefault("JWT_SECRET_KEY", "your_jwt_secret_key_here");
        if (!JWTUtil.verify(token, jwtSecret.getBytes())) {
            renderUnauthorized(response, "Token无效或已过期，请重新登录");
            return false;
        }

        // 解析 Token，将用户信息放入 request attribute
        JWT jwt = JWTUtil.parseToken(token);
        request.setAttribute("userId", Long.valueOf(jwt.getPayload("userId").toString()));
        request.setAttribute("username", jwt.getPayload("username").toString());
        request.setAttribute("role", jwt.getPayload("role").toString());

        // 检查方法上的角色注解
        if (handler instanceof HandlerMethod) {
            RequireRole requireRole = ((HandlerMethod) handler).getMethodAnnotation(RequireRole.class);
            if (requireRole != null) {
                String userRole = jwt.getPayload("role").toString();
                String[] allowedRoles = requireRole.value();
                boolean hasRole = false;
                for (String role : allowedRoles) {
                    if (role.equals(userRole)) {
                        hasRole = true;
                        break;
                    }
                }
                if (!hasRole) {
                    response.setStatus(403);
                    response.setContentType("application/json;charset=UTF-8");
                    response.setHeader("Access-Control-Allow-Origin", "*");
                    String json = JSON.toJSONString(Result.error(403, "权限不足"));
                    response.getWriter().write(json);
                    return false;
                }
            }
        }

        return true;
    }

    private void renderUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        String json = JSON.toJSONString(Result.error(401, message));
        response.getWriter().write(json);
    }
}
