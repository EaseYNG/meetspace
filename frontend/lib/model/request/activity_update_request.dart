import 'package:json_annotation/json_annotation.dart';

part 'activity_update_request.g.dart';

@JsonSerializable()
class ActivityUpdateRequest {
  final String? title;
  final DateTime? startTime;
  final DateTime? endTime;
  final DateTime? signupDeadline;
  final String? address;
  final int? minParticipants;
  final int? maxParticipants;
  final String? image;
  final String? description;
  @JsonKey(name: 'latitude')
  final double? latitude;
  @JsonKey(name: 'longitude')
  final double? longitude;

  ActivityUpdateRequest({
    this.title,
    this.startTime,
    this.endTime,
    this.signupDeadline,
    this.address,
    this.minParticipants,
    this.maxParticipants,
    this.image,
    this.description,
    this.latitude,
    this.longitude,
  });

  factory ActivityUpdateRequest.fromJson(Map<String, dynamic> json) =>
      _$ActivityUpdateRequestFromJson(json);
  Map<String, dynamic> toJson() => _$ActivityUpdateRequestToJson(this);
}
