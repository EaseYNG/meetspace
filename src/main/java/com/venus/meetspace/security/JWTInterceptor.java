package com.venus.meetspace.security;

import com.venus.meetspace.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JWTInterceptor implements HandlerInterceptor {
    private final JWTUtil jwtUtil;

    public JWTInterceptor(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        String token = request.getHeader("Authentication");

        if(token == null) throw new BusinessException(413, "用户未登录！");

        // 从token获取userId
        long userId = jwtUtil.getIdFromToken(token);
        request.setAttribute("userId", userId); // 给request设置userId属性，传入Controller

        return true;
    }
}
