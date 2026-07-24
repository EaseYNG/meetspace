# AGENTS.md

用于为 `code agent` 提供基本项目上下文和规则约束。

## 项目一句话描述

MeetSpace 是一个基于 Spring Boot 的活动管理系统，用于组织和管理用户之间的活动。

## 开发进度

所有的任务都集中于后端。当前处于**重构期**，解决大量不符合后端开发规范和前端数据需求的问题，因此你的任务可能涵盖大量架构重构工作。


## 技术栈

| 层次 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 3.4.5 |
| 语言 | Java | 17 |
| ORM | MyBatis-Plus | 3.5.15 |
| 数据库 | MySQL | 8.x |
| 安全 | Spring Security + Session | 6.x |
| 对象映射 | MapStruct + Lombok | 1.5.5 / 1.18.32 |
| 构建（后端） | Maven | 3.x |

## 规则约束

- **必读**：`./.agents/rules/general-rules.md` — 工作流规则（任务拆分、CR、Git、文档）
- **按需查阅**（编码时根据场景加载）：
  - `architecture-rules.md` — 分层职责、包结构、依赖注入、事务管理
  - `exception-rules.md` — 异常处理、ResultCode 选择
  - `model-rules.md` — DTO / VO / Entity / Query 命名与注解
  - `testing-rules.md` — 单元测试与集成测试规范

## 文档索引

- 通用文档目录：`./docs/`
- 项目说明文档：`README.md`
- API 端点文档：`API-endpoints.md`
- 规则文档目录：`./.agents/rules/`
- 任务模板：`./.agents/task-templates.md`
- Skill 文档目录：`./.agents/skills/`
  - `create-crud-module.md` — 创建新 CRUD 模块的完整工作流
  - `write-service-test.md` — 编写 Service 单元测试的模板与速查
  - `code-review.md` — 结构化 Code Review 检查清单