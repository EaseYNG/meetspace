# MeetSpace 企业级项目文档

## 项目概述

MeetSpace 是一个综合性的活动社交平台，提供用户注册登录、活动创建管理、活动报名参与等核心功能。平台支持多语言（中文/英文），采用前后端分离架构，致力于为用户提供便捷的活动发现和参与体验。

### 核心特性

- **用户系统**：完整的注册、登录、个人资料管理
- **活动管理**：活动创建、编辑、删除、状态自动管理
- **报名系统**：活动报名、退出、参与记录
- **搜索筛选**：基于时间、地点、人数的多维度活动搜索
- **地理定位**：支持基于地理位置的活动推荐
- **地图选点**：集成高德地图，支持活动位置选择和导航
- **国际化**：中英文双语支持
- **安全认证**：JWT Token 认证机制

---

## 技术架构

### 前端技术栈

| 技术/框架             | 版本          | 用途                  |
| --------------------- | ------------- | --------------------- |
| Flutter               | 3.41.6-stable | 跨平台移动应用框架    |
| Dart                  | ^3.11.4       | 前端开发语言          |
| Dio                   | ^5.7.0        | HTTP 网络请求         |
| Google Fonts          | ^6.2.1        | 字体样式库            |
| JSON Serialization    | ^4.9.0        | JSON 序列化/反序列化  |
| SharedPreferences     | ^2.3.3        | 本地存储              |
| Flutter Localizations | SDK           | 国际化支持            |
| Material Design 3     | -             | UI 组件库             |
| flutter_map           | ^8.2.2        | 地图显示组件          |
| latlong2              | ^0.9.1        | 地理坐标处理          |
| geolocator            | ^14.0.2       | 地理位置获取          |
| permission_handler    | ^11.0.0       | Android/iOS 权限请求  |
| url_launcher          | ^6.3.1        | 外部链接/App 导航启动 |

### 后端技术栈

| 技术/框架         | 版本        | 用途             |
| ----------------- | ----------- | ---------------- |
| Spring Boot       | 4.0.3       | 后端框架         |
| Java              | 17          | 后端开发语言     |
| Spring Security   | 4.0.3       | 安全认证         |
| Spring Data JPA   | 4.0.3       | 数据持久化       |
| MySQL Connector   | 8.2.0       | 数据库驱动       |
| Hibernate Spatial | 6.4.4.Final | 地理空间数据处理 |
| JTS               | 1.19.0      | 空间几何计算     |
| JWT (JJWT)        | 0.11.5      | Token 认证       |
| MapStruct         | 1.5.5.Final | 对象映射         |
| Lombok            | -           | 代码简化         |
| Redis             | -           | 缓存支持         |

### 开发工具

- **前端 IDE**：VS Code / Android Studio
- **后端 IDE**：IntelliJ IDEA / Eclipse
- **构建工具**：Maven (后端), Flutter CLI (前端)
- **版本控制**：Git

---

## 系统架构

### 整体架构图

