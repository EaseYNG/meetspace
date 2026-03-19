package com.venus.meetspace.aspect;

import com.venus.meetspace.common.Result;
import com.venus.meetspace.exception.BusinessException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 抛出BusinessException时Controller返回Result
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        return Result.fail(e.getMessage(), e.getCode());
    }
}
