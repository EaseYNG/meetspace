import 'package:json_annotation/json_annotation.dart';

part 'activity_update_request.g.dart';

@JsonSerializable()
class ActivityUpdateRequest {
  final String? title;
  final DateTime? startTime;
  final DateTime? endTime;
  final DateTime? signupDeadline;
  final String? address;
  final String? image;
  final String? description;

  ActivityUpdateRequest({
    this.title,
    this.startTime,
    this.endTime,
    this.signupDeadline,
    this.address,
    this.image,
    this.description,
  });

  factory ActivityUpdateRequest.fromJson(Map<String, dynamic> json) =>
      _$ActivityUpdateRequestFromJson(json);
  Map<String, dynamic> toJson() => _$ActivityUpdateRequestToJson(this);
}
