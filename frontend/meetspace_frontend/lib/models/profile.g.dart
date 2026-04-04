// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'profile.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Profile _$ProfileFromJson(Map<String, dynamic> json) => Profile(
  age: (json['age'] as num?)?.toInt(),
  gender: json['gender'] as String?,
  email: json['email'] as String?,
  firstname: json['first_name'] as String?,
  lastname: json['last_name'] as String?,
);

Map<String, dynamic> _$ProfileToJson(Profile instance) => <String, dynamic>{
  'age': instance.age,
  'gender': instance.gender,
  'email': instance.email,
  'first_name': instance.firstname,
  'last_name': instance.lastname,
};
