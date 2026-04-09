import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';

class ApiClient {
  static final ApiClient _instance = ApiClient._internal();
  factory ApiClient() => _instance;
  ApiClient._internal();

  // 允许通过 `--dart-define=BASE_URL=http://<ip>:8080` 覆盖，便于真机调试
  // - Android 模拟器：通常用 http://10.0.2.2:8080
  // - Android 真机：需要用电脑的局域网 IP（例如 http://192.168.1.10:8080）
  static const String _baseUrlOverride = String.fromEnvironment(
    'BASE_URL',
    defaultValue: '',
  );

  static const List<String> _possibleHosts = [
    'localhost',
    '10.0.2.2',
    '127.0.0.1',
  ];
  String _baseUrl = 'http://localhost:8080';

  late Dio _dio;

  Dio get dio => _dio;

  Future<void> init() async {
    final prefs = await SharedPreferences.getInstance();
    final token = prefs.getString('token');

    if (_baseUrlOverride.trim().isNotEmpty) {
      _baseUrl = _baseUrlOverride.trim();
    } else if (kIsWeb) {
      _baseUrl = 'http://localhost:8080';
    } else if (defaultTargetPlatform == TargetPlatform.android) {
      // 默认使用 10.0.2.2 适配模拟器
      // 如果你运行了 `adb reverse tcp:8080 tcp:8080`，
      // 请通过 --dart-define=BASE_URL=http://localhost:8080 覆盖
      _baseUrl = 'http://localhost:8080';
    } else {
      // 其他平台（iOS、桌面端）尝试自动检测
      await _autoDetectHost();
    }

    print(
      '[ApiClient] baseUrl=$_baseUrl (override=${_baseUrlOverride.isNotEmpty}, '
      'platform=${kIsWeb ? 'web' : defaultTargetPlatform.name})',
    );

    _dio = Dio(
      BaseOptions(
        baseUrl: _baseUrl,
        connectTimeout: const Duration(seconds: 5),
        receiveTimeout: const Duration(seconds: 30),
        sendTimeout: const Duration(seconds: 30),
        headers: {
          if (token != null) 'Authorization': 'Bearer $token',
          'Content-Type': 'application/json',
        },
      ),
    );

    _dio.interceptors.add(
      LogInterceptor(
        request: true,
        requestHeader: true,
        requestBody: true,
        responseHeader: true,
        responseBody: true,
        error: true,
        logPrint: (object) => print('[Dio] $object'),
      ),
    );

    _dio.interceptors.add(
      InterceptorsWrapper(
        onRequest: (options, handler) async {
          final prefs = await SharedPreferences.getInstance();
          final token = prefs.getString('token');
          if (token != null) {
            options.headers['Authorization'] = 'Bearer $token';
          }
          print('[Dio Request] ${options.method} ${options.uri}');
          print('[Dio Request] Headers: ${options.headers}');
          if (options.data != null) {
            print('[Dio Request] Data: ${options.data}');
          }
          handler.next(options);
        },
        onResponse: (response, handler) {
          print('[Dio Response] Status: ${response.statusCode}');
          print('[Dio Response] Data: ${response.data}');
          handler.next(response);
        },
        onError: (error, handler) {
          print('[Dio Error] Type: ${error.type}');
          print('[Dio Error] Message: ${error.message}');
          print('[Dio Error] BaseUrl: ${_baseUrl}');
          if (error.response != null) {
            print('[Dio Error] Status: ${error.response?.statusCode}');
            print('[Dio Error] Data: ${error.response?.data}');
          } else {
            // 提供更详细的错误提示
            if (error.type == DioExceptionType.connectionTimeout) {
              print('[Dio Error] 连接超时 - 请检查后端服务是否启动');
            } else if (error.type == DioExceptionType.sendTimeout) {
              print('[Dio Error] 发送超时 - 网络可能不稳定');
            } else if (error.type == DioExceptionType.receiveTimeout) {
              print('[Dio Error] 接收超时 - 服务器响应慢');
            } else if (error.type == DioExceptionType.connectionError) {
              print('[Dio Error] 连接错误 - 请检查：');
              print('  1. 后端服务是否启动（端口 8080）');
              print('  2. Android 模拟器应使用 http://10.0.2.2:8080');
              print('  3. 防火墙是否阻止连接');
            }
          }
          handler.next(error);
        },
      ),
    );
  }

  Future<void> _autoDetectHost() async {
    for (final host in _possibleHosts) {
      final url = 'http://$host:8080/hi';
      print('Testing connection to $url');
      try {
        final testDio = Dio(
          BaseOptions(connectTimeout: const Duration(seconds: 2)),
        );
        final response = await testDio.get(url);
        if (response.statusCode == 200) {
          _baseUrl = 'http://$host:8080';
          print('Selected baseUrl: $_baseUrl');
          return;
        }
      } catch (e) {
        print('Host $host failed: $e');
        continue;
      }
    }
    print('Warning: Could not auto-detect host, using default: $_baseUrl');
  }

  Future<void> updateToken(String token) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('token', token);
    _dio.options.headers['Authorization'] = 'Bearer $token';
  }

  Future<void> clearToken() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove('token');
    _dio.options.headers.remove('Authorization');
  }

  Future<String?> getToken() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString('token');
  }
}