```
┌─────────────────────────────────────────────────────────┐
│                     Client Layer                         │
│  ┌─────────────────────────────────────────────────┐   │
│  │              Flutter Frontend                    │   │
│  │  - Multi-platform (iOS/Android/Web/Windows)     │   │
│  │  - Material Design 3 UI                         │   │
│  │  - i18n Support (zh/en)                         │   │
│  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
                          │
                          │ HTTP/REST API
                          │ JSON + JWT Token
                          ▼
┌─────────────────────────────────────────────────────────┐
│                    Application Layer                     │
│  ┌─────────────────────────────────────────────────┐   │
│  │           Spring Boot Backend                    │   │
│  │                                                  │   │
│  │  ┌──────────────────────────────────────────┐  │   │
│  │  │  Controller Layer                        │  │   │
│  │  │  - UserController                        │  │   │
│  │  │  - ActivityController                    │  │   │
│  │  │  - HomeController                        │  │   │
│  │  └──────────────────────────────────────────┘  │   │
│  │                                                  │   │
│  │  ┌──────────────────────────────────────────┐  │   │
│  │  │  Service Layer                           │  │   │
│  │  │  - UserService / AuthService             │  │   │
│  │  │  - ActivityService                       │  │   │
│  │  │  - ActivityParticipantService            │  │   │
│  │  │  - ActivityFilter                        │  │   │
│  │  │  - HomeService                           │  │   │
│  │  └──────────────────────────────────────────┘  │   │
│  │                                                  │   │
│  │  ┌──────────────────────────────────────────┐  │   │
│  │  │  Security & Middleware                   │  │   │
│  │  │  - JwtInterceptor                        │  │   │
│  │  │  - CurrentUserIdResolver                 │  │   │
│  │  │  - GlobalExceptionHandler                │  │   │
│  │  │  - ActivityScheduler                     │  │   │
│  │  └──────────────────────────────────────────┘  │   │
│  │                                                  │   │
│  │  ┌──────────────────────────────────────────┐  │   │
│  │  │  Data Access Layer                       │  │   │
│  │  │  - UserRepository                        │  │   │
│  │  │  - ActivityRepository                    │  │   │
│  │  │  - ActivityParticipantRepository         │  │   │
│  │  │  - Mapper (MapStruct)                    │  │   │
│  │  └──────────────────────────────────────────┘  │   │
│  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
                          │
                          │ JPA / Hibernate
                          ▼
┌─────────────────────────────────────────────────────────┐
│                     Data Layer                           │
│  ┌──────────────┐         ┌──────────────┐             │
│  │    MySQL     │         │    Redis     │             │
│  │  Database    │         │    Cache     │             │
│  │  - users     │         │  - Tokens    │             │
│  │  - activity  │         │  - Session   │             │
│  │  - participant│        │              │             │
│  └──────────────┘         └──────────────┘             │
└─────────────────────────────────────────────────────────┘
```

### 前端架构

#### 目录结构

```
frontend/
├── lib/
│   ├── api/                    # API 客户端
│   │   └── api_client.dart     # Dio 封装、Token 管理
│   ├── component/              # 可复用组件
│   │   ├── activity_card.dart  # 活动卡片组件
│   │   ├── custom_button.dart  # 自定义按钮
│   │   ├── search_location_field.dart # 位置搜索输入框
│   │   └── page_title.dart     # 页面标题组件
│   ├── l10n/                   # 国际化
│   │   ├── app_localizations.dart
│   │   ├── messages_en.dart
│   │   └── messages_zh.dart
│   ├── model/                  # 数据模型
│   │   ├── request/            # 请求 DTO
│   │   │   ├── activity_create_request.dart
│   │   │   ├── activity_update_request.dart
│   │   │   ├── activity_search_request.dart
│   │   │   ├── auth_request.dart
│   │   │   └── register_request.dart
│   │   ├── activity.dart       # 活动模型
│   │   ├── activity_status.dart # 活动状态枚举
│   │   ├── user.dart           # 用户模型
│   │   ├── profile.dart        # 个人资料模型
│   │   └── result.dart         # 统一响应模型
│   ├── page/                   # 页面
│   │   ├── login_page.dart     # 登录页
│   │   ├── register_page.dart  # 注册页
│   │   ├── home_page.dart      # 主页（容器）
│   │   ├── explore_page.dart   # 发现页
│   │   ├── activity_page.dart  # 活动页
│   │   ├── profile_page.dart   # 个人页
│   │   ├── map_picker_page.dart # 地图选点页
│   │   ├── activity_detail_page.dart  # 活动详情页
│   │   ├── create_activity_page.dart  # 创建活动页
│   │   └── settings_page.dart  # 设置页
│   ├── service/                # 业务服务
│   │   ├── auth_service.dart   # 认证服务
│   │   ├── activity_service.dart # 活动服务
│   │   ├── user_service.dart   # 用户服务
│   │   ├── location_service.dart # 位置服务（地理定位）
│   │   ├── location_search_service.dart # 高德 POI 搜索
│   │   └── navigation_service.dart # 高德地图导航
│   └── main.dart               # 应用入口
├── android/                    # Android 平台配置
├── ios/                        # iOS 平台配置
├── web/                        # Web 平台配置
├── windows/                    # Windows 平台配置
├── macos/                      # macOS 平台配置
├── linux/                      # Linux 平台配置
└── pubspec.yaml               # 依赖配置
```

#### 前端核心模块

**1. API 客户端 (api_client.dart)**

