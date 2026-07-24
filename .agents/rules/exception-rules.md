# exception-rules.md

异常处理与错误码使用规范。

## 异常体系

```
BusinessException(ResultCode code, String message)
    └── 由 Service 层抛出
    └── 由 GlobalExceptionHandler(@RestControllerAdvice) 统一捕获
    └── 转换为 Result.fail(message, code) 返回前端
```

## ResultCode 枚举

```java
SUCCESS(200)        // 成功
BAD_REQUEST(400)    // 请求参数错误
UNAUTHORIZED(401)   // 未登录
FORBIDDEN(403)      // 无权限（如非 owner 编辑活动）
NOT_FOUND(404)      // 资源不存在
VALUE_ERROR(406)    // 参数值非法（如开始时间在过去）
NO_SUCH_OBJECT(407) // 对象不存在
STATUS_ERROR(408)   // 状态不允许操作（如已删除的活动不可编辑）
CONFLICT(409)       // 冲突（如重复报名）
INTERNAL_ERROR(500) // 系统内部错误
```

## 使用规范

- **Service 层**：遇到业务错误直接 `throw new BusinessException(ResultCode, message)`
- **Controller 层**：**不 try-catch 业务异常**，由全局处理器统一处理
- **参数校验**：DTO/Cmd 使用 Jakarta Validation 注解（`@NotBlank`、`@NotNull`、`@Future` 等），校验信息用中文
- **兜底**：`Exception.class` → `Result.fail("Internal server error", INTERNAL_ERROR)`，记录 `log.error`

## GlobalExceptionHandler 处理链

| 异常类型 | ResultCode | 日志级别 |
|----------|-----------|---------|
| `BusinessException` | e.getCode() | WARN |
| `MethodArgumentNotValidException` | BAD_REQUEST | — |
| `HttpMessageNotReadableException` | BAD_REQUEST | — |
| `MissingServletRequestParameterException` | BAD_REQUEST | — |
| `Exception`（兜底） | INTERNAL_ERROR | ERROR |

## 选择 ResultCode 指南

| 场景 | ResultCode |
|------|-----------|
| 资源不存在 | `NOT_FOUND` |
| 权限不足 | `FORBIDDEN` |
| 参数值不合法（业务校验） | `VALUE_ERROR` |
| 状态机不允许操作 | `STATUS_ERROR` |
| 重复操作/冲突 | `CONFLICT` |
| 请求格式错误（由全局处理器处理） | `BAD_REQUEST` |
