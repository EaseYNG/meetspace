package com.venus.meetspace.common.exception;

import com.venus.meetspace.common.enums.ResultCode;
import com.venus.meetspace.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理全局业务异常
     * @param e 抛出的业务异常
     * @return Result结构体
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("Business exception: code={}, msg={}", e.getCode(), e.getMessage());
        return Result.fail(e.getMessage(), e.getCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Parameter validation failed");
        return Result.fail(msg, ResultCode.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleMessageNotReadable(HttpMessageNotReadableException e) {
        return Result.fail("Request body is invalid", ResultCode.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<Void> handleMissingParam(MissingServletRequestParameterException e) {
        return Result.fail("Missing required parameter: " + e.getParameterName(), ResultCode.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("System exception: ", e);
        return Result.fail("Internal server error", ResultCode.INTERNAL_ERROR);
    }
}
