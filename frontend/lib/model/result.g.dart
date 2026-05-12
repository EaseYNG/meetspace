// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'result.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Result<T> _$ResultFromJson<T>(
  Map<String, dynamic> json,
  T Function(Object? json) fromJsonT,
) => Result<T>(
  code: $enumDecode(_$ResultCodeEnumMap, json['code']),
  msg: json['msg'] as String?,
  data: _$nullableGenericFromJson(json['data'], fromJsonT),
);

Map<String, dynamic> _$ResultToJson<T>(
  Result<T> instance,
  Object? Function(T value) toJsonT,
) => <String, dynamic>{
  'code': _$ResultCodeEnumMap[instance.code]!,
  'msg': instance.msg,
  'data': _$nullableGenericToJson(instance.data, toJsonT),
};

const _$ResultCodeEnumMap = {
  ResultCode.SUCCESS: 'SUCCESS',
  ResultCode.FAIL: 'FAIL',
  ResultCode.UNAUTHORIZED: 'UNAUTHORIZED',
  ResultCode.FORBIDDEN: 'FORBIDDEN',
  ResultCode.NOT_FOUND: 'NOT_FOUND',
  ResultCode.DUPLICATION: 'DUPLICATION',
  ResultCode.VALUE_ERROR: 'VALUE_ERROR',
  ResultCode.NO_SUCH_OBJECT: 'NO_SUCH_OBJECT',
  ResultCode.STATUS_ERROR: 'STATUS_ERROR',
  ResultCode.INTERNAL_SERVER_ERROR: 'INTERNAL_SERVER_ERROR',
};

T? _$nullableGenericFromJson<T>(
  Object? input,
  T Function(Object? json) fromJson,
) => input == null ? null : fromJson(input);

Object? _$nullableGenericToJson<T>(
  T? input,
  Object? Function(T value) toJson,
) => input == null ? null : toJson(input);