- 单例模式设计
- 支持多环境 host 自动检测（localhost/10.0.2.2/127.0.0.1）
- JWT Token 自动管理
- Dio 拦截器配置（日志、请求/响应处理）
- 跨平台支持（Web/移动端）

**2. 数据模型**

- 使用 `json_serializable` 进行 JSON 序列化
- 严格区分请求模型（Request）和实体模型（Entity）
- 统一响应格式（Result<T>）

**3. 服务层**

- `AuthService`: 注册、登录、登出
- `ActivityService`: 活动 CRUD、报名、查询
- `UserService`: 用户资料管理
- `LocationService`: 地理位置获取、地址解析（高德地图 API）
- `LocationSearchService`: 高德 POI 搜索、输入建议、地址转换
- `NavigationService`: 高德地图 App 导航集成

**4. UI 组件**

- Material Design 3 风格
- 自定义组件：活动卡片、统一按钮、页面标题
- `SearchLocationField`: 位置搜索输入组件（实时搜索、结果渲染）
- 响应式布局
- 下拉刷新、加载状态

### 地图功能模块

#### 核心服务

**1. LocationService ([lib/service/location_service.dart](lib/service/location_service.dart))**

- **功能**：跨平台地理位置获取与地址转换
- **关键方法**：
  - `getCurrentLocation()`: 获取当前位置
    - Web：使用 Geolocator，支持浏览器定位
    - Android：优先缓存位置（1分钟内），超时则获取实时位置
  - `getCurrentLocationWithAddress()`: 获取位置和地址
  - `getAddressFromCoordinates(lat, lng)`: 反向地理编码
- **高德 API Key**：
  - Web API Key: `2d6ee8e3071bf17f2d0694af2afc3b17`
  - 无需签名配置，REST API 返回格式化地址
- **技术栈**：geolocator ^14.0.2, Dio ^5.7.0

---

**2. LocationSearchService ([lib/service/location_search_service.dart](lib/service/location_search_service.dart))**

- **功能**：高德 POI 搜索、输入建议、地址转换
- **数据模型**：
  - `PoiItem`: POI 结果（名称、地址、坐标、ID）
  - `InputTip`: 输入建议（名称、地址、坐标、ID）
- **关键方法**：
  - `searchNearbyPoi(lat, lng, keyword, radius)`: 周边搜索
  - `searchPoiByText(keyword, city)`: 文本搜索
  - `getInputTips(input)`: 搜索建议（自动补全）
  - `getCoordinatesFromAddress(address)`: 正向地理编码
  - `getAddressFromCoordinates(lat, lng)`: 反向地理编码
- **API 端点**：
  - Place Around: `https://restapi.amap.com/v3/place/around`
  - Input Tips: `https://restapi.amap.com/v3/assistant/inputtips`
  - Geocode: `https://restapi.amap.com/v3/geocode/geo`
  - Reverse Geocode: `https://restapi.amap.com/v3/geocode/regeo`

---

**3. NavigationService ([lib/service/navigation_service.dart](lib/service/navigation_service.dart))**

- **功能**：高德地图 App 导航跳转
- **关键方法**：
  - `openGaodeOrDownload(destination, destinationName, strategy)`: 跳转高德导航或下载提示
  - `launchGaodeNavigation(lat, lng, name, strategy)`: 直接启动导航
  - `isGaodeMapInstalled()`: 检测高德 App 是否安装
  - `launchGaodePoiDetail(poiId)`: 打开 POI 详情
  - `launchGaodeSearch(keyword)`: 打开高德搜索
- **URL Scheme**：`amapuri://route/plan?...`
  - 支持三种出行方式：driving(开车)、transit(公交)、walk(步行)
  - Web 上返回下载提示；Android 上跳转或提示安装

---

#### UI 组件

**1. SearchLocationField ([lib/component/search_location_field.dart](lib/component/search_location_field.dart))**

- **属性**：
  - `onLocationSelected`: 选中位置回调
  - `centerLatLng`: 中心坐标（用于周边搜索）
  - `searchRadius`: 搜索半径（默认 5000 米）
- **功能**：
  - 实时搜索（防抖 300ms）
  - 显示输入建议列表
  - 异步地址转换
  - 结果网格展示
  - 支持中英文搜索

