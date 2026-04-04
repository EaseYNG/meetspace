import 'package:json_annotation/json_annotation.dart';

part 'update_activity_request.g.dart';

DateTime? _dateTimeFromJson(String? temp) {
  return temp == null ? null : DateTime.parse(temp);
}

String? _dateTimeToJson(DateTime? temp) {
  return temp?.toIso8601String();
}

@JsonSerializable()
class UpdateActivityRequest {
  String? title;

  @JsonKey(
    name: 'start_time',
    fromJson: _dateTimeFromJson,
    toJson: _dateTimeToJson,
  )
  DateTime? startTime;

  @JsonKey(
    name: 'end_time',
    fromJson: _dateTimeFromJson,
    toJson: _dateTimeToJson,
  )
  DateTime? endTime;

  @JsonKey(
    name: 'signup_deadline',
    fromJson: _dateTimeFromJson,
    toJson: _dateTimeToJson,
  )
  DateTime? signupDeadline;

  String? address;
  String? image;
  String? description;

  UpdateActivityRequest({
    this.title,
    this.startTime,
    this.endTime,
    this.signupDeadline,
    this.address,
    this.image,
    this.description,
  });

  factory UpdateActivityRequest.fromJson(Map<String, dynamic> json) =>
      _$UpdateActivityRequestFromJson(json);

  Map<String, dynamic> toJson() => _$UpdateActivityRequestToJson(this);
}
