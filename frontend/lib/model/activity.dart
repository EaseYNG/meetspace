import 'package:json_annotation/json_annotation.dart';
import 'activity_status.dart';

part 'activity.g.dart';

@JsonSerializable()
class Activity {
  final int id;
  final String title;
  final DateTime startTime;
  final DateTime endTime;
  final DateTime signupDeadline;
  final String address;
  final ActivityStatus status;
  @JsonKey(name: 'minParticipants', defaultValue: 0)
  final int minParticipants;
  @JsonKey(name: 'maxParticipants', defaultValue: 0)
  final int maxParticipants;
  final String? image;
  final String? description;
  final double? latitude;
  final double? longitude;

  Activity({
    required this.id,
    required this.title,
    required this.startTime,
    required this.endTime,
    required this.signupDeadline,
    required this.address,
    required this.status,
    required this.minParticipants,
    required this.maxParticipants,
    this.image,
    this.description,
    this.latitude,
    this.longitude,
  });

  factory Activity.fromJson(Map<String, dynamic> json) =>
      _$ActivityFromJson(json);
  Map<String, dynamic> toJson() => _$ActivityToJson(this);

  bool get isReady => status == ActivityStatus.READY;
  bool get isClosed => status == ActivityStatus.CLOSED;
  bool get isOverOrDeleted =>
      status == ActivityStatus.OVER || status == ActivityStatus.DELETED;
}
