# meetspace_frontend

meetspace 的前端部分

## 项目结构

```
lib/
├── main.dart
│
├── common/                            # 公共模块
│   ├── constants/
│   │   ├── constants.dart             # 颜色、字符串、API 地址
│   │   └── app_routes.dart            # 路由名称常量
│   ├── utils/
│   │   ├── date_util.dart             # 时间格式化
│   │   ├── storage_util.dart          # SharedPreferences 封装
│   │   └── http_util.dart             # Dio 封装
│   └── widgets/
│       ├── loading_dialog.dart
│       ├── error_view.dart
│       └── custom_button.dart
│
├── models/                            # 数据模型 - entity, dto
│   ├── activity.dart
│   ├── user.dart
│   ├── registration.dart
│   └── api_response.dart
│
├── services/                          # 服务层（API 调用）
│   ├── api_service.dart               # Dio 实例
│   ├── activity_service.dart
│   ├── auth_service.dart
│   └── storage_service.dart
│
├── pages/                             # 页面 - controller
│   ├── splash_page.dart
│   ├── login_page.dart
│   ├── register_page.dart
│   ├── home_page.dart
│   ├── activity_list_page.dart
│   ├── activity_detail_page.dart
│   ├── create_activity_page.dart
│   └── profile_page.dart
│
├── widgets/                           # 复用组件
│   ├── activity_card.dart
│   ├── status_badge.dart
│   └── bottom_nav_bar.dart
│
├── providers/                         # 状态管理（Riverpod）
│   ├── auth_provider.dart
│   ├── activity_provider.dart
│   └── theme_provider.dart
│
└── theme/                             # 主题配置
    ├── app_theme.dart
    └── app_colors.dart
```

