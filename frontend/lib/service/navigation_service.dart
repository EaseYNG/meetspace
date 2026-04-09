import 'dart:io';

import 'package:latlong2/latlong.dart';
import 'package:url_launcher/url_launcher.dart';

/// 导航服务 - 跳转高德地图应用进行导航
class NavigationService {
  /// 启动高德地图导航
  /// [destination] 目的地经纬度
  /// [destinationName] 目的地名称（可选，会显示在导航列表中）
  /// [strategy] 驾车路线策略：
  ///   - 'driving' 推荐（默认）
  ///   - 'bus' 公路公交
  ///   - 'walk' 步行
  /// [showRouteDetail] 是否显示路线详情, 0:不显示 1:显示（默认）
  static Future<bool> launchGaodeNavigation({
    required LatLng destination,
    String? destinationName,
    String strategy = 'driving',
    int showRouteDetail = 1,
  }) async {
    try {
      // 高德地图 URL Scheme
      // 注意：高德地图使用 GCJ-02 坐标系（国内标准）
      final uri = _buildGaodeNavigationUri(
        destination: destination,
        destinationName: destinationName,
        strategy: strategy,
        showRouteDetail: showRouteDetail,
      );

      if (await canLaunchUrl(uri)) {
        await launchUrl(uri);
        return true;
      } else {
        // 高德地图应用未安装或无法启动，提示用户
        return false;
      }
    } catch (e) {
      print('启动高德地图导航异常: $e');
      return false;
    }
  }

  /// 打开高德地图应用展示指定位置的地点详情
  /// [destination] 目的地经纬度
  /// [destinationName] 地点名称
  static Future<bool> launchGaodePoiDetail({
    required LatLng destination,
    String? destinationName,
  }) async {
    try {
      // 使用 amapuri://viewmap 打开地点详情
      final uri = Uri.parse(
        'amapuri://viewmap?'
        'sourceApplication=MeetSpace&'
        'coordtype=gcj02&'
        'pointx=${destination.longitude}&'
        'pointy=${destination.latitude}&'
        '${destinationName != null ? 'pointname=$destinationName' : ''}',
      );

      if (await canLaunchUrl(uri)) {
        await launchUrl(uri);
        return true;
      }
      return false;
    } catch (e) {
      print('打开高德地点详情异常: $e');
      return false;
    }
  }

  /// 在高德地图中打开搜索结果页面
  /// [keyword] 搜索关键词
  /// [city] 城市名
  static Future<bool> launchGaodeSearch({
    required String keyword,
    String? city,
  }) async {
    try {
      final uri = Uri.parse(
        'amapuri://search?'
        'sourceApplication=MeetSpace&'
        'keywords=$keyword&'
        '${city != null ? 'city=$city' : ''}',
      );

      if (await canLaunchUrl(uri)) {
        await launchUrl(uri);
        return true;
      }
      return false;
    } catch (e) {
      print('打开高德搜索异常: $e');
      return false;
    }
  }

  /// 构建高德地图导航 URI
  static Uri _buildGaodeNavigationUri({
    required LatLng destination,
    String? destinationName,
    String strategy = 'driving',
    int showRouteDetail = 1,
  }) {
    final buffer = StringBuffer('amapuri://route/plan?');
    buffer.write('sourceApplication=MeetSpace&');
    buffer.write('strategy=$strategy&');
    buffer.write('coord_type=gcj02&');
    buffer.write(
      'destination=${destination.longitude},${destination.latitude}&',
    );
    if (destinationName != null && destinationName.isNotEmpty) {
      buffer.write('destinationName=$destinationName&');
    }
    buffer.write('hideRouteDetail=$showRouteDetail');

    return Uri.parse(buffer.toString());
  }

  /// 获取应用商店下载链接（高德地图未安装时使用）
  static Uri getGaodeDownloadUrl() {
    if (Platform.isAndroid) {
      return Uri.parse('https://sj.qq.com/appdetail/com.autonavi.minimap');
    } else if (Platform.isIOS) {
      return Uri.parse('https://apps.apple.com/cn/app/id461903139');
    } else {
      // Web 端提供网页版链接
      return Uri.parse('https://lbs.amap.com');
    }
  }

  /// 打开高德地图 App 或重定向到下载页面
  static Future<bool> openGaodeOrDownload({
    required LatLng destination,
    String? destinationName,
  }) async {
    final navigationSuccess = await launchGaodeNavigation(
      destination: destination,
      destinationName: destinationName,
    );

    if (!navigationSuccess) {
      // 尝试打开下载页面
      try {
        final downloadUrl = getGaodeDownloadUrl();
        if (await canLaunchUrl(downloadUrl)) {
          await launchUrl(downloadUrl);
          return true;
        }
      } catch (e) {
        print('打开下载页面异常: $e');
      }
      return false;
    }

    return true;
  }

  /// 检查高德地图是否已安装
  static Future<bool> isGaodeMapInstalled() async {
    if (Platform.isAndroid) {
      try {
        final uri = Uri.parse('amapuri://');
        return await canLaunchUrl(uri);
      } catch (e) {
        return false;
      }
    } else if (Platform.isIOS) {
      try {
        final uri = Uri.parse('iosamap://');
        return await canLaunchUrl(uri);
      } catch (e) {
        return false;
      }
    }
    return true; // Web 端始终认为可用
  }
}
