# Android 端 Dio 异常解决方案

## 问题原因

Android 模拟器使用 `localhost` 时，指向的是模拟器本身，而不是开发主机。需要特殊配置才能访问后端服务。

## 已修复的问题

### 1. ✅ 域名绑定问题

**修改文件**: `lib/api/api_client.dart`

- Android 平台强制使用 `http://10.0.2.2:8080`
- Web 平台使用 `http://localhost:8080`
- 其他平台（iOS、桌面）自动检测

### 2. ✅ 网络权限缺失

**修改文件**: `android/app/src/main/AndroidManifest.xml`

添加了必要的网络权限：
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### 3. ✅ HTTP 明文传输限制

**新增文件**: `android/app/src/main/res/xml/network_security_config.xml`

配置允许 HTTP 连接（开发环境）：
```xml
<network-security-config>
    <base-config cleartextTrafficPermitted="true">
        ...
    </base-config>
</network-security-config>
```

**修改文件**: `android/app/src/main/AndroidManifest.xml`

在 application 标签中添加：
```xml
android:networkSecurityConfig="@xml/network_security_config"
android:usesCleartextTraffic="true"
```

### 4. ✅ 错误提示优化

增强了 Dio 错误拦截器的日志输出，提供详细的错误原因和解决方案。

## 使用方法

### 1. 确保后端服务启动

```bash
cd ..
mvn spring-boot:run
```

确认后端在 `http://localhost:8080` 运行

### 2. 清理并重新构建 Android 应用

```bash
# 清理构建缓存
flutter clean

# 获取依赖
flutter pub get

# 重新运行
flutter run
```

### 3. 验证连接

运行后查看控制台日志，应该看到：
```
Android platform: Using baseUrl: http://10.0.2.2:8080
```

### 4. 测试 API

尝试执行任何操作（如登录、注册），现在应该不会再出现 Dio 异常。

## 常见问题排查

### Q1: 仍然出现连接错误

**检查步骤**:

1. **确认后端启动**:
   ```bash
   # 在浏览器或 Postman 中访问
   http://localhost:8080/user/test
   ```
   应该返回 "ok"

2. **检查防火墙**:
   - Windows 防火墙可能阻止连接
   - 临时关闭防火墙测试
   - 或添加 Java 进程到白名单

3. **查看日志**:
   ```bash
   # 查看 Android 日志
   adb logcat | grep -i dio
   ```

### Q2: 模拟器无法访问网络

**解决方案**:

1. **冷启动模拟器**:
   ```bash
   # 停止所有模拟器
   adb emu kill
   
   # 重新启动
   flutter emulators --launch <emulator_id>
   ```

2. **检查模拟器网络**:
   - 打开模拟器浏览器
   - 访问 `http://10.0.2.2:8080/user/test`
   - 应该能看到响应

### Q3: 使用真机调试

如果使用真机而非模拟器，需要：

1. **确保手机和电脑在同一 WiFi 网络**
2. **获取电脑 IP 地址**:
   ```bash
   # Windows
   ipconfig
   
   # 查找 IPv4 地址，如 192.168.1.100
   ```

3. **修改 api_client.dart**:
   ```dart
   _baseUrl = 'http://192.168.1.100:8080';
   ```

4. **确保后端允许外部访问**:
   - 检查防火墙设置
   - 可能需要配置 Spring Boot 绑定到 0.0.0.0

## 技术说明

### Android 模拟器网络地址

| 地址 | 说明 |
|------|------|
| `localhost` / `127.0.0.1` | 模拟器本身 |
| `10.0.2.2` | 开发主机（宿主机） |
| `10.0.2.2` | 等同于宿主机的 `127.0.0.1` |

### 为什么 Web 端正常

Web 端在浏览器中运行，浏览器的 `localhost` 直接指向开发主机，不需要特殊处理。

## 验证清单

- [ ] 后端服务已启动（端口 8080）
- [ ] AndroidManifest.xml 添加了网络权限
- [ ] network_security_config.xml 已创建并配置
- [ ] api_client.dart 使用正确的 baseUrl
- [ ] 执行了 `flutter clean` 和 `flutter pub get`
- [ ] 重新安装/运行了应用

## 日志示例

**成功的日志输出**:
```
Android platform: Using baseUrl: http://10.0.2.2:8080
[Dio Request] POST http://10.0.2.2:8080/user/login
[Dio Request] Headers: {Content-Type: application/json}
[Dio Response] Status: 200
[Dio Response] Data: {code: 200, msg: 登录成功！, data: eyJhbGc...}
```

**失败的日志（连接错误）**:
```
[Dio Error] Type: DioExceptionType.connectionError
[Dio Error] BaseUrl: http://10.0.2.2:8080
[Dio Error] 连接错误 - 请检查：
  1. 后端服务是否启动（端口 8080）
  2. Android 模拟器应使用 http://10.0.2.2:8080
  3. 防火墙是否阻止连接
```

## 更新记录

- **2026-04-08**: 修复 Android 端 Dio 连接问题
  - 添加网络权限
  - 配置网络安全性
  - 强制使用 10.0.2.2 访问宿主机
  - 增强错误日志

---

如果问题仍未解决，请查看完整的日志输出并联系开发团队。
