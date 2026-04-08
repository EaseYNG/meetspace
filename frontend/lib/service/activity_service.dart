import '../api/api_client.dart';
import '../model/result.dart';
import '../model/activity.dart';
import '../model/request/activity_create_request.dart';
import '../model/request/activity_update_request.dart';
import '../model/request/activity_search_request.dart';

class ActivityService {
  final ApiClient _apiClient = ApiClient();

  Future<Result<void>> createActivity(ActivityCreateRequest request) async {
    final response = await _apiClient.dio.post(
      '/activity/create',
      data: request.toJson(),
    );
    return Result<void>.fromJson(response.data, (json) {});
  }

  Future<Result<void>> updateActivity(
    int activityId,
    ActivityUpdateRequest request,
  ) async {
    final response = await _apiClient.dio.patch(
      '/activity/update/$activityId',
      data: request.toJson(),
    );
    return Result<void>.fromJson(response.data, (json) {});
  }

  Future<Result<void>> deleteActivity(int activityId) async {
    final response = await _apiClient.dio.delete(
      '/activity/delete/$activityId',
    );
    return Result<void>.fromJson(response.data, (json) {});
  }

  Future<Result<void>> signupActivity(int activityId) async {
    final response = await _apiClient.dio.get('/activity/signup/$activityId');
    return Result<void>.fromJson(response.data, (json) {});
  }

  Future<Result<List<Activity>>> getParticipatedActivities() async {
    final response = await _apiClient.dio.get('/activity/participated');
    return Result<List<Activity>>.fromJson(
      response.data,
      (json) => (json as List)
          .map((e) => Activity.fromJson(e as Map<String, dynamic>))
          .toList(),
    );
  }

  Future<Result<List<Activity>>> getCreatedActivities() async {
    final response = await _apiClient.dio.get('/activity/created');
    return Result<List<Activity>>.fromJson(
      response.data,
      (json) => (json as List)
          .map((e) => Activity.fromJson(e as Map<String, dynamic>))
          .toList(),
    );
  }

  Future<Result<List<Activity>>> searchActivities(
    ActivitySearchRequest request,
  ) async {
    final response = await _apiClient.dio.post(
      '/activity/search',
      data: request.toJson(),
    );
    return Result<List<Activity>>.fromJson(
      response.data,
      (json) => (json as List)
          .map((e) => Activity.fromJson(e as Map<String, dynamic>))
          .toList(),
    );
  }

  Future<Result<List<Activity>>> getAllActivities() async {
    final response = await _apiClient.dio.get('/activity/list');
    return Result<List<Activity>>.fromJson(
      response.data,
      (json) => (json as List)
          .map((e) => Activity.fromJson(e as Map<String, dynamic>))
          .toList(),
    );
  }
}
