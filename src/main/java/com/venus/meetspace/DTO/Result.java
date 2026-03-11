package com.venus.meetspace.DTO;

import lombok.Data;

@Data
public class Result <T> {
    private int code; // 200 - success 400 - fail 500 - system error
    private String msg;
    private T data;


    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<T>();
        result.setCode(200);
        result.setMsg(null);
        result.setData(data);
        return result;
    }
    public static <T> Result<T> success(T data, String msg) {
        Result<T> result = new Result<T>();
        result.setCode(200);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }
    public static <T> Result<T> fail(String msg) {
        Result<T> result = new Result<T>();
        result.setCode(400);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }
    public static <T> Result<T> fail(String msg, int code) {
        Result<T> result = new Result<T>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }
}