---

#### 页面集成

**1. MapPickerPage ([lib/page/map_picker_page.dart](lib/page/map_picker_page.dart))**

- **功能**：活动位置选择页面
- **核心特性**：
  - 高德地图瓦片 (flutter_map) + flutter_map ^8.2.2
  - 自动定位到当前位置
  - 手动点击地图选点
  - 搜索栏快速定位（集成 SearchLocationField）
  - 实时地址显示（坐标 → 地址转换）
  - 确认返回坐标和地址信息
- **UI 布局**：
  - 地图容器（flex 1）
  - 顶部搜索栏
  - 顶部定位按钮（重新定位）
  - 中心点提示器
  - 底部选点确认面板（显示地址、坐标）
- **工作流**：
  1. 页面加载 → 请求定位权限 → 获取当前位置 → 地图居中
  2. 用户可搜索、点击地图、或使用手动选点
  3. 每次选点 → 自动反向地理编码获取地址
  4. 点击确认 → 返回坐标和地址给前一页

**2. ActivityDetailPage 集成**

- 在活动详情页底部添加 "打开高德地图导航" 按钮
- 点击后调用 `NavigationService.openGaodeOrDownload()`
- 如果 App 未安装，弹出下载提示

---

#### 高德 API 配置

**Web API Key 配置**

- 密钥：`2d6ee8e3071bf17f2d0694af2afc3b17`
- 类型：Web API
- 无需签名验证，适合 REST 调用
- 配置位置：`lib/service/location_service.dart`

**Android API Key 配置**

- 密钥：`7907bea639e73c134f435d6cd2aa2120`
- 类型：Android SDK Key
- 配置位置：`android/app/src/main/AndroidManifest.xml`
  ```xml
  <meta-data
    android:name="com.amap.api.API_KEY"
    android:value="7907bea639e73c134f435d6cd2aa2120"/>
  ```

---

#### 技术方案决策

**为什么使用 flutter_map 而不是 amap_flutter_map？**

1. **跨平台支持**：flutter_map 支持 Web/Android/iOS/Desktop
2. **API 稳定性**：官方高德 plugin (amap_flutter_map 3.0+) API 存在较大变更
3. **依赖简洁**：无需复杂的平台签名配置
4. **功能完善**：高德瓦片 + REST API 已满足位置选点需求
5. **维护成本低**：不依赖官方插件版本更新

**使用 REST API 而不是 SDK**

- REST API 通过 HTTP 调用，无平台限制
- Web Key 无需签名验证，所有平台统一使用
- 减少依赖体积和编译复杂度

---

### 后端架构

#### 目录结构

```
meetspace/
├── src/main/java/com/venus/meetspace/
│   ├── annotation/             # 自定义注解
│   │   └── CurrentUserId.java  # 当前用户 ID 注解
│   ├── aspect/                 # AOP 切面
│   │   ├── ActivityScheduler.java  # 活动状态定时任务
│   │   └── GlobalExceptionHandler.java # 全局异常处理
│   ├── common/                 # 通用类
│   │   ├── type/               # 枚举类型
│   │   │   ├── ActivityStatus.java
│   │   │   └── ResultCode.java
│   │   └── Result.java         # 统一响应
│   ├── config/                 # 配置类
│   │   ├── SecurityConfig.java # 安全配置
│   │   └── WebConfig.java      # Web 配置
│   ├── controller/             # 控制器层
│   │   ├── UserController.java
│   │   ├── ActivityController.java
│   │   └── HomeController.java
│   ├── dto/                    # 数据传输对象
│   │   ├── request/            # 请求 DTO
│   │   │   ├── AuthRequest.java
│   │   │   ├── RegisterRequest.java
│   │   │   ├── ActivityCreateRequest.java
│   │   │   ├── ActivityUpdateRequest.java
│   │   │   └── ActivitySearchRequest.java
│   │   ├── response/           # 响应 DTO
│   │   │   ├── UserResponse.java
│   │   │   └── ActivityResponse.java
│   │   └── Profile.java        # 个人资料 DTO
│   ├── entity/                 # 实体类
│   │   ├── User.java
│   │   ├── Activity.java
│   │   └── ActivityParticipant.java
│   ├── exception/              # 异常类
│   │   └── BusinessException.java
│   ├── mapper/                 # 对象映射（MapStruct）
│   │   ├── UserMapper.java
│   │   ├── ActivityMapper.java
│   │   └── ActivityParticipantMapper.java
│   ├── repository/             # 数据访问层
│   │   ├── UserRepository.java
│   │   ├── ActivityRepository.java
│   │   └── ActivityParticipantRepository.java
│   ├── security/               # 安全相关
│   │   ├── JwtUtil.java        # JWT 工具
│   │   ├── JwtInterceptor.java # JWT 拦截器
│   │   ├── CurrentUserIdResolver.java # 参数解析器
│   │   └── UserContext.java    # 用户上下文
│   ├── service/                # 服务层接口
│   │   ├── UserService.java
│   │   ├── AuthService.java
│   │   ├── ActivityService.java
│   │   ├── ActivityParticipantService.java
│   │   └── ActivityFilter.java
│   └── impl/                   # 服务层实现
│       ├── UserServiceImpl.java
│       ├── AuthServiceImpl.java
│       ├── ActivityServiceImpl.java
│       ├── ActivityParticipantServiceImpl.java
│       ├── ActivityFilterImpl.java
│       └── HomeServiceImpl.java
├── src/main/resources/
│   └── application.yml         # 应用配置
└── pom.xml                     # Maven 配置
```

