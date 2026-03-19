package com.venus.meetspace.aspect;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;


/**
 * @author Void Yang
 *
 */

@Aspect
@Component
@Slf4j
public class LogAspect {
    private final HttpServletRequest request;
    private final ObjectMapper objectMapper; // 用于将对象映射到String输出到日志

    public LogAspect(HttpServletRequest request, ObjectMapper objectMapper) {
        this.request = request;
        this.objectMapper = objectMapper;
    }

    @Pointcut("@annotation(com.venus.meetspace.annotation.Log) || " +
            "@within(com.venus.meetspace.annotation.Log)") // 标识在类和方法上都可用
    public void logPointCut() {}

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object[] args = joinPoint.getArgs(); // 获取参数
        Object result;

        // 请求信息
        String url = request.getRequestURL().toString();
        String method = request.getMethod();
        String className = joinPoint.getTarget().getClass().getName();

        log.info("--------------- log ---------------");
        log.info("url: {}", url);
        log.info("method: {}", method);
        log.info("class: {}", className);
        log.info("args: {}", objectMapper.writeValueAsString(args));

        try {
            result = joinPoint.proceed(); // 执行原方法
        } catch (Exception e) {
            log.error("执行异常", e);
            throw e;
        }

        long duration = System.currentTimeMillis() - start; // 总时间
        log.info("response: {}", objectMapper.writeValueAsString(result));
        log.info("duration: {} ms", duration);

        return result;
    }

}
