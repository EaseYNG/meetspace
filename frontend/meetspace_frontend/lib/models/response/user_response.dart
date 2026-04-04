import 'package:json_annotation/json_annotation.dart';

part 'user_response.g.dart';

@JsonSerializable()
class UserResponse {
  final int id;
  final String nickname;
  final String username;
  final int age;
  final String gender;
  final String email;
  @JsonKey(name: 'first_name')
  final String firstname;
  @JsonKey(name: 'last_name')
  final String lastname;

  UserResponse({
    required this.id,
    required this.age,
    required this.nickname,
    required this.username,
    required this.email,
    required this.gender,
    required this.firstname,
    required this.lastname,
  });

  factory UserResponse.fromJson(Map<String, dynamic> json) =>
      _$UserResponseFromJson(json);

  Map<String, dynamic> toJson() => _$UserResponseToJson(this);
}
