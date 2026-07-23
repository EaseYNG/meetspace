# MeetSpace

MeetSpace 是一个集用户注册、登录、认证、活动创建、修改、删除、报名、搜索、推荐等功能的综合活动社交平台。

## 技术栈

| 层次 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 3.4.5 |
| 语言 | Java | 17 |
| ORM | MyBatis-Plus | 3.5.15 |
| 数据库 | MySQL | 8.x |
| 缓存 | Redis + Redisson | 7.x / 3.40.2 |
| 安全 | Spring Security + Session | 6.x |
| 对象映射 | MapStruct + Lombok | 1.5.5 / 1.18.32 |
| 构建（后端） | Maven | 3.x |

## 前置环境

在启动项目前，请确保以下服务已安装并运行：

| 服务 | 默认端口 | 必需 | 说明 |
|------|----------|------|------|
| MySQL | 3306 | 是 | 数据库 `meetspace_db`，字符集 UTF-8 |
| Redis | 6379 | 是 | 缓存、分布式锁、Session 存储 |


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


## API 端点概览

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


## License

MIT
