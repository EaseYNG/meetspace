import 'package:json_annotation/json_annotation.dart';

part 'create_activity_request.g.dart';

DateTime _dateTimeFromJson(String temp) {
  return DateTime.parse(temp);
}

String _dateTimeToJson(DateTime temp) {
  return temp.toIso8601String();
}

@JsonSerializable()
class CreateActivityRequest {
  final String title;

  @JsonKey(
    name: 'start_time',
    fromJson: _dateTimeFromJson,
    toJson: _dateTimeToJson,
  )
  final DateTime startTime;
  @JsonKey(
    name: 'end_time',
    fromJson: _dateTimeFromJson,
    toJson: _dateTimeToJson,
  )
  final DateTime endTime;
  @JsonKey(
    name: 'signup_deadline',
    fromJson: _dateTimeFromJson,
    toJson: _dateTimeToJson,
  )
  final DateTime signupDeadline;
  final String address;
  String? image;
  String? description;

  CreateActivityRequest({
    required this.title,
    required this.startTime,
    required this.endTime,
    required this.signupDeadline,
    required this.address,
    this.image,
    this.description,
  });

  factory CreateActivityRequest.fromJson(Map<String, dynamic> json) =>
      _$CreateActivityRequestFromJson(json);

  Map<String, dynamic> toJson() => _$CreateActivityRequestToJson(this);
}
