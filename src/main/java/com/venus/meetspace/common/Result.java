package com.venus.meetspace.common;

import com.venus.meetspace.common.type.ResultCode;
import lombok.Data;

@Data
public class Result <T> {
    private ResultCode code;
    private String msg;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<T>();
        result.setCode(ResultCode.SUCCESS);
        result.setMsg(null);
        result.setData(data);
        return result;
    }
    public static <T> Result<T> success(T data, String msg) {
        Result<T> result = new Result<T>();
        result.setCode(ResultCode.SUCCESS);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }
    public static <T> Result<T> fail(T data, String msg) {
        Result<T> result = new Result<T>();
        result.setCode(ResultCode.FAIL);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }
    public static <T> Result<T> fail(T data, String msg, ResultCode code) {
        Result<T> result = new Result<T>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }
    public static <T> Result<T> custom(T data, String msg, ResultCode code) {
        Result<T> result = new Result<T>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }
}
