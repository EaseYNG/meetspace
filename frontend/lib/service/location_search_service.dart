import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';
import 'package:latlong2/latlong.dart';

/// POI 搜索结果模型
class PoiItem {
  final String id;
  final String name;
  final String address;
  final double latitude;
  final double longitude;
  final String? tel;
  final String? typecode;

  PoiItem({
    required this.id,
    required this.name,
    required this.address,
    required this.latitude,
    required this.longitude,
    this.tel,
    this.typecode,
  });

  LatLng get latLng => LatLng(latitude, longitude);

  /// 从高德地图 API 响应中解析
  factory PoiItem.fromJson(Map<String, dynamic> json) {
    final location = (json['location'] as String? ?? '').split(',');
    return PoiItem(
      id: json['id'] as String? ?? '',
      name: json['name'] as String? ?? '未命名',
      address: json['address'] as String? ?? '',
      latitude: location.length > 1
          ? double.tryParse(location[1]) ?? 0.0
          : double.tryParse(json['latitude'] as String? ?? '0') ?? 0.0,
      longitude: location.isNotEmpty
          ? double.tryParse(location[0]) ?? 0.0
          : double.tryParse(json['longitude'] as String? ?? '0') ?? 0.0,
      tel: json['tel'] as String?,
      typecode: json['typecode'] as String?,
    );
  }
}

/// 输入提示结果
class InputTip {
  final String name;
  final String address;
  final double? latitude;
  final double? longitude;
  final String? adcode;

  InputTip({
    required this.name,
    required this.address,
    this.latitude,
    this.longitude,
    this.adcode,
  });

  factory InputTip.fromJson(Map<String, dynamic> json) {
    final location = (json['location'] as String? ?? '').split(',');
    return InputTip(
      name: json['name'] as String? ?? '',
      address: json['district'] as String? ?? '',
      latitude: location.length > 1 ? double.tryParse(location[1]) : null,
      longitude: location.isNotEmpty ? double.tryParse(location[0]) : null,
      adcode: json['adcode'] as String?,
    );
  }

  LatLng? get latLng => latitude != null && longitude != null
      ? LatLng(latitude!, longitude!)
      : null;
}

/// 高德地图位置搜索服务（单例模式）
class LocationSearchService {
  static final LocationSearchService _instance =
      LocationSearchService._internal();

  factory LocationSearchService() => _instance;

  LocationSearchService._internal();

  final Dio _dio = Dio();

  // 高德地图 API Key（根据平台区分）
  static const String _webApiKey = '2d6ee8e3071bf17f2d0694af2afc3b17';
  static const String _androidApiKey = '7907bea639e73c134f435d6cd2aa2120';

  String get _currentApiKey {
    return defaultTargetPlatform == TargetPlatform.android
        ? _androidApiKey
        : _webApiKey;
  }

  /// 初始化服务（可选，用于预热 Dio）
  Future<void> init() async {
    _dio.options.connectTimeout = const Duration(seconds: 10);
    _dio.options.receiveTimeout = const Duration(seconds: 10);
  }

  /// 周边 POI 搜索
  /// [center] 搜索中心点经纬度
  /// [keyword] 搜索关键词（如"餐厅"、"电影院"等）
  /// [radius] 搜索半径，单位：米，默认 1000
  /// [offset] 每页记录数，最多 20，默认 20
  /// [pageIndex] 分页页码，从 1 开始，默认 1
  Future<List<PoiItem>> searchNearbyPoi({
    required LatLng center,
    required String keyword,
    int radius = 1000,
    int offset = 20,
    int pageIndex = 1,
  }) async {
    try {
      final response = await _dio.get(
        'https://restapi.amap.com/v3/place/around',
        queryParameters: {
          'key': _currentApiKey,
          'location': '${center.longitude},${center.latitude}',
          'keywords': keyword,
          'radius': radius,
          'offset': offset,
          'page': pageIndex,
          'output': 'json',
        },
      );

      if (response.statusCode == 200) {
        final data = response.data;
        if (data['status'] == '1' && data['pois'] != null) {
          final pois = List<Map<String, dynamic>>.from(data['pois'] as List);
          return pois.map((poi) => PoiItem.fromJson(poi)).toList();
        } else {
          print('高德API返回状态码非1: ${data['status']}, 信息: ${data['info']}');
          return [];
        }
      }
      return [];
    } catch (e) {
      print('周边 POI 搜索异常: $e');
      return [];
    }
  }

