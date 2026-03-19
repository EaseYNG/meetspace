package com.venus.meetspace.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    /**
     * jwt拦截器，实现HandlerInterceptor，用于处理登录后的token请求
     * @author Void Yang
     * <p>
     *     拦截器处理流程：<br>
     *     请求到达：首先经过Servlet Filter（如果有）<br>
     *     进入DispatcherServlet：Spring MVC的核心控制器<br>
     *     HandlerMapping：确定处理请求的Controller和方法<br>
     *     执行拦截器preHandle：你的JWT拦截器在这个阶段执行<br>
     *     执行Controller方法：如果preHandle返回true<br>
     *     执行拦截器postHandle：Controller执行后，视图渲染前<br>
     *     执行拦截器afterCompletion：请求完成后（视图渲染后）<br>
     *     返回响应
     * </p>
     */

    private final JwtUtil jwtUtil;

    public JwtInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        System.out.println("URI: " + request.getRequestURI());

        String header = request.getHeader("Authorization");
        String token = header.replace("Bearer ", "");



        if (token == null || token.isEmpty()) {
            throw new RuntimeException("未登录");
        }

        long userId = jwtUtil.getIdFromToken(token);
        UserContext.set(userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) throws Exception {

        UserContext.clear();
    }
}