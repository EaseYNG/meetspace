import 'package:json_annotation/json_annotation.dart';
import 'profile.dart';

part 'user.g.dart';

@JsonSerializable()
class User {
  final int id;
  final String nickname;
  final String username;
  final int? age;
  final String? gender;
  final String? email;
  final String? firstname;
  final String? lastname;

  User({
    required this.id,
    required this.nickname,
    required this.username,
    this.age,
    this.gender,
    this.email,
    this.firstname,
    this.lastname,
  });

  factory User.fromJson(Map<String, dynamic> json) => _$UserFromJson(json);
  Map<String, dynamic> toJson() => _$UserToJson(this);

  Profile toProfile() {
    return Profile(
      age: age ?? 0,
      gender: gender ?? '',
      email: email ?? '',
      firstname: firstname ?? '',
      lastname: lastname ?? '',
    );
  }
}
