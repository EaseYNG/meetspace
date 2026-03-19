package com.venus.meetspace.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Void Yang <br>
 * {@code @Target} 表明这个注解用于方法<br>
 * {@code @Retention} 表明滞留策略<br>
 * 本注解用于标识Log切点
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
    String value() default "";
}
