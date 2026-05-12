# MeetSpace

MeetSpace 是一个集用户注册、登录、认证、活动创建、修改、删除、报名、搜索、推荐等功能的综合活动社交平台。

## 技术栈

| 层次 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 3.4.5 |
| 语言 | Java | 21 |
| ORM | MyBatis-Plus | 3.5.15 |
| 数据库 | MySQL | 8.x |
| 缓存 | Redis + Redisson | 7.x / 3.40.2 |
| 搜索引擎 | Elasticsearch + IK 分词器 | 8.x |
| 安全 | Spring Security + Session | 6.x |
| 对象映射 | MapStruct + Lombok | 1.5.5 / 1.18.32 |
| API 文档 | Knife4j (Swagger 3) | 4.5.0 |
| 前端框架 | Vue 3 (Composition API) | 3.5.x |
| 前端语言 | TypeScript | 5.7.x |
| 构建（后端） | Maven | 3.x |
| 构建（前端） | Vite | 6.x |
| UI 库 | Element Plus | 2.9.x |
| HTTP 客户端 | axios | 1.7.x |

## 前置环境

在启动项目前，请确保以下服务已安装并运行：

| 服务 | 默认端口 | 必需 | 说明 |
|------|----------|------|------|
| MySQL | 3306 | 是 | 数据库 `meetspace_db`，字符集 UTF-8 |
| Redis | 6379 | 是 | 缓存、分布式锁、Session 存储 |
| Elasticsearch | 9200 | 否（未接入） | 计划中，ES 依赖和索引配置已就绪但未实际接入，搜索暂用 MySQL `ST_Distance_Sphere` |

### 安装 IK 分词器

```bash
# ES 8.x 示例（版本号请对齐你的 ES 版本）
./bin/elasticsearch-plugin install https://get.infini.cloud/elasticsearch/analysis-ik/8.15.0
```

## 快速开始

### 1. 创建数据库

```sql
CREATE DATABASE meetspace_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 初始化表结构

应用启动时 MyBatis-Plus 不会自动建表，请手动执行以下 DDL：

```sql
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nickname VARCHAR(50) NOT NULL,
    username VARCHAR(32) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    age INT DEFAULT 0,
    gender VARCHAR(10),
    email VARCHAR(100),
    firstname VARCHAR(50),
    lastname VARCHAR(50)
);

CREATE TABLE activity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    address VARCHAR(500) NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    signup_deadline DATETIME NOT NULL,
    status ENUM('READY','CLOSED','DELETED','OVER') NOT NULL DEFAULT 'READY',
    min_participants INT DEFAULT 0,
    max_participants INT DEFAULT 10,
    image VARCHAR(500),
    latitude DOUBLE DEFAULT 0,
    longitude DOUBLE DEFAULT 0
);

CREATE TABLE activity_participant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    activity_id BIGINT NOT NULL,
    participant_id BIGINT NOT NULL,
    role ENUM('CREATOR','NORMAL') NOT NULL DEFAULT 'NORMAL',
    UNIQUE KEY uk_activity_user (activity_id, participant_id)
);
```

### 3. 修改配置

编辑 `src/main/resources/application.yml`，按实际环境修改数据库、Redis、ES 连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/meetspace_db?useSSL=false&...
    username: root
    password: 你的密码
  data:
    redis:
      host: localhost
      port: 6379
      password: 你的Redis密码  # 无密码则留空
    elasticsearch:
      uris: http://localhost:9200
```

### 4. 启动应用

```bash
./mvnw spring-boot:run
```

或使用 IDE 运行 `MeetspaceApplication.main()`。

### 5. 启动前端（可选）

```bash
cd frontend-vue
npm install    # 首次运行
npm run dev    # → http://localhost:5173
```

Vite 已配置 proxy，`/api/*` 自动转发到后端 `http://localhost:8080`。

### 6. 访问接口文档

```
http://localhost:8080/doc.html
```

## 项目结构

### 后端

