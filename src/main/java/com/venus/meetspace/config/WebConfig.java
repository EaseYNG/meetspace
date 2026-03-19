package com.venus.meetspace.config;

import com.venus.meetspace.security.CurrentUserIdResolver;
import com.venus.meetspace.security.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final JwtInterceptor jwtInterceptor;
    private final CurrentUserIdResolver currentUserIdResolver;

    public WebConfig(JwtInterceptor jwtInterceptor, CurrentUserIdResolver currentUserIdResolver) {
        this.jwtInterceptor = jwtInterceptor;
        this.currentUserIdResolver = currentUserIdResolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/login",
                        "/user/register",
                        "/h2-console/**",
                        "/test/**",
                        "/error/**"
                );
    }
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(this.currentUserIdResolver);
    }
}
