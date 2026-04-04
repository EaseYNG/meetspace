import 'package:json_annotation/json_annotation.dart';

part 'profile.g.dart';

@JsonSerializable()
class Profile {
  int? age;
  String? gender;
  String? email;
  @JsonKey(name: "first_name")
  String? firstname;
  @JsonKey(name: "last_name")
  String? lastname;

  Profile({this.age, this.gender, this.email, this.firstname, this.lastname});

  factory Profile.fromJson(Map<String, dynamic> json) =>
      _$ProfileFromJson(json);

  Map<String, dynamic> toJson() => _$ProfileToJson(this);
}
