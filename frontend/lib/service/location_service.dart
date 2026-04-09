import 'package:geolocator/geolocator.dart';
import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';

class LocationResult {
  final double latitude;
  final double longitude;
  final String? address;

  LocationResult({
    required this.latitude,
    required this.longitude,
    this.address,
  });
}

class LocationService {
  static final LocationService _instance = LocationService._internal();
  factory LocationService() => _instance;
  LocationService._internal();

  final Dio _dio = Dio();

  static const String _webApiKey = '2d6ee8e3071bf17f2d0694af2afc3b17';

  bool get _isAndroid {
    return defaultTargetPlatform == TargetPlatform.android;
  }

  bool get _isWeb {
    return kIsWeb;
  }

  String get _currentApiKey {
    // 使用 Web API Key 进行 REST API 调用（不需要签名配置）
    // REST API 基于 HTTP，所有平台都可以使用 Web Key
    return _webApiKey;
  }

  Future<void> init() async {
    print('[LocationService] Initialized');
  }

  Future<Position?> getCurrentLocation() async {
    // 使用 Geolocator 获取位置
    bool serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled && !_isWeb) {
      // Web 端不检查定位服务
      return null;
    }

    LocationPermission permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied) {
      permission = await Geolocator.requestPermission();
      if (permission == LocationPermission.denied) {
        return null;
      }
    }

    if (permission == LocationPermission.deniedForever) {
      return null;
    }

    try {
      // Web 端直接获取位置，不使用 getLastKnownPosition（不支持）
      if (_isWeb) {
        return await Geolocator.getCurrentPosition(
          locationSettings: const LocationSettings(
            accuracy: LocationAccuracy.best,
            timeLimit: Duration(seconds: 15),
          ),
        );
      }

      // 移动端：优先获取上一次缓存的位置，响应极快
      final lastPosition = await Geolocator.getLastKnownPosition();
      if (lastPosition != null) {
        final now = DateTime.now();
        final positionTime = lastPosition.timestamp;
        // 如果缓存位置是 1 分钟内的，直接使用
        if (now.difference(positionTime).inMinutes < 1) {
          print(
            'Using last known position: ${lastPosition.latitude}, ${lastPosition.longitude}',
          );
          return lastPosition;
        }
      }

      // 获取当前位置，设置 10 秒超时
      return await Geolocator.getCurrentPosition(
        locationSettings: const LocationSettings(
          accuracy: LocationAccuracy.medium, // 使用中等精度，在室内响应更快
          timeLimit: Duration(seconds: 10), // 10 秒超时
        ),
      );
    } catch (e) {
      print('Error getting current location: $e');

      // Web 端无法回退，返回 null
      if (_isWeb) {
        return null;
      }

      // 移动端若获取失败，再次尝试获取最后的已知位置（不考虑时间）
      try {
        return await Geolocator.getLastKnownPosition();
      } catch (e) {
        print('Error getting last known position: $e');
        return null;
      }
    }
  }

  Future<LocationResult?> getCurrentLocationWithAddress() async {
    final position = await getCurrentLocation();
    if (position == null) return null;

    final address = await getAddressFromCoordinates(
      position.latitude,
      position.longitude,
    );

    return LocationResult(
      latitude: position.latitude,
      longitude: position.longitude,
      address: address,
    );
  }

  Future<String?> getAddressFromCoordinates(double lat, double lng) async {
    try {
      print(
        '[LocationService] getAddressFromCoordinates: lat=$lat, lng=$lng, platform=${_isAndroid ? "Android" : "Web"}, apiKey=${_currentApiKey.substring(0, 8)}...',
      );

      final response = await _dio.get(
        'https://restapi.amap.com/v3/geocode/regeo',
        queryParameters: {
          'key': _currentApiKey,
          'location': '$lng,$lat',
          'output': 'json',
          'extensions': 'base',
        },
        options: Options(
          connectTimeout: const Duration(seconds: 10),
          receiveTimeout: const Duration(seconds: 10),
        ),
      );

      print(
        '[LocationService] API Response: status=${response.statusCode}, data=${response.data}',
      );

      if (response.statusCode == 200) {
        final status = response.data['status']?.toString() ?? '';
        print('[LocationService] API Status: $status, isAndroid=$_isAndroid');

        if (status == '1') {
          final regeocode = response.data['regeocode'];
          if (regeocode != null && regeocode['formatted_address'] != null) {
            final address = regeocode['formatted_address'] as String;
            print('[LocationService] Address found: $address');
            return address;
          }
        } else if (status == '0') {
          print(
            '[LocationService] API returned status 0 (no valid data). Info: ${response.data['info']}',
          );
        } else {
          print(
            '[LocationService] Unexpected API status. Full response: ${response.data}',
          );
        }
      }
      return null;
    } catch (e) {
      print('[LocationService] Reverse geocoding error: $e');
      if (e is DioException) {
        print(
          '[LocationService] DioException details: type=${e.type}, message=${e.message}, response=${e.response?.data}',
        );
      }
      return null;
    }
  }

  void dispose() {
    // Cleanup
    print('[LocationService] Disposed');
  }
}
