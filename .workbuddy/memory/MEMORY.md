# MeetSpace 项目长期记忆

## 项目基本信息
- **项目路径**: `e:/dev/JavaProjects/meetspace/meetspace`
- **技术栈**: Spring Boot 4.0.3 + Flutter (Dart)
- **数据库**: MySQL (meetspace_db, localhost:3306, root/**无密码**)
- **Redis**: localhost:6379（已引入但推荐功能未实现）
- **后端端口**: 8080，JWT Bearer token 认证

## 后端 API 规范
- **无需认证**: `POST /user/register`, `POST /user/login`
- **需要认证（Bearer token）**:
  - `GET /home/profile` / `POST /home/profile` - 个人信息
  - `POST /activity/create` / `PATCH /activity/update/{id}` / `DELETE /activity/delete/{id}`
  - `GET /activity/signup/{id}` - 报名
  - `GET /activity/created` / `GET /activity/participated` - 获取活动列表
  - `GET /activity/list` - 获取所有活动（活动广场用）

## 前端架构
- **主题色**: `#6C63FF`（深紫色），Material3
- **路由**: `/login` → `/register` → `/main`（含底部 NavigationBar）
- **页面**: 登录/注册/首页/活动广场/活动管理/创建活动/活动详情/Profile
- **文件位置**: `frontend/meetspace_frontend/lib/`

## Activity 状态枚举（数据库存 tinyint 整数）
- `0` = `READY` = 报名中（绿色）
- `1` = `CLOSED` = 已截止（橙色）
- `2` = `OVER` = 已结束（灰色）
- `3` = `DELETED` = 已删除（红色）

## 测试数据
- `test_data.sql` 包含 10 用户 + 13 活动 + 26 参与记录
- 测试账号密码均为 `password123`

## 待办事项
- [ ] 活动推荐功能（详见 RECOMMENDATION.md，优先实现阶段一规则推荐）
- [ ] ActivityController 缺少 GET /activity/list 获取全部活动的接口（现在用的是按 ownerId 查询，活动广场功能受限）
- [ ] UserServiceImpl.register 逻辑有 bug：用 findByUsername 存在才保存，新用户无法注册（应改为不存在才创建）
