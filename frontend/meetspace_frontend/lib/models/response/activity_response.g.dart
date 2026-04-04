// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'activity_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ActivityResponse _$ActivityResponseFromJson(Map<String, dynamic> json) =>
    ActivityResponse(
      id: (json['id'] as num).toInt(),
      title: json['title'] as String,
      startTime: _dateTimeFromJson(json['start_time'] as String),
      endTime: _dateTimeFromJson(json['end_time'] as String),
      signupDeadline: _dateTimeFromJson(json['signup_deadline'] as String),
      address: json['address'] as String,
      status: json['status'] as String,
      image: json['image'] as String?,
      description: json['description'] as String?,
    );

Map<String, dynamic> _$ActivityResponseToJson(ActivityResponse instance) =>
    <String, dynamic>{
      'id': instance.id,
      'title': instance.title,
      'start_time': _dateTimeToJson(instance.startTime),
      'end_time': _dateTimeToJson(instance.endTime),
      'signup_deadline': _dateTimeToJson(instance.signupDeadline),
      'address': instance.address,
      'status': instance.status,
      'image': instance.image,
      'description': instance.description,
    };
