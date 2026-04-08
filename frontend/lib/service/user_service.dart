import '../api/api_client.dart';
import '../model/result.dart';
import '../model/profile.dart';

class UserService {
  final ApiClient _apiClient = ApiClient();

  Future<Result<Profile>> getProfile() async {
    final response = await _apiClient.dio.get('/home/profile');
    return Result<Profile>.fromJson(
      response.data,
      (json) => Profile.fromJson(json as Map<String, dynamic>),
    );
  }

  Future<Result<void>> updateProfile(Profile profile) async {
    final response = await _apiClient.dio.post(
      '/home/profile',
      data: profile.toJson(),
    );
    return Result<void>.fromJson(response.data, (json) => null);
  }
}
