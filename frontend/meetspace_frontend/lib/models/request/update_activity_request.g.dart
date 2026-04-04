// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'update_activity_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

UpdateActivityRequest _$UpdateActivityRequestFromJson(
  Map<String, dynamic> json,
) => UpdateActivityRequest(
  title: json['title'] as String?,
  startTime: _dateTimeFromJson(json['start_time'] as String?),
  endTime: _dateTimeFromJson(json['end_time'] as String?),
  signupDeadline: _dateTimeFromJson(json['signup_deadline'] as String?),
  address: json['address'] as String?,
  image: json['image'] as String?,
  description: json['description'] as String?,
);

Map<String, dynamic> _$UpdateActivityRequestToJson(
  UpdateActivityRequest instance,
) => <String, dynamic>{
  'title': instance.title,
  'start_time': _dateTimeToJson(instance.startTime),
  'end_time': _dateTimeToJson(instance.endTime),
  'signup_deadline': _dateTimeToJson(instance.signupDeadline),
  'address': instance.address,
  'image': instance.image,
  'description': instance.description,
};
