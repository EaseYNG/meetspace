# code-review

对 MeetSpace 项目的代码变更执行结构化 Code Review。

## 触发条件

当用户说"review"、"code review"、"CR"、或每个开发会话结束前自动触发。

## CR 检查清单

### 1. 架构兼容性

- [ ] 新代码是否遵循现有分层架构（Controller → Service → Mapper）
- [ ] 是否在正确的位置放置代码（业务逻辑在 Service，不在 Controller）
- [ ] 新模块的包路径是否符合项目约定
- [ ] 是否引入了不必要的依赖或循环依赖

### 2. 业务一致性

- [ ] 异常处理是否统一使用 `BusinessException(ResultCode, message)`
- [ ] ResultCode 选择是否恰当
- [ ] 权限校验是否完整（owner 校验、状态检查）
- [ ] 数据校验是否完备（`@Valid` + Jakarta Validation）

### 3. 代码质量

- [ ] 有并发风险的写操作是否有 `@Transactional(rollbackFor = Exception.class)`
- [ ] 日志是否记录关键操作（`log.info` 记录创建/更新/删除）
- [ ] NPE 风险：对可能为 null 的对象是否做了判断
- [ ] SQL 注入风险：MyBatis XML 中是否使用 `#{}` 而非 `${}`
- [ ] 是否存在硬编码的魔法值

### 4. 可测试性

- [ ] 单元测试是否覆盖了正常路径和异常路径
- [ ] Mock 是否合理（不 Mock 不该 Mock 的）
- [ ] 测试命名是否清晰表达意图
- [ ] 是否有必要的 `verify` 断言

### 5. API 设计（如涉及 Controller）

- [ ] URL 路径是否符合 RESTful 风格
- [ ] HTTP 方法是否正确（GET/POST/PATCH/DELETE）
- [ ] 响应体是否使用 `Result<T>` 包装
- [ ] 是否需要认证的端点是否正确配置

### 6. 文档更新

- [ ] `API-endpoints.md` 是否更新（新端点）
- [ ] `CHANGELOG.md` 是否记录变更
- [ ] 复杂逻辑是否有注释说明

## CR 结果输出模板

```
## Code Review 结果

### 通过项 ✅
- [列出所有通过的检查项]

### 问题项 ❌
- [问题描述] — [文件路径:行号] — [严重程度: 高/中/低] — [修复建议]

### 建议项 💡
- [改进建议] — [理由]

### 总体结论
[通过 / 不通过] — [一句话总结]
```

## 注意事项

- CR 不通过时，**必须终止任务**，明确告知用户不通过原因，不能继续执行后续步骤
- 对不确定的架构决策，使用 `AskUserQuestion` 向用户确认
- 优先关注高严重性问题（安全漏洞、数据一致性、事务缺失）
