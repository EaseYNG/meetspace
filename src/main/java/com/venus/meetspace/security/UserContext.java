package com.venus.meetspace.security;

// 用于保存当前登陆的用户
public class UserContext {

    private static final ThreadLocal<Long> userHolder = new ThreadLocal<>();

    public static void set(long id) {
        userHolder.set(id);
    }
    public static Long get() {
        return userHolder.get();
    }
    public static void clear() {
        userHolder.remove();
    }
}
