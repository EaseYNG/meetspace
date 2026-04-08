import 'package:json_annotation/json_annotation.dart';

part 'profile.g.dart';

@JsonSerializable()
class Profile {
  final int age;
  final String gender;
  final String email;
  final String firstname;
  final String lastname;

  Profile({
    required this.age,
    required this.gender,
    required this.email,
    required this.firstname,
    required this.lastname,
  });

  factory Profile.fromJson(Map<String, dynamic> json) => _$ProfileFromJson(json);
  Map<String, dynamic> toJson() => _$ProfileToJson(this);
}
