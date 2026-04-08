// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'activity.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Activity _$ActivityFromJson(Map<String, dynamic> json) => Activity(
  id: (json['id'] as num).toInt(),
  title: json['title'] as String,
  startTime: DateTime.parse(json['startTime'] as String),
  endTime: DateTime.parse(json['endTime'] as String),
  signupDeadline: DateTime.parse(json['signupDeadline'] as String),
  address: json['address'] as String,
  status: $enumDecode(_$ActivityStatusEnumMap, json['status']),
  minParticipants: (json['minParticipants'] as num).toInt(),
  maxParticipants: (json['maxParticipants'] as num).toInt(),
  image: json['image'] as String?,
  description: json['description'] as String?,
  latitude: (json['latitude'] as num?)?.toDouble(),
  longitude: (json['longitude'] as num?)?.toDouble(),
);

Map<String, dynamic> _$ActivityToJson(Activity instance) => <String, dynamic>{
  'id': instance.id,
  'title': instance.title,
  'startTime': instance.startTime.toIso8601String(),
  'endTime': instance.endTime.toIso8601String(),
  'signupDeadline': instance.signupDeadline.toIso8601String(),
  'address': instance.address,
  'status': _$ActivityStatusEnumMap[instance.status]!,
  'minParticipants': instance.minParticipants,
  'maxParticipants': instance.maxParticipants,
  'image': instance.image,
  'description': instance.description,
  'latitude': instance.latitude,
  'longitude': instance.longitude,
};

const _$ActivityStatusEnumMap = {
  ActivityStatus.READY: 'READY',
  ActivityStatus.CLOSED: 'CLOSED',
  ActivityStatus.DELETED: 'DELETED',
  ActivityStatus.OVER: 'OVER',
};
