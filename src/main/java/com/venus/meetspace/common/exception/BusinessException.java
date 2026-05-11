package com.venus.meetspace.common.exception;

import com.venus.meetspace.common.enums.ResultCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ResultCode code;
    private final String message;

    public BusinessException(ResultCode code, String message) {
        this.code = code;
        this.message = message;
    }
}