```
src/main/java/com/venus/meetspace/
├── MeetspaceApplication.java          # 应用入口
├── common/                            # 常量、枚举、异常、统一返回
├── config/                            # Spring Security / Redis / MyBatis-Plus / Jackson / Knife4j
├── controller/                        # REST 控制器
├── convert/                           # MapStruct 映射
├── model/                             # entity / cmd / vo / query
├── repository/                        # MyBatis-Plus Mapper + XML
├── service/ + impl/                   # 业务逻辑
├── cache/ + impl/                     # Redis 缓存（穿透/击穿/雪崩防护）
├── security/                          # Session 认证 + SecurityUtil
└── aspect/                            # 定时任务（关闭过期活动）
```

### 前端（`frontend-vue/`）

```
src/
├── main.ts                            # 入口：Pinia + Router + Element Plus (zh-cn)
├── App.vue                            # 根组件，监听 session 过期自动跳转登录
├── api/                               # axios 实例 + 各模块 API 调用
│   ├── client.ts                      # 实例化 + 拦截器（401 清空 auth / 错误提示）
│   ├── auth.ts                        # login / register / logout
│   ├── activity.ts                    # CRUD + 搜索 + 报名/退出
│   └── user.ts                        # 主页 / 资料 / 活动列表
├── types/index.ts                     # TypeScript 接口 + ActivityStatus 枚举
├── stores/auth.ts                     # Pinia：session 状态、checkSession 初始化
├── router/index.ts                    # 8 条路由 + 导航守卫（await session）
├── layouts/
│   └── DefaultLayout.vue              # 顶部导航 + 内容区
├── components/
│   ├── activity/
│   │   ├── ActivityCard.vue           # 活动卡片（状态彩色边框 + badge）
│   │   ├── ActivityStatusBadge.vue    # 状态标签（报名中/已截止/已结束/已删除）
│   │   └── ActivitySearchFilters.vue  # 搜索面板（时间/位置/人数）
│   ├── auth/
│   │   ├── LoginForm.vue              # 登录表单
│   │   └── RegisterForm.vue           # 注册表单
│   ├── user/
│   │   ├── ProfileInfo.vue            # 资料展示
│   │   └── ProfileEdit.vue            # 资料编辑表单
│   └── common/
│       ├── LoadingSpinner.vue
│       └── EmptyState.vue
├── views/
│   ├── LoginView.vue                  # 登录页
│   ├── RegisterView.vue               # 注册页
│   ├── HomeView.vue                   # 首页（统计 + 推荐 + 进行中）
│   ├── ExploreView.vue                # 搜索发现
│   ├── ActivityDetailView.vue         # 详情 + 状态驱动操作按钮
│   ├── ActivityFormView.vue           # 创建/编辑（共用）
│   ├── ProfileView.vue                # 个人中心
│   ├── MyActivitiesView.vue           # 三个 Tab：参加的/报名的/创建的
│   └── NotFoundView.vue               # 404
└── styles/
    ├── variables.scss                  # 主题变量（#4CAF50 绿色）
    └── global.scss                     # 全局样式 + Element Plus 主题色
```

## API 接口概览

### 认证 `/api/v1/auth`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/v1/auth/register` | 用户注册 | 否 |
| POST | `/api/v1/auth/login` | 用户登录，创建 HTTP Session | 否 |
| POST | `/api/v1/auth/logout` | 登出，销毁 Session | 是 |

### 健康检查 `/api/v1/health`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/v1/health` | 服务连通性检测 | 否 |

### 用户 `/api/v1/users`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/v1/users/me/home` | 获取主页信息（资料 + 推荐活动） | 是 |
| GET | `/api/v1/users/me/profile` | 获取个人资料 | 是 |
| PATCH | `/api/v1/users/me/profile` | 更新个人资料 | 是 |

