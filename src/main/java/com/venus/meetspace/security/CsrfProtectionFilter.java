package com.venus.meetspace.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * CSRF 后端防护过滤器（用于 Apifox 调试阶段，不做前端 CSRF Token 注入）。
 * 校验变更类请求的 Origin/Referer 头，防止跨站请求伪造。
 */
public class CsrfProtectionFilter extends OncePerRequestFilter {

    private static final String[] SAFE_METHODS = {"GET", "HEAD", "OPTIONS"};

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (isSafeMethod(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");

        if (origin == null && referer == null) {
            // Apifox/Postman 等工具不发送 Origin/Referer，放行
            filterChain.doFilter(request, response);
            return;
        }

        String requestHost = request.getServerName();
        if (isSameOrigin(origin, requestHost) || isSameOrigin(referer, requestHost)) {
            filterChain.doFilter(request, response);
            return;
        }

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write("{\"code\":403,\"message\":\"CSRF check failed: origin mismatch\"}");
    }

    private boolean isSafeMethod(String method) {
        for (String safe : SAFE_METHODS) {
            if (safe.equalsIgnoreCase(method)) return true;
        }
        return false;
    }

    private boolean isSameOrigin(String headerValue, String host) {
        if (headerValue == null) return false;
        return headerValue.contains("//" + host) || headerValue.contains("//localhost");
    }
}