#### 后端核心模块

**1. 安全认证模块**

- **JwtUtil**: JWT Token 生成与解析
- **JwtInterceptor**: 请求拦截、Token 验证、用户上下文设置
- **CurrentUserIdResolver**: 从 Token 中提取用户 ID 并注入到 Controller 参数
- **SecurityConfig**: Spring Security 配置（CORS、CSRF、密码加密）

**2. 控制器层**

- **UserController**: 用户注册、登录接口
- **ActivityController**: 活动 CRUD、报名、查询接口
- **HomeController**: 首页相关接口

**3. 服务层**

- **UserServiceImpl**: 用户注册、资料管理
- **AuthServiceImpl**: 登录认证
- **ActivityServiceImpl**: 活动创建、更新、删除、查询
- **ActivityParticipantServiceImpl**: 活动报名、退出、参与记录
- **ActivityFilterImpl**: 活动搜索筛选（时间、地点、人数）

**4. 数据访问层**

- **Repository**: 基于 Spring Data JPA 的 CRUD 操作
- **Mapper**: 基于 MapStruct 的 DTO 与 Entity 转换
- **自定义查询**: 支持地理空间查询、多条件筛选

**5. AOP 切面**

- **ActivityScheduler**: 定时任务，自动更新活动状态（每小时执行）
- **GlobalExceptionHandler**: 统一异常处理

---

## 数据库设计

### 数据表结构

#### 1. users 表

| 字段名    | 类型    | 约束                        | 说明                |
| --------- | ------- | --------------------------- | ------------------- |
| id        | BIGINT  | PRIMARY KEY, AUTO_INCREMENT | 用户 ID             |
| nickname  | VARCHAR | NOT NULL, UNIQUE            | 昵称                |
| username  | VARCHAR | NOT NULL                    | 用户名（登录用）    |
| password  | VARCHAR | NOT NULL                    | 密码（BCrypt 加密） |
| age       | INT     | -                           | 年龄                |
| gender    | VARCHAR | -                           | 性别                |
| email     | VARCHAR | -                           | 邮箱                |
| firstname | VARCHAR | -                           | 名                  |
| lastname  | VARCHAR | -                           | 姓                  |

#### 2. activity 表