### 活动 `/api/v1/activities`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/v1/activities/create` | 创建活动 | 是 |
| GET | `/api/v1/activities/{id}` | 获取活动详情 | 是 |
| PATCH | `/api/v1/activities/{id}` | 更新活动信息 | 是 |
| DELETE | `/api/v1/activities/{id}` | 删除活动（逻辑删除） | 是 |
| POST | `/api/v1/activities/search` | 筛选搜索活动 | 是 |
| POST | `/api/v1/activities/{id}/participants` | 报名活动 | 是 |
| DELETE | `/api/v1/activities/{id}/participants/me` | 退出活动 | 是 |

### 活动记录 `/api/v1/users/me/activities`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/v1/users/me/activities/participated` | 全部参与活动（含创建和报名） | 是 |
| GET | `/api/v1/users/me/activities/created` | 我创建的活动 | 是 |
| GET | `/api/v1/users/me/activities/signed-up` | 我已报名的活动 | 是 |

### 智能 Agent `/api/v1/agents`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/v1/agents/recommendations` | 基于用户侧写的活动推荐 | 是 |
| POST | `/api/v1/agents/chat` | Agent 对话（SSE 流式） | 是 |

## 核心设计

### Session 认证流程

```
Client                              Server
  │                                   │
  │  POST /api/v1/auth/login          │
  │  {username, password}             │
  │──────────────────────────────────►│  CustomUserDetailsService
  │                                   │  → 从 MySQL 加载用户
  │                                   │  BCryptPasswordEncoder 校验
  │                                   │  SecurityContextHolder 写入
  │                                   │  HttpSession 持久化
  │  JSESSIONID + UserProfileVO       │
  │◄──────────────────────────────────│
  │                                   │
  │  GET /api/v1/users/me/profile     │
  │  Cookie: JSESSIONID=xxx           │
  │──────────────────────────────────►│  SecurityContextPersistenceFilter
  │                                   │  → 从 Session 恢复上下文
  │                                   │  SecurityUtil.getCurrentUserId()
  │  返回用户数据                      │
  │◄──────────────────────────────────│
  │                                   │
  │  POST /api/v1/auth/logout         │
  │──────────────────────────────────►│  Session 失效
  │                                   │  SecurityContext 清除
```

### 缓存策略

#### 缓存键表

| 缓存 Key | TTL | 说明 |
|----------|-----|------|
| `activity:{id}` | 30 分钟 | 活动详情，写时主动失效 |
| `activity:ready:list` | 5 分钟 | 就绪活动列表，增删改/定时关闭时失效 |
| `user:profile:{id}` | 1 小时 | 用户个人资料，更新资料时失效 |
| `user:participated:{userId}` | 5 分钟 | 用户全部参与的活动列表，报名/退出时失效 |
| `user:signed_up:{userId}` | 5 分钟 | 用户已报名的活动列表，报名/退出时失效 |
| `user:created:{userId}` | 5 分钟 | 用户创建的活动列表（缓存穿透防护） |
| `activity_participant:activity_id:{id}:participant_id:{id}` | 30 分钟 | 单个参与记录，退出时失效 |

#### 三级防护

- **缓存穿透**：`getOrLoad` 空值缓存 60 秒，防止恶意查询不存在的 ID
- **缓存击穿**：`getOrLoadWithLock` 基于 Redisson 分布式锁互斥重建，仅一个线程回源 DB
- **并发报名**：`RedisTemplate.setIfAbsent`（`SETNX`）原子操作防止重复报名
- **缓存雪崩**：`set` 方法对 TTL 添加 ±10% 随机偏移，避免大批 key 同时过期

### ES 搜索（计划中）

- 索引配置 `elasticsearch/activity-settings.json` 和文档模型 `search/` 包已就绪
- **暂未接入**：`pom.xml` 中无 `spring-data-elasticsearch` 依赖，当前搜索使用 MySQL `findByConditions`（`ST_Distance_Sphere` 空间距离过滤）
- 后续接入需：添加依赖 → 启用 `ElasticsearchDataSync` → 实现 `ActivitySearchService`

## License

MIT
