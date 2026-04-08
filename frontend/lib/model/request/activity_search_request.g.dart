// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'activity_search_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ActivitySearchRequest _$ActivitySearchRequestFromJson(
  Map<String, dynamic> json,
) => ActivitySearchRequest(
  startTime: json['startTime'] == null
      ? null
      : DateTime.parse(json['startTime'] as String),
  endTime: json['endTime'] == null
      ? null
      : DateTime.parse(json['endTime'] as String),
  longitude: (json['longitude'] as num?)?.toDouble(),
  latitude: (json['latitude'] as num?)?.toDouble(),
  radiusKm: (json['radiusKm'] as num?)?.toDouble(),
  min: (json['min'] as num?)?.toInt(),
  max: (json['max'] as num?)?.toInt(),
);

Map<String, dynamic> _$ActivitySearchRequestToJson(
  ActivitySearchRequest instance,
) => <String, dynamic>{
  'startTime': instance.startTime?.toIso8601String(),
  'endTime': instance.endTime?.toIso8601String(),
  'longitude': instance.longitude,
  'latitude': instance.latitude,
  'radiusKm': instance.radiusKm,
  'min': instance.min,
  'max': instance.max,
};