  /// 文本搜索（全国范围）
  /// [keywords] 搜索关键词
  /// [city] 城市名（如"北京"），不指定则全国搜索
  /// [offset] 每页记录数，最多 20，默认 20
  /// [pageIndex] 分页页码，从 1 开始，默认 1
  Future<List<PoiItem>> searchPoiByText({
    required String keywords,
    String? city,
    int offset = 20,
    int pageIndex = 1,
  }) async {
    try {
      final queryParams = {
        'key': _currentApiKey,
        'keywords': keywords,
        'offset': offset,
        'page': pageIndex,
        'output': 'json',
      };

      if (city != null && city.isNotEmpty) {
        queryParams['city'] = city;
      }

      final response = await _dio.get(
        'https://restapi.amap.com/v3/place/text',
        queryParameters: queryParams,
      );

      if (response.statusCode == 200) {
        final data = response.data;
        if (data['status'] == '1' && data['pois'] != null) {
          final pois = List<Map<String, dynamic>>.from(data['pois'] as List);
          return pois.map((poi) => PoiItem.fromJson(poi)).toList();
        }
        return [];
      }
      return [];
    } catch (e) {
      print('文本 POI 搜索异常: $e');
      return [];
    }
  }

  /// 输入提示（地址自动补全）
  /// [input] 用户输入的文本
  /// [city] 城市编码或城市名，不指定则全国搜索
  /// [type] POI 类型，不指定则返回所有类型
  Future<List<InputTip>> getInputTips({
    required String input,
    String? city,
    String? type,
  }) async {
    if (input.isEmpty) return [];

    try {
      final queryParams = {
        'key': _currentApiKey,
        'keywords': input,
        'output': 'json',
      };

      if (city != null && city.isNotEmpty) {
        queryParams['city'] = city;
      }
      if (type != null && type.isNotEmpty) {
        queryParams['type'] = type;
      }

      final response = await _dio.get(
        'https://restapi.amap.com/v3/assistant/inputtips',
        queryParameters: queryParams,
      );

      if (response.statusCode == 200) {
        final data = response.data;
        if (data['status'] == '1' && data['tips'] != null) {
          final tips = List<Map<String, dynamic>>.from(data['tips'] as List);
          return tips.map((tip) => InputTip.fromJson(tip)).toList();
        }
        return [];
      }
      return [];
    } catch (e) {
      print('输入提示异常: $e');
      return [];
    }
  }

  /// 反向地理编码（已在 LocationService 中实现，这里备用）
  /// [latitude] 纬度
  /// [longitude] 经度
  Future<String?> getAddressFromCoordinates(
    double latitude,
    double longitude,
  ) async {
    try {
      final response = await _dio.get(
        'https://restapi.amap.com/v3/geocode/regeo',
        queryParameters: {
          'key': _currentApiKey,
          'location': '$longitude,$latitude',
          'output': 'json',
          'extensions': 'base',
        },
      );

      if (response.statusCode == 200 && response.data['status'] == '1') {
        final regeocode = response.data['regeocode'];
        if (regeocode != null && regeocode['formatted_address'] != null) {
          return regeocode['formatted_address'] as String;
        }
      }
      return null;
    } catch (e) {
      print('反向地理编码异常: $e');
      return null;
    }
  }

  /// 正向地理编码（地址 -> 坐标）
  /// [address] 地址信息
  /// [city] 城市名
  Future<LatLng?> getCoordinatesFromAddress({
    required String address,
    String? city,
  }) async {
    try {
      final queryParams = {
        'key': _currentApiKey,
        'address': address,
        'output': 'json',
      };

      if (city != null && city.isNotEmpty) {
        queryParams['city'] = city;
      }

      final response = await _dio.get(
        'https://restapi.amap.com/v3/geocode/geo',
        queryParameters: queryParams,
      );

      if (response.statusCode == 200 && response.data['status'] == '1') {
        final geocodes = response.data['geocodes'] as List?;
        if (geocodes != null && geocodes.isNotEmpty) {
          final location = (geocodes[0]['location'] as String? ?? '').split(
            ',',
          );
          if (location.length == 2) {
            return LatLng(
              double.tryParse(location[1]) ?? 0.0,
              double.tryParse(location[0]) ?? 0.0,
            );
          }
        }
      }
      return null;
    } catch (e) {
      print('正向地理编码异常: $e');
      return null;
    }
  }

  void dispose() {}
}
