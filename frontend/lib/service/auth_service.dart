import '../api/api_client.dart';
import '../model/result.dart';
import '../model/request/auth_request.dart';
import '../model/request/register_request.dart';

class AuthService {
  final ApiClient _apiClient = ApiClient();

  Future<Result<void>> register(RegisterRequest request) async {
    final response = await _apiClient.dio.post(
      '/user/register',
      data: request.toJson(),
    );
    return Result<void>.fromJson(response.data, (json) {});
  }

  Future<Result<String>> login(AuthRequest request) async {
    final response = await _apiClient.dio.post(
      '/user/login',
      data: request.toJson(),
    );
    final result = Result<String>.fromJson(
      response.data,
      (json) => json as String,
    );
    if (result.isSuccess && result.data != null) {
      await _apiClient.updateToken(result.data!);
    }
    return result;
  }

  Future<void> logout() async {
    await _apiClient.clearToken();
  }
}
