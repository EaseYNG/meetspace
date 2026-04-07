import 'package:flutter/material.dart';
import 'package:meetspace_frontend/pages/home_page.dart';
import 'package:meetspace_frontend/pages/square_page.dart';
import 'package:meetspace_frontend/pages/activity_manage_page.dart';
import 'package:meetspace_frontend/pages/profile_page.dart';

class MainShell extends StatefulWidget {
  const MainShell({super.key});

  @override
  State<MainShell> createState() => _MainShellState();
}

class _MainShellState extends State<MainShell> {
  int _currentIndex = 0;

  final List<Widget> _pages = const [
    HomePage(),
    SquarePage(),
    ActivityManagePage(),
    ProfilePage(),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: IndexedStack(
        index: _currentIndex,
        children: _pages,
      ),
      bottomNavigationBar: NavigationBar(
        selectedIndex: _currentIndex,
        onDestinationSelected: (i) => setState(() => _currentIndex = i),
        indicatorColor: const Color(0xFF6C63FF).withOpacity(0.15),
        backgroundColor: Colors.white,
        elevation: 0,
        shadowColor: Colors.black12,
        destinations: const [
          NavigationDestination(
            icon: Icon(Icons.home_outlined),
            selectedIcon: Icon(Icons.home_rounded, color: Color(0xFF6C63FF)),
            label: '首页',
          ),
          NavigationDestination(
            icon: Icon(Icons.explore_outlined),
            selectedIcon:
                Icon(Icons.explore_rounded, color: Color(0xFF6C63FF)),
            label: '广场',
          ),
          NavigationDestination(
            icon: Icon(Icons.event_outlined),
            selectedIcon:
                Icon(Icons.event_rounded, color: Color(0xFF6C63FF)),
            label: '活动',
          ),
          NavigationDestination(
            icon: Icon(Icons.person_outline),
            selectedIcon:
                Icon(Icons.person_rounded, color: Color(0xFF6C63FF)),
            label: '我的',
          ),
        ],
      ),
    );
  }
}
