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

  Future<Result<void>> quitActivity(int activityId) async {
    final response = await _apiClient.dio.get('/activity/quit/$activityId');
    return Result<void>.fromJson(response.data, (json) {});
  }

  Future<Result<List<Activity>>> getRelatedActivities() async {
    final response = await _apiClient.dio.get('/activity/related');
    return Result<List<Activity>>.fromJson(
      response.data,
      (json) => (json as List)
          .map((e) => Activity.fromJson(e as Map<String, dynamic>))
          .toList(),
    );
  }

  Future<Result<List<Activity>>> getSignedUpActivities() async {
    final response = await _apiClient.dio.get('/activity/signed_up');
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

  Future<Result<Activity>> getActivity(int activityId) async {
    final response = await _apiClient.dio.get('/activity/$activityId');
    return Result<Activity>.fromJson(
      response.data,
      (json) => Activity.fromJson(json as Map<String, dynamic>),
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
