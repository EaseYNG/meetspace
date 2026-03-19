package com.venus.meetspace.common.type;

import lombok.Getter;

@Getter
public enum ResultCode {
    SUCCESS(200),
    FAIL(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    DUPLICATION(405),
    VALUE_ERROR(406),
    NO_SUCH_OBJECT(407),
    STATUS_ERROR(408),
    INTERNAL_SERVER_ERROR(500);

    private final int code;

    ResultCode(int code) {
        this.code = code;
    }

}
