package com.venus.meetspace.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final int code;
    private final String message;

    public BusinessException(int code, String msg) {
        this.code = code;
        this.message = msg;
    }
}
