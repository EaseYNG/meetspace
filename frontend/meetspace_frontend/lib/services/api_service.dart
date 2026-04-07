import 'dart:convert';
import 'package:http/http.dart' as http;
import 'token_service.dart';

class ApiService {
  // Android 真机 USB 调试时，adb reverse tcp:8080 tcp:8080 已将手机端口转发到开发机
  // 所以直接用 localhost 即可
  static const String baseUrl = 'http://localhost:8080';

  // ─── 内部 helper ─────────────────────────────────────
  static Future<Map<String, String>> _authHeaders() async {
    final token = await TokenService.getToken();
    return {
      'Content-Type': 'application/json',
      if (token != null) 'Authorization': 'Bearer $token',
    };
  }

  static Map<String, dynamic> _parseBody(http.Response res) {
    final body = jsonDecode(res.body) as Map<String, dynamic>;
    return body;
  }

  // ─── 用户 ─────────────────────────────────────────────
  static Future<Map<String, dynamic>> register({
    required String nickname,
    required String username,
    required String password,
  }) async {
    final res = await http.post(
      Uri.parse('$baseUrl/user/register'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        'nickname': nickname,
        'username': username,
        'password': password,
      }),
    );
    return _parseBody(res);
  }

  static Future<Map<String, dynamic>> login({
    required String username,
    required String password,
  }) async {
    final res = await http.post(
      Uri.parse('$baseUrl/user/login'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'username': username, 'password': password}),
    );
    return _parseBody(res);
  }

  // ─── Profile ──────────────────────────────────────────
  static Future<Map<String, dynamic>> getProfile() async {
    final res = await http.get(
      Uri.parse('$baseUrl/home/profile'),
      headers: await _authHeaders(),
    );
    return _parseBody(res);
  }

  static Future<Map<String, dynamic>> setProfile(
      Map<String, dynamic> profile) async {
    final res = await http.post(
      Uri.parse('$baseUrl/home/profile'),
      headers: await _authHeaders(),
      body: jsonEncode(profile),
    );
    return _parseBody(res);
  }

  // ─── 活动 ─────────────────────────────────────────────
  static Future<Map<String, dynamic>> createActivity(
      Map<String, dynamic> data) async {
    final res = await http.post(
      Uri.parse('$baseUrl/activity/create'),
      headers: await _authHeaders(),
      body: jsonEncode(data),
    );
    return _parseBody(res);
  }

  static Future<Map<String, dynamic>> updateActivity(
      int id, Map<String, dynamic> data) async {
    final res = await http.patch(
      Uri.parse('$baseUrl/activity/update/$id'),
      headers: await _authHeaders(),
      body: jsonEncode(data),
    );
    return _parseBody(res);
  }

  static Future<Map<String, dynamic>> deleteActivity(int id) async {
    final res = await http.delete(
      Uri.parse('$baseUrl/activity/delete/$id'),
      headers: await _authHeaders(),
    );
    return _parseBody(res);
  }

  static Future<Map<String, dynamic>> getMyActivities() async {
    final res = await http.get(
      Uri.parse('$baseUrl/activity/created'),
      headers: await _authHeaders(),
    );
    return _parseBody(res);
  }

  static Future<Map<String, dynamic>> getParticipatedActivities() async {
    final res = await http.get(
      Uri.parse('$baseUrl/activity/participated'),
      headers: await _authHeaders(),
    );
    return _parseBody(res);
  }

  static Future<Map<String, dynamic>> signupActivity(int activityId) async {
    final res = await http.get(
      Uri.parse('$baseUrl/activity/signup/$activityId'),
      headers: await _authHeaders(),
    );
    return _parseBody(res);
  }

  /// 获取所有活动（活动广场用）—— 暂时复用 /activity/list
  static Future<Map<String, dynamic>> getAllActivities() async {
    final res = await http.get(
      Uri.parse('$baseUrl/activity/list'),
      headers: await _authHeaders(),
    );
    return _parseBody(res);
  }
}
