import 'package:dio/dio.dart';
import 'package:shared_preferences/shared_preferences.dart';

class ApiClient {
  static final ApiClient _instance = ApiClient._internal();
  factory ApiClient() => _instance;
  ApiClient._internal();

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

    // Web 平台使用固定的 baseUrl，因为自动检测会受到 CORS 限制
    const useFixedHost = bool.fromEnvironment('dart.library.js_util');
    // Android 平台检测
    const isAndroid = bool.fromEnvironment('dart.library.io');

    if (useFixedHost) {
      _baseUrl = 'http://localhost:8080';
    } else if (isAndroid) {
      // Android 模拟器使用 10.0.2.2 访问宿主机
      _baseUrl = 'http://10.0.2.2:8080';
    } else {
      // 其他平台（iOS、桌面端）尝试自动检测
      await _autoDetectHost();
    }

    _dio = Dio(
      BaseOptions(
        baseUrl: _baseUrl,
        connectTimeout: const Duration(seconds: 30),
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
