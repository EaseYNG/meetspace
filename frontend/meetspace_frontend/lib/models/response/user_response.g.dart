// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'user_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

UserResponse _$UserResponseFromJson(Map<String, dynamic> json) => UserResponse(
  id: (json['id'] as num).toInt(),
  age: (json['age'] as num).toInt(),
  nickname: json['nickname'] as String,
  username: json['username'] as String,
  email: json['email'] as String,
  gender: json['gender'] as String,
  firstname: json['first_name'] as String,
  lastname: json['last_name'] as String,
);

Map<String, dynamic> _$UserResponseToJson(UserResponse instance) =>
    <String, dynamic>{
      'id': instance.id,
      'nickname': instance.nickname,
      'username': instance.username,
      'age': instance.age,
      'gender': instance.gender,
      'email': instance.email,
      'first_name': instance.firstname,
      'last_name': instance.lastname,
    };
