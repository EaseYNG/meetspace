package com.venus.meetspace.common.enums;

import lombok.Getter;

@Getter
public enum ResultCode {
    SUCCESS(200),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    CONFLICT(409),
    VALUE_ERROR(406),
    NO_SUCH_OBJECT(407),
    STATUS_ERROR(408),
    INTERNAL_ERROR(500);

    private final int value;

    ResultCode(int value) {
        this.value = value;
    }
}
