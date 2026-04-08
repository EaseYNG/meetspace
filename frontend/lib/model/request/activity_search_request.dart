import 'package:json_annotation/json_annotation.dart';

part 'activity_search_request.g.dart';

@JsonSerializable()
class ActivitySearchRequest {
  final DateTime? startTime;
  final DateTime? endTime;
  final double? longitude;
  final double? latitude;
  final double? radiusKm;
  final int? min;
  final int? max;

  ActivitySearchRequest({
    this.startTime,
    this.endTime,
    this.longitude,
    this.latitude,
    this.radiusKm,
    this.min,
    this.max,
  });

  factory ActivitySearchRequest.fromJson(Map<String, dynamic> json) =>
      _$ActivitySearchRequestFromJson(json);
  Map<String, dynamic> toJson() => _$ActivitySearchRequestToJson(this);
}
