package com.venus.meetspace.common.result;

import com.venus.meetspace.common.enums.ResultCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Unified response body")
public class Result<T> {

    @Schema(description = "Status code")
    private int code;

    @Schema(description = "Response message")
    private String msg;

    @Schema(description = "Response data")
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
