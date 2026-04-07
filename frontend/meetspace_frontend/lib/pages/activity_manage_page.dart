import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:meetspace_frontend/services/api_service.dart';
import 'package:meetspace_frontend/pages/create_activity_page.dart';
import 'package:meetspace_frontend/pages/activity_detail_page.dart';

class ActivityManagePage extends StatefulWidget {
  const ActivityManagePage({super.key});

  @override
  State<ActivityManagePage> createState() => _ActivityManagePageState();
}

class _ActivityManagePageState extends State<ActivityManagePage>
    with SingleTickerProviderStateMixin {
  late final TabController _tabController;
  List<dynamic> _created = [];
  List<dynamic> _participated = [];
  bool _loading = true;

  @override
  void initState() {
    _tabController = TabController(length: 2, vsync: this);
    super.initState();
    _loadData();
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  Future<void> _loadData() async {
    setState(() => _loading = true);
    try {
      final r1 = await ApiService.getMyActivities();
      final r2 = await ApiService.getParticipatedActivities();
      if (mounted) {
        setState(() {
          _created = r1['data'] ?? [];
          _participated = r2['data'] ?? [];
        });
      }
    } catch (_) {}
    if (mounted) setState(() => _loading = false);
  }

  Future<void> _deleteActivity(int id) async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (_) => AlertDialog(
        title: const Text('确认删除'),
        content: const Text('删除后活动将无法恢复，确定吗？'),
        actions: [
          TextButton(
              onPressed: () => Navigator.pop(context, false),
              child: const Text('取消')),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text('删除', style: TextStyle(color: Colors.red)),
          ),
        ],
      ),
    );
    if (confirm != true) return;
    try {
      final res = await ApiService.deleteActivity(id);
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(
          content: Text(res['msg'] ?? '已删除'),
          backgroundColor:
              res['code'] == 'SUCCESS' ? Colors.green : Colors.redAccent,
          behavior: SnackBarBehavior.floating,
          shape:
              RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
        ));
        _loadData();
      }
    } catch (_) {}
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF8F7FF),
      appBar: AppBar(
        backgroundColor: Colors.white,
        elevation: 0,
        title: Text(
          '活动管理',
          style: GoogleFonts.inter(
              fontSize: 20,
              fontWeight: FontWeight.bold,
              color: Colors.black87),
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.add_circle_outline_rounded,
                color: Color(0xFF6C63FF), size: 28),
            tooltip: '创建活动',
            onPressed: () async {
              final created = await Navigator.push(
                context,
                MaterialPageRoute(
                    builder: (_) => const CreateActivityPage()),
              );
              if (created == true) _loadData();
            },
          ),
        ],
        bottom: TabBar(
          controller: _tabController,
          labelColor: const Color(0xFF6C63FF),
          unselectedLabelColor: Colors.grey,
          indicatorColor: const Color(0xFF6C63FF),
          indicatorWeight: 3,
          tabs: const [
            Tab(text: '我创建的'),
            Tab(text: '我参加的'),
          ],
        ),
      ),
      body: _loading
          ? const Center(
              child: CircularProgressIndicator(color: Color(0xFF6C63FF)))
          : RefreshIndicator(
              onRefresh: _loadData,
              color: const Color(0xFF6C63FF),
              child: TabBarView(
                controller: _tabController,
                children: [
                  _ActivityList(
                    activities: _created,
                    showDelete: true,
                    onDelete: _deleteActivity,
                    onTap: (a) => Navigator.push(
                        context,
                        MaterialPageRoute(
                            builder: (_) =>
                                ActivityDetailPage(activity: a))),
                    emptyText: '你还没有创建活动',
                  ),
                  _ActivityList(
                    activities: _participated,
                    showDelete: false,
                    onDelete: null,
                    onTap: (a) => Navigator.push(
                        context,
                        MaterialPageRoute(
                            builder: (_) =>
                                ActivityDetailPage(activity: a))),
                    emptyText: '你还没有参加任何活动',
                  ),
                ],
              ),
            ),
    );
  }
}

class _ActivityList extends StatelessWidget {
  final List<dynamic> activities;
  final bool showDelete;
  final Function(int)? onDelete;
  final Function(Map<String, dynamic>) onTap;
  final String emptyText;

