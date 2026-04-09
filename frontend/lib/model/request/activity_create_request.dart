import 'package:json_annotation/json_annotation.dart';

part 'activity_create_request.g.dart';

@JsonSerializable()
class ActivityCreateRequest {
  final String title;
  final DateTime startTime;
  final DateTime endTime;
  final DateTime signupDeadline;
  final String address;
  final int minParticipants;
  final int maxParticipants;
  final String? image;
  final String? description;
  final double? latitude;
  final double? longitude;

  ActivityCreateRequest({
    required this.title,
    required this.startTime,
    required this.endTime,
    required this.signupDeadline,
    required this.address,
    required this.minParticipants,
    required this.maxParticipants,
    this.image,
    this.description,
    this.latitude,
    this.longitude,
  });

  factory ActivityCreateRequest.fromJson(Map<String, dynamic> json) =>
      _$ActivityCreateRequestFromJson(json);
  Map<String, dynamic> toJson() => _$ActivityCreateRequestToJson(this);
}