| 字段名           | 类型     | 约束                        | 说明                                             |
| ---------------- | -------- | --------------------------- | ------------------------------------------------ |
| id               | BIGINT   | PRIMARY KEY, AUTO_INCREMENT | 活动 ID                                          |
| owner_id         | BIGINT   | NOT NULL                    | 创建者 ID                                        |
| title            | VARCHAR  | NOT NULL                    | 活动标题                                         |
| description      | TEXT     | -                           | 活动描述                                         |
| address          | VARCHAR  | NOT NULL                    | 活动地点                                         |
| start_time       | DATETIME | NOT NULL                    | 开始时间                                         |
| end_time         | DATETIME | NOT NULL                    | 结束时间                                         |
| signup_deadline  | DATETIME | NOT NULL                    | 报名截止时间                                     |
| status           | TINYINT  | NOT NULL                    | 活动状态（0:READY, 1:CLOSED, 2:OVER, 3:DELETED） |
| min_participants | INT      | NOT NULL                    | 最小参与人数                                     |
| max_participants | INT      | NOT NULL                    | 最大参与人数                                     |
| image            | VARCHAR  | -                           | 活动图片 URL                                     |
| latitude         | DOUBLE   | -                           | 纬度                                             |
| longitude        | DOUBLE   | -                           | 经度                                             |
| location         | POINT    | -                           | 地理空间位置（用于空间查询）                     |

#### 3. activity_participant 表

| 字段名         | 类型   | 约束                        | 说明      |
| -------------- | ------ | --------------------------- | --------- |
| id             | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 记录 ID   |
| activity_id    | BIGINT | NOT NULL, FOREIGN KEY       | 活动 ID   |
| participant_id | BIGINT | NOT NULL, FOREIGN KEY       | 参与者 ID |

### 索引设计

- `users.username`: 唯一索引，加速登录查询
- `activity.owner_id`: 索引，加速按创建者查询
- `activity.status`: 索引，加速状态筛选
- `activity.location`: 空间索引，加速地理查询
- `activity_participant.activity_id`: 索引，加速报名记录查询
- `activity_participant.participant_id`: 索引，加速用户参与记录查询

---

## API 接口文档

### 基础信息

- **Base URL**: `http://localhost:8080`
- **认证方式**: JWT Bearer Token
- **请求格式**: `application/json`
- **响应格式**: `application/json`

### 统一响应格式

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {}
}
```

### 用户接口

#### 1. 用户注册

```http
POST /user/register
Content-Type: application/json

{
  "nickname": "用户昵称",
  "username": "用户名",
  "password": "密码"
}
```

**响应示例**:

```json
{
  "code": 200,
  "msg": "用户创建成功！",
  "data": null
}
```

#### 2. 用户登录

```http
POST /user/login
Content-Type: application/json

