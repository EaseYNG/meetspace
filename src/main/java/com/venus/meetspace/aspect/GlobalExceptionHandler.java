package com.venus.meetspace.aspect;

import com.venus.meetspace.common.Result;
import com.venus.meetspace.common.type.ResultCode;
import com.venus.meetspace.exception.BusinessException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 抛出BusinessException时Controller返回Result
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        int code = e.getCode();
        ResultCode rc = null;
        switch (code) {
            case 400 -> rc = ResultCode.FAIL;
            case 401 -> rc = ResultCode.UNAUTHORIZED;
            case 403 -> rc = ResultCode.FORBIDDEN;
            case 404 -> rc = ResultCode.NOT_FOUND;
            case 500 -> rc = ResultCode.INTERNAL_SERVER_ERROR;
        }
        return Result.fail(null, e.getMessage(), rc);
    }
}
