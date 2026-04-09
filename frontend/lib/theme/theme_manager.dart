import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

class ThemeManager {
  static final ThemeManager _instance = ThemeManager._internal();
  factory ThemeManager() => _instance;
  ThemeManager._internal();

  final ValueNotifier<ThemeMode> themeMode = ValueNotifier(ThemeMode.light);

  Future<void> init() async {
    final prefs = await SharedPreferences.getInstance();
    final isDark = prefs.getBool('isDark') ?? false;
    themeMode.value = isDark ? ThemeMode.dark : ThemeMode.light;
  }

  Future<void> toggleTheme(bool isDark) async {
    themeMode.value = isDark ? ThemeMode.dark : ThemeMode.light;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool('isDark', isDark);
  }

  bool get isDark => themeMode.value == ThemeMode.dark;
}
