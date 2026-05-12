import 'package:json_annotation/json_annotation.dart';

enum ActivityStatus {
  @JsonValue('READY')
  READY,
  @JsonValue('CLOSED')
  CLOSED,
  @JsonValue('DELETED')
  DELETED,
  @JsonValue('OVER')
  OVER,
}
