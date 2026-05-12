// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'activity_create_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ActivityCreateRequest _$ActivityCreateRequestFromJson(
  Map<String, dynamic> json,
) => ActivityCreateRequest(
  title: json['title'] as String,
  startTime: DateTime.parse(json['startTime'] as String),
  endTime: DateTime.parse(json['endTime'] as String),
  signupDeadline: DateTime.parse(json['signupDeadline'] as String),
  address: json['address'] as String,
  minParticipants: (json['minParticipants'] as num).toInt(),
  maxParticipants: (json['maxParticipants'] as num).toInt(),
  image: json['image'] as String?,
  description: json['description'] as String?,
  latitude: (json['latitude'] as num?)?.toDouble(),
  longitude: (json['longitude'] as num?)?.toDouble(),
);

Map<String, dynamic> _$ActivityCreateRequestToJson(
  ActivityCreateRequest instance,
) => <String, dynamic>{
  'title': instance.title,
  'startTime': instance.startTime.toIso8601String(),
  'endTime': instance.endTime.toIso8601String(),
  'signupDeadline': instance.signupDeadline.toIso8601String(),
  'address': instance.address,
  'minParticipants': instance.minParticipants,
  'maxParticipants': instance.maxParticipants,
  'image': instance.image,
  'description': instance.description,
  'latitude': instance.latitude,
  'longitude': instance.longitude,
};
