import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:latlong2/latlong.dart';

import '../l10n/app_localizations.dart';
import '../service/location_search_service.dart';

/// 位置搜索输入框组件
/// 支持输入提示、POI 搜索和地点选择
class SearchLocationField extends StatefulWidget {
  /// 当用户选择一个位置时的回调
  final Function(PoiItem) onLocationSelected;

  /// 搜索中心点（用于周边搜索）
  final LatLng? centerLatLng;

  /// 搜索城市（可选）
  final String? city;

  /// 搜索半径（默认 1000 米）
  final int searchRadius;

  /// 最大显示结果数（默认 20）
  final int maxResults;

  const SearchLocationField({
    required this.onLocationSelected,
    this.centerLatLng,
    this.city,
    this.searchRadius = 1000,
    this.maxResults = 20,
    super.key,
  });

  @override
  State<SearchLocationField> createState() => _SearchLocationFieldState();
}

class _SearchLocationFieldState extends State<SearchLocationField> {
  late TextEditingController _controller;
  final _searchService = LocationSearchService();
  final _focusNode = FocusNode();

  List<dynamic> _searchResults = []; // PoiItem 或 InputTip
  bool _isSearching = false;
  bool _showResults = false;
  String _lastSearchQuery = '';

  @override
  void initState() {
    super.initState();
    _controller = TextEditingController();
    _focusNode.addListener(_onFocusChange);
  }

