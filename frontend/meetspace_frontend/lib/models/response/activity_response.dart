import 'package:json_annotation/json_annotation.dart';

part 'activity_response.g.dart';

DateTime _dateTimeFromJson(String temp) {
  return DateTime.parse(temp);
}

String _dateTimeToJson(DateTime temp) {
  return temp.toIso8601String();
}

@JsonSerializable()
class ActivityResponse {
  final int id;
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
  final String status;

  String? image;
  String? description;

  ActivityResponse({
    required this.id,
    required this.title,
    required this.startTime,
    required this.endTime,
    required this.signupDeadline,
    required this.address,
    required this.status,
    this.image,
    this.description,
  });

  factory ActivityResponse.fromJson(Map<String, dynamic> json) =>
      _$ActivityResponseFromJson(json);

  Map<String, dynamic> toJson() => _$ActivityResponseToJson(this);
}
