# API-endpoints.md

存放本项目的 API 端点信息。

# API 端点列表

## 认证 `/api/v1/auth`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/v1/auth/register` | 用户注册 | 否 |
| POST | `/api/v1/auth/login` | 用户登录，创建 HTTP Session | 否 |
| POST | `/api/v1/auth/logout` | 登出，销毁 Session | 是 |

## 健康检查 `/api/v1/health`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/v1/health` | 服务连通性检测 | 否 |

## 用户 `/api/v1/users`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/v1/users/me/home` | 获取主页信息（资料 + 推荐活动） | 是 |
| GET | `/api/v1/users/me/profile` | 获取个人资料 | 是 |
| PATCH | `/api/v1/users/me/profile` | 更新个人资料 | 是 |

## 活动 `/api/v1/activities`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/v1/activities/create` | 创建活动 | 是 |
| GET | `/api/v1/activities/{id}` | 获取活动详情 | 是 |
| PATCH | `/api/v1/activities/{id}` | 更新活动信息 | 是 |
| DELETE | `/api/v1/activities/{id}` | 删除活动（逻辑删除） | 是 |
| POST | `/api/v1/activities/search` | 筛选搜索活动 | 是 |
| POST | `/api/v1/activities/{id}/participants` | 报名活动 | 是 |
| DELETE | `/api/v1/activities/{id}/participants/me` | 退出活动 | 是 |

## 活动记录 `/api/v1/users/me/activities`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/v1/users/me/activities/participated` | 全部参与活动（含创建和报名） | 是 |
| GET | `/api/v1/users/me/activities/created` | 我创建的活动 | 是 |
| GET | `/api/v1/users/me/activities/signed-up` | 我已报名的活动 | 是 |