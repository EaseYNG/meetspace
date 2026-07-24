# Code Review 修复计划

> 基于 2026-07-24 全项目架构审查报告，按优先级拆分为原子任务。
> **状态：全部完成** ✅ (P0✓ P1✓ P2✓ | P1-5 待补测试)

---

## P0 — Critical（阻塞性 Bug）✅ 已完成

### Task P0-1: 修复 UserMapper.xml 表名错误

- **文件**: `src/main/resources/repository/UserMapper.xml:6`
- **问题**: `SELECT * FROM user` → `user` 是 MySQL 保留字，且实际表名为 `users`
- **影响**: `findByUsername` 查询失败 → 登录/注册/CustomUserDetailsService 全部不可用
- **修复**: 将 `FROM user` 改为 `FROM users`
- **验证**: 启动项目，执行登录和注册接口

### Task P0-2: 修复 User Entity 字段映射错误

- **文件**: `src/main/java/com/venus/meetspace/model/entity/User.java:24-27`
- **问题**: 
  - `@TableField(value = "first_name")` → DB 实际列名为 `firstname`
  - `@TableField(value = "last_name")` → DB 实际列名为 `lastname`
- **影响**: MyBatis-Plus 操作 User 表时 `first_name`/`last_name` 列不存在
- **修复**: 
  - 方案A: 改 Entity 注解为 `@TableField("firstname")` / `@TableField("lastname")`
  - 方案B: 改 init.sql 列名为 `first_name` / `last_name`（推荐，更规范）
- **验证**: 启动后执行任意 User 查询/更新

### Task P0-3: 修复 ActivityMapper.xml 字符串比较 TINYINT

- **文件**: `src/main/resources/repository/ActivityMapper.xml:7,29`
- **问题**: `WHERE status = 'READY'` — 对 TINYINT 列做字符串比较，依赖 MySQL 隐式转换
- **影响**: `findAllReady()` 和 `findByConditions()` 查询结果不可靠；跨数据库不兼容
- **修复**: 将 `status = 'READY'` 改为 `status = 0`
- **验证**: 调用活动搜索接口确认返回正确

### Task P0-4: 统一活动状态码定义

- **涉及文件**:
  - `src/main/java/com/venus/meetspace/model/entity/Activity.java:31-37`（状态定义注释）
  - `src/main/java/com/venus/meetspace/service/impl/ActivityServiceImpl.java:64,80`（使用了 status=4 和错误的 deleteStatus）
  - `src/main/java/com/venus/meetspace/service/impl/ActivityParticipantServiceImpl.java:70`（使用了 status=4）
- **问题**: 
  1. 注释定义 4 种状态 (0/1/2/3)，但代码中使用了未定义的 `status=4`
  2. `deleteActivity` 设置 `status=3`（OVER），应为 `status=2`（DELETED）
  3. `quit` 方法中允许 `status=4` 的退出逻辑，但 status=4 不存在
- **修复**:
  1. 将 `Activity.java` 中的 int status 替换为 `ActivityStatus` 枚举
  2. `updateActivity` 中 `status==4` 改为 `status==ActivityStatus.DELETED`
  3. `deleteActivity` 中 `setStatus(3)` 改为 `setStatus(ActivityStatus.DELETED)`
  4. `ActivityParticipantServiceImpl.quit()` 中删除 `status!=4` 判断
- **枚举定义**:
  ```java
  public enum ActivityStatus {
      READY(0),    // 可报名
      CLOSED(1),   // 报名截止
      DELETED(2),  // 已删除
      OVER(3);     // 已结束
  }
  ```
- **验证**: 执行 CRUD 全流程 + quit 操作

---

## P1 — Important✅ 已完成

### Task P1-1: 添加 Cmd 入参校验

- **涉及文件**: `model/cmd/` 下所有 Cmd 类
  - `RegisterCmd.java` — username/password/nickname 不能为空
  - `LoginCmd.java` — username/password 不能为空
  - `ActivityCreateCmd.java` — title/startTime/endTime/signupDeadline 不能为空
  - `ActivityUpdateCmd.java` — 至少一个字段不为空
  - `ProfileUpdateCmd.java` — 至少一个字段不为空
- **修复**: 添加 `@NotBlank`/`@NotNull`/`@Size` 等 Jakarta Validation 注解
- **注意**: Controller 层需添加 `@Valid` 注解激活校验
- **验证**: 发送空字段请求，确认返回 400 错误

### Task P1-2: 添加活动最大参与人数校验

- **文件**: `src/main/java/com/venus/meetspace/service/impl/ActivityParticipantServiceImpl.java:37-60`
- **问题**: `participate()` 方法没有检查 `max_participants` 是否已达上限
- **修复**: 在 `participate()` 方法中加入参与者数量检查
- **验证**: 模拟达到上限后报名返回错误

### Task P1-3: 修复 getCreatedActivities 逻辑（区分创建者 vs 报名者）

- **文件**: `src/main/java/com/venus/meetspace/service/impl/ActivityParticipantServiceImpl.java:117-127`
- **问题**: `getCreatedActivities` 和 `getSignedUpActivities` 逻辑完全相同，都只基于 participant 记录查询
- **根因**: 当前设计中创建者也会被插入 `activity_participant` 表（见 `ActivityServiceImpl.createActivity:48-51`），无法区分"我创建的"和"我报名的"
- **方案A（推荐）**: 在 `Activity` 表新增 `owner_id` 字段，创建者不再插入 `activity_participant`，`getCreatedActivities` 查询 `Activity` 表
- **方案B**: 在 `activity_participant` 表新增 `role` 字段（OWNER/PARTICIPANT）区分
- **验证**: 创建活动后分别调用两个接口，确认返回正确

