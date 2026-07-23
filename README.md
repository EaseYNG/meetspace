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


## 开发环境配置

### 初始化数据库

执行 `src/main/resources/static/init.sql`

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

```

## 项目结构

## API 端点概览

见 `API-endpoints.md`

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
