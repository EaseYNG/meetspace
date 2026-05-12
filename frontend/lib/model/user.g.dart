// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'user.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

User _$UserFromJson(Map<String, dynamic> json) => User(
  id: (json['id'] as num).toInt(),
  nickname: json['nickname'] as String,
  username: json['username'] as String,
  age: (json['age'] as num?)?.toInt(),
  gender: json['gender'] as String?,
  email: json['email'] as String?,
  firstname: json['firstname'] as String?,
  lastname: json['lastname'] as String?,
);

Map<String, dynamic> _$UserToJson(User instance) => <String, dynamic>{
  'id': instance.id,
  'nickname': instance.nickname,
  'username': instance.username,
  'age': instance.age,
  'gender': instance.gender,
  'email': instance.email,
  'firstname': instance.firstname,
  'lastname': instance.lastname,
};