### Task P1-4: 添加活动更新/删除的权限检查

- **涉及文件**:
  - `src/main/java/com/venus/meetspace/service/impl/ActivityServiceImpl.java:59-71`（updateActivity）
  - `src/main/java/com/venus/meetspace/service/impl/ActivityServiceImpl.java:75-83`（deleteActivity）
- **问题**: 任何已认证用户都可以更新/删除任意活动，无 owner 校验
- **前置**: 依赖 P1-3（需要 owner_id 字段）
- **修复**: 在 update/delete 前校验 `activity.getOwnerId().equals(currentUserId)`
- **验证**: 用户A创建活动，用户B尝试修改/删除，应返回 403

### Task P1-5: 补充单元测试

- **涉及文件**: `src/test/java/com/venus/meetspace/controller/` 下 3 个空测试类
  - `UserControllerTest.java` — 空
  - `HomeControllerTest.java` — 空（且不在 `@SpringBootTest` 中）
  - `ActivityControllerTest.java` — 空（且不在 `@SpringBootTest` 中）
- **要求**:
  1. 为每个 Controller 编写至少 2 个基本测试（成功 + 失败场景）
  2. Service 层补充单元测试（`AuthServiceImpl`、`ActivityServiceImpl` 关键方法）
  3. 补充集成测试：注册 → 登录 → 创建活动 → 搜索活动的全链路
- **验证**: `mvn test` 全部通过

### Task P1-6: 启用 CSRF 保护或添加补偿措施

- **文件**: `src/main/java/com/venus/meetspace/config/SecurityConfig.java:35`
- **问题**: `csrf.disable()` 对基于 Session + Cookie 的认证是安全风险
- **方案**:
  - 方案A: 启用 CSRF，前端通过 meta 标签或 cookie 获取 token 并在请求头中携带
  - 方案B: 添加 `SameSite=Strict` Cookie 属性 + 自定义 `Referer`/`Origin` 头校验 Filter
- **验证**: 发送不带 CSRF token 的 POST 请求确认被拦截

### Task P1-7: 活动创建者不应自动成为参与者

- **文件**: `src/main/java/com/venus/meetspace/service/impl/ActivityServiceImpl.java:48-51`
- **问题**: 创建活动时自动将创建者插入 `activity_participant` 表，混淆了"创建者"和"参与者"概念
- **修复**: 配合 P1-3 方案A，移除自动插入逻辑，改为在 Activity 表记录 owner_id
- **验证**: 创建活动后在页面确认不会自动报名

---

## P2 — Minor ✅ 已完成

### Task P2-1: 统一 init.sql 列定义格式

- **文件**: `src/main/resources/static/init.sql:23`
- **问题**: `phone` 列缺少 `DEFAULT NULL`，与其他列格式不一致
- **修复**: 补齐 `DEFAULT NULL`
- **验证**: 重新执行 init.sql 不报错

### Task P2-2: 移除 anonymous().disable() 或添加注释说明

- **文件**: `src/main/java/com/venus/meetspace/config/SecurityConfig.java:37`
- **问题**: `anonymous().disable()` 非标准实践，可能导致 SecurityContext 中 Authentication 为 null
- **修复**: 删除该行使用默认配置，或添加注释说明原因
- **验证**: 未登录访问公开接口不报 NullPointerException

### Task P2-3: ApiConstants 版本号外部化

- **文件**: `src/main/java/com/venus/meetspace/common/constant/ApiConstants.java`
- **问题**: API 版本号硬编码在常量类中
- **修复**: 将 `/api/v1` 前缀提取到 `application.yml` 配置项
- **验证**: 确认接口路径不变

### Task P2-4: 日志脱敏

- **文件**: `src/main/java/com/venus/meetspace/service/impl/AuthServiceImpl.java:50`
- **问题**: `log.info("User logged in: {}", cmd.getUsername())` — 在生产环境记录用户名可能涉及隐私合规
- **修复**: 将 username 改为 userId（脱敏）
- **验证**: 检查日志输出

### Task P2-5: 修复 HomeControllerTest 不在 @SpringBootTest 中

- **文件**: `src/test/java/com/venus/meetspace/controller/HomeControllerTest.java:1-3`
- **问题**: 缺少 `@SpringBootTest` 注解（与 UserControllerTest 不一致）
- **修复**: 在 P1-5 补充测试时一并修复

---

## 任务依赖关系

```
P0-1 (表名) ──┐
P0-2 (字段映射) ─┤
P0-3 (XML比较)  ├── 无依赖，可并行 ──→ P1 组
P0-4 (状态码)  ─┘

P1-3 (创建者区分) ──→ P1-4 (权限检查)
                  ──→ P1-7 (移除自动报名)

P1-5 (测试) ←── 依赖 P0 + P1 完成后执行

P2 组 ── 无依赖，随时可做
```

## 执行顺序建议

1. **第1轮**: P0-1 → P0-2 → P0-3 → P0-4（全部 Critical，顺序执行验证）
2. **第2轮**: P1-1 → P1-2 → P1-3 → P1-7 → P1-4 → P1-6 → P1-5
3. **第3轮**: P2-1 → P2-2 → P2-3 → P2-4 → P2-5