  const _ActivityList({
    required this.activities,
    required this.showDelete,
    required this.onDelete,
    required this.onTap,
    required this.emptyText,
  });

  Color _statusColor(String? s) {
    switch (s) {
      case 'READY':
        return Colors.green;
      case 'CLOSED':
        return Colors.orange;
      case 'OVER':
        return Colors.grey;
      case 'DELETED':
        return Colors.red;
      default:
        return Colors.blueGrey;
    }
  }

  String _statusLabel(String? s) {
    switch (s) {
      case 'READY':
        return '报名中';
      case 'CLOSED':
        return '已截止';
      case 'OVER':
        return '已结束';
      case 'DELETED':
        return '已删除';
      default:
        return s ?? '';
    }
  }

  String _formatDate(dynamic t) {
    if (t == null) return '-';
    try {
      final dt = DateTime.parse(t.toString());
      return '${dt.year}-${dt.month.toString().padLeft(2, '0')}-${dt.day.toString().padLeft(2, '0')}';
    } catch (_) {
      return t.toString();
    }
  }

  @override
  Widget build(BuildContext context) {
    if (activities.isEmpty) {
      return Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(Icons.event_busy_rounded,
                size: 60, color: Colors.grey.shade300),
            const SizedBox(height: 12),
            Text(emptyText,
                style: GoogleFonts.inter(
                    color: Colors.grey.shade400, fontSize: 14)),
          ],
        ),
      );
    }

    return ListView.builder(
      padding: const EdgeInsets.all(16),
      itemCount: activities.length,
      itemBuilder: (ctx, i) {
        final a = activities[i] as Map<String, dynamic>;
        final status = a['status'] as String?;
        return GestureDetector(
          onTap: () => onTap(a),
          child: Container(
            margin: const EdgeInsets.only(bottom: 12),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(16),
              boxShadow: [
                BoxShadow(
                    color: Colors.black.withOpacity(0.05),
                    blurRadius: 8,
                    offset: const Offset(0, 2)),
              ],
            ),
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Expanded(
                        child: Text(
                          a['title'] ?? '',
                          style: GoogleFonts.inter(
                              fontSize: 16,
                              fontWeight: FontWeight.w600,
                              color: Colors.black87),
                          maxLines: 1,
                          overflow: TextOverflow.ellipsis,
                        ),
                      ),
                      Container(
                        padding: const EdgeInsets.symmetric(
                            horizontal: 10, vertical: 4),
                        decoration: BoxDecoration(
                          color: _statusColor(status).withOpacity(0.1),
                          borderRadius: BorderRadius.circular(20),
                        ),
                        child: Text(
                          _statusLabel(status),
                          style: TextStyle(
                              fontSize: 11,
                              color: _statusColor(status),
                              fontWeight: FontWeight.w600),
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 10),
                  Row(
                    children: [
                      Icon(Icons.location_on_outlined,
                          size: 14, color: Colors.grey.shade400),
                      const SizedBox(width: 4),
                      Expanded(
                        child: Text(
                          a['address'] ?? '',
                          style: TextStyle(
                              fontSize: 13, color: Colors.grey.shade500),
                          maxLines: 1,
                          overflow: TextOverflow.ellipsis,
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 4),
                  Row(
                    children: [
                      Icon(Icons.calendar_month_outlined,
                          size: 14, color: Colors.grey.shade400),
                      const SizedBox(width: 4),
                      Text(
                        '${_formatDate(a['startTime'])} — ${_formatDate(a['endTime'])}',
                        style: TextStyle(
                            fontSize: 13, color: Colors.grey.shade500),
                      ),
                    ],
                  ),
                  if (showDelete) ...[
                    const SizedBox(height: 12),
                    Align(
                      alignment: Alignment.centerRight,
                      child: TextButton.icon(
                        onPressed: () =>
                            onDelete?.call(a['id'] as int),
                        icon: const Icon(Icons.delete_outline_rounded,
                            size: 16, color: Colors.red),
                        label: const Text('删除',
                            style:
                                TextStyle(color: Colors.red, fontSize: 13)),
                        style: TextButton.styleFrom(
                            padding: const EdgeInsets.symmetric(
                                horizontal: 12, vertical: 4)),
                      ),
                    ),
                  ],
                ],
              ),
            ),
          ),
        );
      },
    );
  }
}
