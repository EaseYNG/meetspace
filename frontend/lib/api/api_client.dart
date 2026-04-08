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
    if (useFixedHost) {
      _baseUrl = 'http://localhost:8080';
      print('Web platform: Using fixed baseUrl: $_baseUrl');
    } else {
      // 非 Web 平台尝试自动检测
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
          print('[Dio Error] Stack: ${error.error}');
          if (error.response != null) {
            print('[Dio Error] Status: ${error.response?.statusCode}');
            print('[Dio Error] Data: ${error.response?.data}');
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
