import 'package:json_annotation/json_annotation.dart';

part 'result.g.dart';

enum ResultCode { SUCCESS, FAIL, UNAUTHORIZED, FORBIDDEN, NOT_FOUND, DUPLICATION, VALUE_ERROR, NO_SUCH_OBJECT, STATUS_ERROR, INTERNAL_SERVER_ERROR }

@JsonSerializable(genericArgumentFactories: true)
class Result<T> {
  final ResultCode code;
  final String? msg;
  final T? data;

  Result({required this.code, this.msg, this.data});

  factory Result.success(T data, {String? msg}) => Result(code: ResultCode.SUCCESS, msg: msg, data: data);
  factory Result.fail(T data, {String? msg}) => Result(code: ResultCode.FAIL, msg: msg, data: data);
  factory Result.error(ResultCode code, T data, {String? msg}) => Result(code: code, msg: msg, data: data);

  factory Result.fromJson(
      Map<String, dynamic> json, T Function(Object? json) fromJsonT) =>
      _$ResultFromJson(json, fromJsonT);

  Map<String, dynamic> toJson(Object? Function(T value) toJsonT) =>
      _$ResultToJson(this, toJsonT);

  bool get isSuccess => code == ResultCode.SUCCESS;
  
  @override
  String toString() => 'Result{code: $code, msg: $msg, data: $data}';
}