  void _onFocusChange() {
    setState(() {
      if (!_focusNode.hasFocus && _searchResults.isEmpty) {
        _showResults = false;
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // 搜索输入框
        Container(
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(12),
            boxShadow: [
              BoxShadow(
                color: Colors.black.withOpacity(0.08),
                blurRadius: 8,
                offset: const Offset(0, 2),
              ),
            ],
          ),
          child: TextField(
            controller: _controller,
            focusNode: _focusNode,
            style: GoogleFonts.inter(fontSize: 15),
            decoration: InputDecoration(
              hintText: context.l10n.searchLocation,
              hintStyle: GoogleFonts.inter(
                fontSize: 15,
                color: Colors.grey[400],
              ),
              prefixIcon: Icon(
                Icons.location_on_outlined,
                color: Colors.grey[600],
              ),
              suffixIcon: _controller.text.isNotEmpty
                  ? GestureDetector(
                      onTap: _clearSearch,
                      child: Icon(
                        Icons.close,
                        color: Colors.grey[600],
                        size: 20,
                      ),
                    )
                  : null,
              border: InputBorder.none,
              contentPadding: const EdgeInsets.symmetric(
                vertical: 12,
                horizontal: 4,
              ),
            ),
            onChanged: (value) {
              setState(() {
                _showResults = value.isNotEmpty;
              });
              if (value.isEmpty) {
                setState(() => _searchResults.clear());
              } else if (value.length >= 2) {
                // 仅在输入至少 2 个字符时开始搜索
                _performSearch(value);
              }
            },
            onSubmitted: (value) {
              if (value.isNotEmpty) {
                _performSearch(value);
              }
            },
          ),
        ),

        // 搜索结果列表
        if (_showResults)
          Padding(
            padding: const EdgeInsets.only(top: 8),
            child: Container(
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(12),
                boxShadow: [
                  BoxShadow(
                    color: Colors.black.withOpacity(0.08),
                    blurRadius: 8,
                    offset: const Offset(0, 2),
                  ),
                ],
              ),
              constraints: const BoxConstraints(maxHeight: 300),
              child: _buildResultsList(),
            ),
          ),
      ],
    );
  }

  Widget _buildResultsList() {
    if (_isSearching) {
      return Center(
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: SizedBox(
            height: 24,
            width: 24,
            child: CircularProgressIndicator(
              strokeWidth: 2,
              valueColor: AlwaysStoppedAnimation<Color>(
                Colors.green[600] ?? Colors.green,
              ),
            ),
          ),
        ),
      );
    }

    if (_searchResults.isEmpty) {
      return Center(
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Text(
            context.l10n.noResults,
            style: GoogleFonts.inter(fontSize: 14, color: Colors.grey[600]),
          ),
        ),
      );
    }

    return ListView.separated(
      shrinkWrap: true,
      physics: const ClampingScrollPhysics(),
      itemCount: _searchResults.length,
      separatorBuilder: (context, index) =>
          Divider(height: 1, color: Colors.grey[200]),
      itemBuilder: (context, index) {
        final item = _searchResults[index];
        if (item is PoiItem) {
          return _buildPoiResultTile(item);
        } else if (item is InputTip) {
          return _buildInputTipTile(item);
        }
        return const SizedBox.shrink();
      },
    );
  }

  Widget _buildPoiResultTile(PoiItem poi) {
    return ListTile(
      contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
      leading: Icon(
        Icons.location_on_rounded,
        color: Colors.green[600],
        size: 20,
      ),
      title: Text(
        poi.name,
        style: GoogleFonts.inter(fontSize: 15, fontWeight: FontWeight.w500),
        maxLines: 1,
        overflow: TextOverflow.ellipsis,
      ),
      subtitle: Text(
        poi.address,
        style: GoogleFonts.inter(fontSize: 13, color: Colors.grey[600]),
        maxLines: 1,
        overflow: TextOverflow.ellipsis,
      ),
      trailing: Icon(Icons.chevron_right, color: Colors.grey[400], size: 20),
      onTap: () => _selectPoiItem(poi),
    );
  }

  Widget _buildInputTipTile(InputTip tip) {
    return ListTile(
      contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
      leading: Icon(Icons.search, color: Colors.grey[600], size: 20),
      title: Text(
        tip.name,
        style: GoogleFonts.inter(fontSize: 15, fontWeight: FontWeight.w500),
        maxLines: 1,
        overflow: TextOverflow.ellipsis,
      ),
      subtitle: Text(
        tip.address,
        style: GoogleFonts.inter(fontSize: 13, color: Colors.grey[600]),
        maxLines: 1,
        overflow: TextOverflow.ellipsis,
      ),
      trailing: Icon(Icons.chevron_right, color: Colors.grey[400], size: 20),
      onTap: () => _selectInputTip(tip),
    );
  }

  Future<void> _performSearch(String query) async {
    if (query == _lastSearchQuery) return;
    _lastSearchQuery = query;

    setState(() => _isSearching = true);

    try {
      List<dynamic> results = [];

      // 优先尝试输入提示搜索（响应更快）
      final tips = await _searchService.getInputTips(
        input: query,
        city: widget.city,
      );

      if (tips.isNotEmpty) {
        results = tips.take(widget.maxResults).toList();
      } else if (widget.centerLatLng != null) {
        // 如果输入提示无结果，尝试周边搜索
        final pois = await _searchService.searchNearbyPoi(
          center: widget.centerLatLng!,
          keyword: query,
          radius: widget.searchRadius,
          offset: widget.maxResults,
        );
        results = pois.take(widget.maxResults).toList();
      } else {
        // 不提供中心点时，使用文本搜索
        final pois = await _searchService.searchPoiByText(
          keywords: query,
          city: widget.city,
          offset: widget.maxResults,
        );
        results = pois.take(widget.maxResults).toList();
      }

      if (mounted) {
        setState(() {
          _searchResults = results;
          _isSearching = false;
        });
      }
    } catch (e) {
      print('搜索异常: $e');
      if (mounted) {
        setState(() => _isSearching = false);
      }
    }
  }

  void _selectPoiItem(PoiItem poi) {
    _controller.text = poi.name;
    setState(() => _showResults = false);
    _focusNode.unfocus();
    widget.onLocationSelected(poi);
  }

  void _selectInputTip(InputTip tip) {
    _controller.text = tip.name;

    // 如果 InputTip 没有坐标，尝试地理编码获取
    if (tip.latLng != null) {
      final poi = PoiItem(
        id: tip.adcode ?? '',
        name: tip.name,
        address: tip.address,
        latitude: tip.latitude!,
        longitude: tip.longitude!,
      );
      _selectPoiItem(poi);
    } else {
      // 异步获取坐标
      _geocodeAndSelect(tip);
    }
  }

  Future<void> _geocodeAndSelect(InputTip tip) async {
    setState(() => _isSearching = true);

    try {
      final latLng = await _searchService.getCoordinatesFromAddress(
        address: tip.name,
        city: widget.city,
      );

      if (latLng != null && mounted) {
        final poi = PoiItem(
          id: tip.adcode ?? '',
          name: tip.name,
          address: tip.address,
          latitude: latLng.latitude,
          longitude: latLng.longitude,
        );
        _selectPoiItem(poi);
      }

      if (mounted) {
        setState(() => _isSearching = false);
      }
    } catch (e) {
      print('地理编码异常: $e');
      if (mounted) {
        setState(() => _isSearching = false);
      }
    }
  }

  void _clearSearch() {
    _controller.clear();
    setState(() {
      _searchResults.clear();
      _showResults = false;
      _lastSearchQuery = '';
    });
    _focusNode.unfocus();
  }

  @override
  void dispose() {
    _controller.dispose();
    _focusNode.dispose();
    super.dispose();
  }
}
