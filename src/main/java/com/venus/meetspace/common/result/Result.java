package com.venus.meetspace.common.result;

import com.venus.meetspace.common.enums.ResultCode;
import lombok.Data;

@Data
public class Result<T> {

    private int code;

    private String msg;

    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = ResultCode.SUCCESS.getValue();
        result.data = data;
        return result;
    }

    public static <T> Result<T> success(T data, String msg) {
        Result<T> result = new Result<>();
        result.code = ResultCode.SUCCESS.getValue();
        result.msg = msg;
        result.data = data;
        return result;
    }

    public static <T> Result<T> fail(String msg, ResultCode code) {
        Result<T> result = new Result<>();
        result.code = code.getValue();
        result.msg = msg;
        return result;
    }
}
