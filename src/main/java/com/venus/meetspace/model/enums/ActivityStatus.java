package com.venus.meetspace.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum ActivityStatus {

    READY(0),
    CLOSED(1),
    DELETED(2),
    OVER(3);

    @EnumValue
    private final int code;

    ActivityStatus(int code) {
        this.code = code;
    }
}
