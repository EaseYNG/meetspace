import 'package:geolocator/geolocator.dart';
import 'package:dio/dio.dart';

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
  // TODO: Use your Amap Web Service API Key here.
  // Note: Web JS API Key (used in index.html) and Web Service API Key are different in Amap console.
  // We use Web Service API key for REST API calls like reverse geocoding.
  static const String _amapWebServiceKey = '2d6ee8e3071bf17f2d0694af2afc3b17';

  Future<Position?> getCurrentLocation() async {
    bool serviceEnabled;
    LocationPermission permission;

    serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled) {
      return null;
    }

    permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied) {
      permission = await Geolocator.requestPermission();
      if (permission == LocationPermission.denied) {
        return null;
      }
    }

    if (permission == LocationPermission.deniedForever) {
      return null;
    }

    return await Geolocator.getCurrentPosition(
      desiredAccuracy: LocationAccuracy.high,
    );
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
      final response = await _dio.get(
        'https://restapi.amap.com/v3/geocode/regeo',
        queryParameters: {
          'key': _amapWebServiceKey,
          'location': '$lng,$lat', // Amap uses longitude,latitude format
          'output': 'json',
          'extensions': 'base',
        },
      );

      if (response.statusCode == 200 && response.data['status'] == '1') {
        final regeocode = response.data['regeocode'];
        if (regeocode != null && regeocode['formatted_address'] != null) {
          final address = regeocode['formatted_address'] as String;
          return address.isNotEmpty ? address : null;
        }
      }
      return null;
    } catch (e) {
      print('Reverse geocoding error: $e');
      return null;
    }
  }
}
