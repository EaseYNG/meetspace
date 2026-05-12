// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'activity_update_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ActivityUpdateRequest _$ActivityUpdateRequestFromJson(
  Map<String, dynamic> json,
) => ActivityUpdateRequest(
  title: json['title'] as String?,
  startTime: json['startTime'] == null
      ? null
      : DateTime.parse(json['startTime'] as String),
  endTime: json['endTime'] == null
      ? null
      : DateTime.parse(json['endTime'] as String),
  signupDeadline: json['signupDeadline'] == null
      ? null
      : DateTime.parse(json['signupDeadline'] as String),
  address: json['address'] as String?,
  minParticipants: (json['minParticipants'] as num?)?.toInt(),
  maxParticipants: (json['maxParticipants'] as num?)?.toInt(),
  image: json['image'] as String?,
  description: json['description'] as String?,
  latitude: (json['latitude'] as num?)?.toDouble(),
  longitude: (json['longitude'] as num?)?.toDouble(),
);

Map<String, dynamic> _$ActivityUpdateRequestToJson(
  ActivityUpdateRequest instance,
) => <String, dynamic>{
  'title': instance.title,
  'startTime': instance.startTime?.toIso8601String(),
  'endTime': instance.endTime?.toIso8601String(),
  'signupDeadline': instance.signupDeadline?.toIso8601String(),
  'address': instance.address,
  'minParticipants': instance.minParticipants,
  'maxParticipants': instance.maxParticipants,
  'image': instance.image,
  'description': instance.description,
  'latitude': instance.latitude,
  'longitude': instance.longitude,
};
