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
| 前端 | Flutter | 3.41.6-stable |
| 构建 | Maven | 3.x |

## 前置环境

在启动项目前，请确保以下服务已安装并运行：

| 服务 | 默认端口 | 必需 | 说明 |
|------|----------|------|------|
| MySQL | 3306 | 是 | 数据库 `meetspace_db`，字符集 UTF-8 |
| Redis | 6379 | 是 | 缓存、分布式锁、Session 存储 |
| Elasticsearch | 9200 | 是 | 活动全文搜索，需安装 [IK 分词器](https://github.com/medcl/elasticsearch-analysis-ik) |

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

### 5. 访问接口文档

启动后打开浏览器访问：

```
http://localhost:8080/doc.html
```

## 项目结构

```
src/main/java/com/venus/meetspace/
├── MeetspaceApplication.java          # 应用入口
├── common/
│   ├── constant/
│   │   └── ApiConstants.java          # API 路径常量（版本前缀等）
│   ├── enums/
│   │   ├── ActivityStatus.java        # READY / CLOSED / DELETED / OVER
│   │   ├── ParticipantRole.java       # CREATOR / NORMAL
│   │   └── ResultCode.java            # 统一响应码枚举
│   ├── exception/
│   │   ├── BusinessException.java     # 业务异常（ErrorCode + message）
│   │   └── GlobalExceptionHandler.java # 全局异常处理，统一返回 Result 格式
│   ├── result/
│   │   ├── Result.java                # 统一响应体 {code, msg, data}
│   │   └── PageResult.java            # 分页响应体
│   └── type/
│       └── LocalDateTimeTypeHandler.java # MyBatis 时间类型处理器
├── config/
│   ├── ElasticsearchConfig.java       # ES 仓库扫描配置
│   ├── JacksonConfig.java             # JSON 序列化配置
│   ├── Knife4jConfig.java             # Swagger 文档配置
│   ├── MybatisPlusConfig.java         # MyBatis-Plus 拦截器
│   ├── RedisConfig.java               # Redis 序列化 + Redisson + CacheManager
│   ├── SecurityConfig.java            # Spring Security 过滤器链 + CORS
│   └── WebMvcConfig.java              # 全局 MVC 配置
├── controller/                        # REST 控制器
│   ├── ActivityController.java        # /api/v1/activities/** (CRUD + 搜索 + 报名/退出)
│   ├── ActivityParticipantController.java # /api/v1/users/me/activities/** (活动记录查询)
│   ├── AgentController.java           # /api/v1/agents/** (RAG+LLM 预留接口)
│   ├── AuthController.java            # /api/v1/auth/** (注册/登录/登出)
│   ├── HealthController.java          # /api/v1/health (健康检查)
│   └── UserController.java            # /api/v1/users/** (个人资料/主页)
├── convert/                           # MapStruct 对象映射器
│   ├── ActivityConvert.java
│   └── UserConvert.java
├── model/
│   ├── cmd/                           # 写操作 DTO（Command）
│   │   ├── ActivityCreateCmd.java
│   │   ├── ActivityUpdateCmd.java
│   │   ├── AgentRecommendCmd.java
│   │   ├── LoginCmd.java
│   │   ├── ProfileUpdateCmd.java
│   │   └── RegisterCmd.java
│   ├── entity/                        # 数据实体
│   │   ├── Activity.java
│   │   ├── ActivityParticipant.java
│   │   └── User.java
│   ├── query/                         # 查询 DTO
│   │   └── ActivitySearchQuery.java
│   └── vo/                            # 视图对象（Value Object）
│       ├── ActivityVO.java
│       ├── AgentRecommendVO.java
│       ├── UserHomeVO.java
│       └── UserProfileVO.java
├── repository/                        # MyBatis Mapper（替代旧 dao/）
│   ├── ActivityMapper.java
│   ├── ActivityParticipantMapper.java
│   └── UserMapper.java
├── search/                            # Elasticsearch 搜索
│   ├── ActivityDocument.java          # ES 索引文档模型
│   ├── ActivitySearchRepository.java  # Spring Data ES Repository
│   ├── ActivitySearchService.java     # ES 高级搜索服务
│   └── ElasticsearchDataSync.java     # 启动时 MySQL → ES 全量同步
├── security/                          # Spring Security
│   ├── CustomUserDetails.java         # 实现 UserDetails
│   ├── CustomUserDetailsService.java  # 从 MySQL 加载用户
│   └── SecurityUtil.java              # 获取当前登录用户 ID
├── service/                           # 业务逻辑层
│   ├── ActivityFilterService.java     # 活动筛选
│   ├── ActivityParticipantService.java
│   ├── ActivityService.java
│   ├── AgentService.java
│   ├── AuthService.java
│   ├── UserService.java
│   └── impl/                          # 接口实现
├── cache/                             # 缓存服务
│   ├── CacheService.java
│   └── impl/CacheServiceImpl.java
└── aspect/
    └── ActivityScheduler.java         # 定时任务：每分钟检测并关闭过期的 READY 活动

src/main/resources/
├── application.yml                    # 主配置文件
├── elasticsearch/
│   └── activity-settings.json         # ES 索引配置（分片 + IK 分词器）
└── repository/                        # MyBatis XML 映射
    ├── ActivityMapper.xml
    ├── ActivityParticipantMapper.xml
    └── UserMapper.xml
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
| PUT | `/api/v1/users/me/profile` | 更新个人资料 | 是 |

### 活动 `/api/v1/activities`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/v1/activities` | 创建活动 | 是 |
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

| 缓存 Key | TTL | 说明 |
|----------|-----|------|
| `activity:{id}` | 30 分钟 | 活动详情，写时主动更新 |
| `user:profile:{id}` | 1 小时 | 用户个人资料 |
| `activity:ready:list` | 5 分钟 | 就绪活动列表 |

三级防护：
- **缓存穿透**：空值缓存 60 秒，防止恶意查询不存在的 ID
- **缓存击穿**：Redisson 分布式锁互斥重建，仅一个线程回源 DB
- **并发报名**：`SETNX` 原子操作防止重复报名

### ES 搜索

- 索引名：`meetspace_activity`
- 中文分词：`ik_max_word`（索引）/ `ik_smart`（搜索）
- 支持：全文搜索、地理距离过滤、时间范围、人数范围、多条件组合
- 数据同步：应用启动时全量同步，运行时增量同步

## License

MIT
