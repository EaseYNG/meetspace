# 重构变更记录

## 1. 包结构重构

按技术职责分层，消除原有 `dao/` / `mapper/` 命名冲突，统一 DTO 分类。

### 旧结构（混合）

```
annotation/        @CurrentUserId 注解
common/type/       ActivityStatus, Role, ResultCode 枚举
dao/               MyBatis Mapper
dto/request/       请求 DTO
dto/response/      响应 DTO
entity/            数据实体
mapper/            MapStruct 映射器
security/          JwtUtil, JwtInterceptor, UserContext, CurrentUserIdResolver
```

### 新结构（分层清晰）

```
common/
  constant/        ApiConstants（API 路径常量）
  enums/           枚举统一存放
  exception/       BusinessException + GlobalExceptionHandler
  result/          Result, PageResult（统一响应体）
  type/            MyBatis 类型处理器
config/            全局配置（Security、Redis、ES 等）
controller/        REST 控制器
convert/           MapStruct 对象映射器
model/
  cmd/             写操作 DTO（XxxCmd）
  query/           查询 DTO（XxxQuery）
  entity/          数据实体
  vo/              视图对象（XxxVO）
repository/        MyBatis Mapper（替代旧 dao/）
security/          Spring Security 组件
service/           业务接口 + impl
search/            Elasticsearch 搜索
cache/             Redis 缓存
aspect/            定时任务 / AOP
```

---

## 2. API 路径规范化

### 变更要点

- **加版本前缀**: 所有接口移至 `/api/v1/` 下
- **RESTful 风格**: 资源用复数名词，操作由 HTTP 方法表达
- **可读性优化**: 多词路径用 kebab-case，避免动词 + 名词混合

### 接口映射

| 旧路径 | 新路径 | HTTP 方法 |
|--------|--------|-----------|
| `/user/register` | `/api/v1/auth/register` | POST |
| `/user/login` | `/api/v1/auth/login` | POST |
| — | `/api/v1/auth/logout` | POST |
| `/user/test` | `/api/v1/health` | GET |
| `/home` | `/api/v1/users/me/home` | GET |
| `/home/profile` | `/api/v1/users/me/profile` | GET |
| `/home/profile` (POST) | `/api/v1/users/me/profile` | PUT |
| `/activity/create` | `/api/v1/activities` | POST |
| `/activity/update/{id}` | `/api/v1/activities/{id}` | PATCH |
| `/activity/delete/{id}` | `/api/v1/activities/{id}` | DELETE |
| — | `/api/v1/activities/{id}` | GET |
| `/activity/signup/{id}` | `/api/v1/activities/{id}/participants` | POST |
| `/activity/quit/{id}` | `/api/v1/activities/{id}/participants/me` | DELETE |
| `/activity/related` | `/api/v1/users/me/activities/participated` | GET |
| `/activity/created` | `/api/v1/users/me/activities/created` | GET |
| `/activity/signed_up` | `/api/v1/users/me/activities/signed-up` | GET |
| `/activity/search` | `/api/v1/activities/search` | POST |
| `/agent/recommend` | `/api/v1/agents/recommendations` | POST |
| `/agent/chat` | `/api/v1/agents/chat` | POST |

---

## 3. 认证方式变更: JWT → Session

### 移除

- `jjwt` 全部依赖（`jjwt-api`, `jjwt-impl`, `jjwt-jackson`）
- `JwtUtil`（Token 生成/解析）
- `JwtInterceptor`（手动拦截器）
- `UserContext`（ThreadLocal 上下文）
- `CurrentUserId` 注解 + `CurrentUserIdResolver`

### 引入: Spring Security + Session 认证

| 组件 | 职责 |
|------|------|
| `CustomUserDetails` | 实现 `UserDetails`，包装 User 实体 |
| `CustomUserDetailsService` | 实现 `UserDetailsService`，从 MySQL 加载用户 |
| `SecurityUtil` | 工具类，从 `SecurityContextHolder` 获取当前用户 |
| `SecurityConfig` | Spring Security 过滤器链 + CORS + Session 策略 |

### 认证流程

```
登录:  POST /api/v1/auth/login  (LoginCmd)
  → AuthenticationManager.authenticate()
  → CustomUserDetailsService.loadUserByUsername()
  → BCryptPasswordEncoder 密码校验
  → SecurityContextHolder 写入 + HttpSession 持久化
  → 返回 UserProfileVO  + Set-Cookie: JSESSIONID=xxx

后续请求:
  → Cookie: JSESSIONID=xxx
  → SecurityContextPersistenceFilter 还原上下文
  → Controller 通过 SecurityUtil.getCurrentUserId() 获取用户

退出:  POST /api/v1/auth/logout
  → 清除 SecurityContext + 使 Session 失效
```

---

## 4. DTO 命名规范化

| 分类 | 旧命名 | 新命名 | 示例 |
|------|--------|--------|------|
| 写操作 | `XxxRequest` | `XxxCmd` | `LoginCmd`, `ActivityCreateCmd` |
| 查询 | `XxxSearchRequest` | `XxxQuery` | `ActivitySearchQuery` |
| 响应 | `XxxResponse`, `Profile` | `XxxVO` | `ActivityVO`, `UserProfileVO` |
| 分页 | `PageResponse` | `PageResult` | `PageResult<T>` |

---

## 5. Controller 职责拆分

| 旧 | 新 |
|----|----|
| `UserController` (注册+登录) | → `AuthController` (认证) |
| `HomeController` (主页+资料) | → `UserController` (用户) |
| `ActivityController` (所有活动操作) | → `ActivityController` (CRUD+搜索+报名) + `ActivityParticipantController` (活动查询) |
| `AgentController` | → `AgentController` (不变) |
| — | → `HealthController` (健康检查) |

---

## 6. Service 命名规范化

| 旧方法 | 新方法 |
|--------|--------|
| `signup()` | `participate()` |
| `signupActivity()` | `participate()` |
| `getRelatedActivities()` | `getParticipatedActivities()` |
| `search()` (ActivityFilter) | `search()` (ActivityFilterService) |
| `setProfile()` | `updateProfile()` |

`ActivityFilter` 重命名为 `ActivityFilterService`，统一 `XxxService` 命名模式。

---

## 7. 其他改动

- 移除空的 `HomeService` 接口及其实现
- `GlobalExceptionHandler` 移至 `common/exception/`，增加参数校验异常处理
- `ResultCode` 枚举统一，增加 `BAD_REQUEST`(400) / `CONFLICT`(409)
- `Role` 枚举重命名为 `ParticipantRole`，语义更明确
- `resources/dao/` → `resources/repository/`（XML mapper 路径同步更新）

---

## 文件统计

- **新增**: 35 个文件
- **修改**: 17 个文件
- **删除**: 33 个文件
- **净变更**: +897 / -1338 行