{
  "username": "用户名",
  "password": "密码"
}
```

**响应示例**:

```json
{
  "code": 200,
  "msg": "登录成功！",
  "data": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### 3. 获取个人资料

```http
GET /user/profile
Authorization: Bearer {token}
```

#### 4. 更新个人资料

```http
PUT /user/profile
Authorization: Bearer {token}
Content-Type: application/json

{
  "age": 25,
  "gender": "male",
  "email": "user@example.com",
  "firstname": "John",
  "lastname": "Doe"
}
```

### 活动接口

#### 1. 创建活动

```http
POST /activity/create
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "活动标题",
  "description": "活动描述",
  "address": "活动地点",
  "startTime": "2026-05-01T10:00:00",
  "endTime": "2026-05-01T18:00:00",
  "signupDeadline": "2026-04-30T23:59:59",
  "minParticipants": 5,
  "maxParticipants": 50,
  "image": "https://example.com/image.jpg",
  "latitude": 39.9042,
  "longitude": 116.4074
}
```

**响应示例**:

```json
{
  "code": 200,
  "msg": "活动创建成功！",
  "data": null
}
```

#### 2. 更新活动

```http
PATCH /activity/update/{activityId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "更新后的标题",
  "description": "更新后的描述",
  ...
}
```

#### 3. 删除活动

```http
DELETE /activity/delete/{activityId}
Authorization: Bearer {token}
```

#### 4. 活动报名

```http
GET /activity/signup/{activityId}
Authorization: Bearer {token}
```

#### 5. 退出活动

```http
DELETE /activity/quit/{activityId}
Authorization: Bearer {token}
```

#### 6. 获取参与的活动列表

```http
GET /activity/participated
Authorization: Bearer {token}
```

**响应示例**:

```json
{
  "code": 200,
  "msg": "用户参加活动列表获取成功！",
  "data": [
    {
      "id": 1,
      "title": "活动标题",
      "startTime": "2026-05-01T10:00:00",
      "endTime": "2026-05-01T18:00:00",
      "address": "活动地点",
      "status": 0,
      "minParticipants": 5,
      "maxParticipants": 50,
      "image": "https://example.com/image.jpg"
    }
  ]
}
```

#### 7. 获取创建的活动列表

```http
GET /activity/created
Authorization: Bearer {token}
```

#### 8. 搜索活动

```http
POST /activity/search
Content-Type: application/json

{
  "startTime": "2026-05-01T10:00:00",
  "endTime": "2026-05-01T18:00:00",
  "latitude": 39.9042,
  "longitude": 116.4074,
  "radiusKm": 10.0,
  "min": 5,
  "max": 50
}
```

**响应示例**:

```json
{
  "code": 200,
  "msg": null,
  "data": [...]
}
```

#### 9. 获取所有活动（测试用）

```http
GET /activity/list
Authorization: Bearer {token}
```

---

## 核心业务流程

### 1. 用户注册流程

```
用户输入注册信息
    ↓
前端验证输入完整性
    ↓
调用 /user/register API
    ↓
后端验证用户名唯一性
    ↓
密码 BCrypt 加密
    ↓
保存用户到数据库
    ↓
返回成功响应
```

### 2. 用户登录流程

```
用户输入登录凭证
    ↓
调用 /user/login API
    ↓
后端验证用户名和密码
    ↓
生成 JWT Token（有效期 1 小时）
    ↓
前端存储 Token 到 SharedPreferences
    ↓
后续请求自动携带 Token
```

### 3. 活动创建流程

```
用户填写活动信息
    ↓
前端验证时间逻辑（开始时间>当前时间）
    ↓
调用 /activity/create API
    ↓
后端验证时间有效性
    ↓
设置活动状态为 READY
    ↓
保存到数据库（自动计算 location）
    ↓
返回成功响应
```

### 4. 活动报名流程

```
用户点击报名按钮
    ↓
前端检查活动状态
    ↓
调用 /activity/signup/{id} API
    ↓
后端验证：
  - 活动状态为 READY
  - 未过报名截止时间
  - 用户未重复报名
    ↓
创建报名记录
    ↓
返回成功响应
```

### 5. 活动状态自动更新流程

```
定时任务（每小时执行）
    ↓
查询所有状态为 READY 的活动
    ↓
检查报名截止时间
    ↓
如果已过截止时间
    ↓
更新状态为 CLOSED
    ↓
保存到数据库
```

---

## 安全机制

### 1. JWT Token 认证

- **Token 生成**: 使用 HS256 算法，256 位密钥
- **Token 有效期**: 1 小时
- **Token 存储**: 前端 SharedPreferences
- **Token 传递**: Authorization Header (Bearer {token})

### 2. 密码安全

- **加密算法**: BCrypt
- **强度**: 自动加盐，防止彩虹表攻击

### 3. CORS 配置

- 允许所有来源（开发环境）
- 支持所有 HTTP 方法
- 支持凭证传输

### 4. 参数验证

- 时间逻辑验证（开始时间 > 当前时间）
- 活动状态验证
- 权限验证（只能编辑自己的活动）

---

## 部署指南

### 环境要求

#### 后端

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

#### 前端

- Flutter SDK 3.41.6+
- Dart SDK 3.11.4+
- Android Studio / VS Code

### 数据库初始化

```sql
CREATE DATABASE meetspace_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE meetspace_db;

-- 表结构会自动由 JPA 创建（ddl-auto: update）
```

### 后端部署

1. **配置数据库连接**
   编辑 `src/main/resources/application.yml`:

   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/meetspace_db
       username: your_username
       password: your_password
     redis:
       host: localhost
       port: 6379
   ```

2. **构建项目**

   ```bash
   mvn clean package
   ```

3. **运行应用**
   ```bash
   java -jar target/meetspace-0.0.1-SNAPSHOT.jar
   ```

### 前端部署

1. **获取依赖**

   ```bash
   flutter pub get
   ```

2. **运行应用**

   ```bash
   # Android
   flutter run

   # iOS
   flutter run

   # Web
   flutter run -d chrome

   # Windows
   flutter run -d windows
   ```

3. **构建发布版本**

   ```bash
   # Android
   flutter build apk --release

   # iOS
   flutter build ios --release

   # Web
   flutter build web --release
   ```

---

## 开发规范

### 前端规范

1. **代码风格**
   - 遵循 Dart 官方风格指南
   - 使用 `analysis_options.yaml` 配置 lint 规则
   - 组件命名使用 PascalCase
   - 变量命名使用 camelCase

2. **状态管理**
   - 简单页面使用 StatefulWidget
   - 复杂状态考虑引入 Provider/Riverpod

3. **API 调用**
   - 统一使用 ApiClient 类
   - 所有请求返回 Result<T> 类型
   - 错误处理统一

4. **国际化**
   - 所有用户可见文本使用 l10n
   - 支持中英文切换

### 后端规范

1. **分层架构**
   - Controller: 处理 HTTP 请求
   - Service: 业务逻辑
   - Repository: 数据访问
   - Entity: 数据库映射
   - DTO: 数据传输

2. **命名规范**
   - Controller 以 Controller 结尾
   - Service 实现以 ServiceImpl 结尾
   - Repository 以 Repository 结尾
   - DTO 放在 request/response 包中

3. **异常处理**
   - 使用 BusinessException 处理业务异常
   - 全局异常处理器统一返回格式

4. **日志规范**
   - 使用 Lombok @Slf4j
   - 关键操作记录日志
   - 敏感信息不记录

---

## 测试策略

### 后端测试

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=UserControllerTest
```

### 前端测试

```bash
# 运行单元测试
flutter test

# 运行集成测试
flutter test integration_test
```

---

## 常见问题

### Q1: 前端无法连接后端

**解决方案**:

- 检查后端是否启动（默认端口 8080）
- Android 模拟器使用 `10.0.2.2` 访问宿主机
- Web 端使用 `localhost:8080`
- 检查 CORS 配置

### Q2: JWT Token 失效

**解决方案**:

- Token 有效期为 1 小时
- 过期后需要重新登录
- 可考虑实现 Refresh Token 机制

### Q3: 地理位置查询不生效

**解决方案**:

- 确保 MySQL 支持空间数据类型
- 检查 latitude 和 longitude 字段是否有值
- 验证 ST_Distance_Sphere 函数是否可用

### Q4: 活动状态未自动更新

**解决方案**:

- 检查 @Scheduled 注解是否启用
- 验证定时任务 cron 表达式
- 查看日志确认任务执行情况

### Q5: Web 平台地图缩放时出现错误

**问题描述**:
在 Chrome 浏览器中调试时，大幅度缩放地图可能报错：

```
Locations: Error adding location for E:/env/flutter/packages/flutter/lib/src/foundation/change_notifier.dart: FormatException: Unsupported URI form
```

**解决方案**:
已在 `map_picker_page.dart` 中应用以下优化：

1. 使用 `NetworkTileProvider` 作为瓦片提供器，避免缓存问题
2. 禁用 `retinaMode`（设置为 false），避免 Web 平台渲染问题

这些修改已有效缓解 Web 平台上的地图缩放问题。如果仍有问题，可以考虑：

- 降低最大缩放级别（如从 18.0 降到 17.0）
- 调整地图交互选项
- 优化瓦片加载策略

---

## 未来规划

### 功能扩展

1. **社交功能**
   - 用户关注系统
   - 活动评论和点赞
   - 消息通知

2. **支付集成**
   - 付费活动支持
   - 退款流程

3. **推荐系统**
   - 基于用户兴趣的活动推荐
   - 热门活动排行榜

4. **数据分析**
   - 活动参与统计
   - 用户行为分析

### 技术优化

1. **性能优化**
   - Redis 缓存热点数据
   - 数据库查询优化
   - 前端图片懒加载

2. **安全加固**
   - Refresh Token 机制
   - 接口限流
   - SQL 注入防护

3. **监控告警**
   - 接入 Prometheus + Grafana
   - 日志集中管理（ELK）
   - 异常告警通知

---

## 版本信息

- **项目名称**: MeetSpace
- **版本**: v1.0
- **开发时间**: 2026
- **前端版本**: Flutter 3.41.6-stable
- **后端版本**: Spring Boot 4.0.3

---

## 联系方式

如有问题或建议，请联系开发团队。

---

_本文档最后更新：2026-04-08_
