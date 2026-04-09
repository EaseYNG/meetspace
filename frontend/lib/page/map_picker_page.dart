import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:flutter_map/flutter_map.dart';
import 'package:latlong2/latlong.dart';
import '../l10n/app_localizations.dart';

import '../service/location_service.dart';

class MapPickerPage extends StatefulWidget {
  final double? initialLat;
  final double? initialLng;

  const MapPickerPage({super.key, this.initialLat, this.initialLng});

  @override
  State<MapPickerPage> createState() => _MapPickerPageState();
}

class _MapPickerPageState extends State<MapPickerPage> {
  final MapController _mapController = MapController();
  final LocationService _locationService = LocationService();
  LatLng? _selectedLocation;
  String _currentAddress = '正在获取地址...';
  bool _isLocating = false;

  @override
  void initState() {
    super.initState();
    if (widget.initialLat != null && widget.initialLng != null) {
      _selectedLocation = LatLng(widget.initialLat!, widget.initialLng!);
      _fetchAddress(_selectedLocation!);
    } else {
      // Default to Beijing if no initial location, then try to locate
      _selectedLocation = const LatLng(39.9042, 116.4074);
      _autoLocate();
    }
  }

  Future<void> _autoLocate() async {
    setState(() {
      _isLocating = true;
      _currentAddress = '正在定位当前位置...';
    });

    final locationResult = await _locationService
        .getCurrentLocationWithAddress();

    if (mounted) {
      setState(() {
        _isLocating = false;
        if (locationResult != null) {
          _selectedLocation = LatLng(
            locationResult.latitude,
            locationResult.longitude,
          );
          _currentAddress = locationResult.address ?? '无法解析当前地址';
          _mapController.move(_selectedLocation!, 15.0);
        } else {
          _currentAddress = '定位失败，请手动选择位置';
        }
      });
    }
  }

  Future<void> _fetchAddress(LatLng coords) async {
    setState(() => _currentAddress = '正在解析地址...');
    final address = await _locationService.getAddressFromCoordinates(
      coords.latitude,
      coords.longitude,
    );
    if (mounted) {
      setState(() {
        _currentAddress = address ?? '无法获取该位置的详细地址';
      });
    }
  }

  void _onMapClick(TapPosition tapPosition, LatLng coordinates) {
    setState(() {
      _selectedLocation = coordinates;
    });
    _fetchAddress(coordinates);
  }

  @override
  Widget build(BuildContext context) {
    final l10n = context.l10n;
    final isDark = Theme.of(context).brightness == Brightness.dark;

    // Amap tile URL template
    // Using style=8 for dark mode and style=7 for normal map
    final tileUrl = isDark
        ? 'http://wprd0{s}.is.autonavi.com/appmaptile?lang=zh_cn&size=1&style=8&x={x}&y={y}&z={z}'
        : 'http://wprd0{s}.is.autonavi.com/appmaptile?lang=zh_cn&size=1&style=7&x={x}&y={y}&z={z}';

    return Scaffold(
      appBar: AppBar(
        title: Text(
          l10n.address,
          style: GoogleFonts.inter(fontSize: 17, fontWeight: FontWeight.w600),
        ),
        actions: [
          TextButton(
            onPressed: () {
              if (_selectedLocation != null) {
                Navigator.of(context).pop({
                  'lat': _selectedLocation!.latitude,
                  'lng': _selectedLocation!.longitude,
                  'address': _currentAddress,
                });
              }
            },
            child: Text(
              '确认',
              style: GoogleFonts.inter(
                color: const Color(0xFF4CAF50),
                fontWeight: FontWeight.w600,
              ),
            ),
          ),
        ],
      ),
      body: Stack(
        children: [
          FlutterMap(
            mapController: _mapController,
            options: MapOptions(
              initialCenter: _selectedLocation!,
              initialZoom: 12.0,
              maxZoom: 18.0,
              minZoom: 3.0,
              onTap: _onMapClick,
              interactionOptions: const InteractionOptions(
                flags: InteractiveFlag.all & ~InteractiveFlag.rotate,
                enableMultiFingerGestureRace: true,
              ),
            ),
            children: [
              TileLayer(
                urlTemplate: tileUrl,
                subdomains: const [
                  '1',
                  '2',
                  '3',
                  '4',
                ], // High-concurrency support
                userAgentPackageName: 'com.example.meetspace',
                maxZoom: 18.0,
                retinaMode: false,
                tileProvider: NetworkTileProvider(),
              ),
              MarkerLayer(
                markers: [
                  if (_selectedLocation != null)
                    Marker(
                      point: _selectedLocation!,
                      width: 40,
                      height: 40,
                      child: const Icon(
                        Icons.location_on,
                        color: Colors.red,
                        size: 40,
                      ),
                      alignment: Alignment.topCenter,
                    ),
                ],
              ),
            ],
          ),
          Positioned(
            bottom: 24,
            left: 16,
            right: 16,
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
              decoration: BoxDecoration(
                color: Theme.of(context).cardColor,
                borderRadius: BorderRadius.circular(12),
                boxShadow: [
                  BoxShadow(
                    color: Colors.black.withValues(alpha: 0.1),
                    blurRadius: 10,
                    offset: const Offset(0, 4),
                  ),
                ],
              ),
              child: Row(
                children: [
                  Icon(Icons.location_on, color: Colors.blue[400], size: 24),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          _currentAddress,
                          style: GoogleFonts.inter(
                            fontSize: 15,
                            fontWeight: FontWeight.w600,
                            color: Theme.of(context).textTheme.bodyLarge?.color,
                          ),
                          maxLines: 2,
                          overflow: TextOverflow.ellipsis,
                        ),
                        const SizedBox(height: 4),
                        Text(
                          _selectedLocation != null
                              ? '经度: ${_selectedLocation!.longitude.toStringAsFixed(4)}   纬度: ${_selectedLocation!.latitude.toStringAsFixed(4)}'
                              : '请在地图上点击选择位置',
                          style: GoogleFonts.inter(
                            fontSize: 12,
                            color: Theme.of(context).textTheme.bodySmall?.color,
                          ),
                        ),
                      ],
                    ),
                  ),
                  if (_isLocating)
                    const SizedBox(
                      width: 24,
                      height: 24,
                      child: CircularProgressIndicator(strokeWidth: 2),
                    )
                  else
                    IconButton(
                      icon: const Icon(Icons.my_location),
                      color: Colors.blue[400],
                      onPressed: _autoLocate,
                    ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}
