import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:meetspace_frontend/services/api_service.dart';

class ActivityDetailPage extends StatefulWidget {
  final Map<String, dynamic> activity;

  const ActivityDetailPage({super.key, required this.activity});

  @override
  State<ActivityDetailPage> createState() => _ActivityDetailPageState();
}

class _ActivityDetailPageState extends State<ActivityDetailPage> {
  bool _signing = false;

  Future<void> _signup() async {
    setState(() => _signing = true);
    try {
      final res =
          await ApiService.signupActivity(widget.activity['id'] as int);
      final msg = res['msg'] ?? '';
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(
          content: Text(msg.isNotEmpty ? msg : '操作完成'),
          behavior: SnackBarBehavior.floating,
          backgroundColor:
              res['code'] == 'SUCCESS' ? Colors.green : Colors.redAccent,
          shape:
              RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
        ));
        if (res['code'] == 'SUCCESS') Navigator.pop(context);
      }
    } catch (_) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(const SnackBar(
          content: Text('网络错误'),
          backgroundColor: Colors.redAccent,
        ));
      }
    } finally {
      if (mounted) setState(() => _signing = false);
    }
  }

  Color _statusColor(String? s) {
    switch (s) {
      case 'READY':
        return Colors.green;
      case 'CLOSED':
        return Colors.orange;
      case 'OVER':
        return Colors.grey;
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

  String _formatDateTime(dynamic t) {
    if (t == null) return '-';
    try {
      final dt = DateTime.parse(t.toString());
      return '${dt.year}-${dt.month.toString().padLeft(2, '0')}-${dt.day.toString().padLeft(2, '0')}  ${dt.hour.toString().padLeft(2, '0')}:${dt.minute.toString().padLeft(2, '0')}';
    } catch (_) {
      return t.toString();
    }
  }

  @override
  Widget build(BuildContext context) {
    final a = widget.activity;
    final status = a['status'] as String?;
    final canSignup = status == 'READY';

    return Scaffold(
      backgroundColor: const Color(0xFFF8F7FF),
      body: CustomScrollView(
        slivers: [
          SliverAppBar(
            expandedHeight: 200,
            pinned: true,
            backgroundColor: const Color(0xFF6C63FF),
            leading: IconButton(
              icon: const Icon(Icons.arrow_back_ios_new_rounded,
                  color: Colors.white),
              onPressed: () => Navigator.pop(context),
            ),
            flexibleSpace: FlexibleSpaceBar(
              background: Container(
                decoration: const BoxDecoration(
                  gradient: LinearGradient(
                    colors: [Color(0xFF6C63FF), Color(0xFF9C88FF)],
                    begin: Alignment.topLeft,
                    end: Alignment.bottomRight,
                  ),
                ),
                child: Center(
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      const Icon(Icons.event_rounded,
                          size: 56, color: Colors.white),
                      const SizedBox(height: 8),
                      Container(
                        padding: const EdgeInsets.symmetric(
                            horizontal: 14, vertical: 5),
                        decoration: BoxDecoration(
                          color: Colors.white.withOpacity(0.2),
                          borderRadius: BorderRadius.circular(20),
                        ),
                        child: Text(
                          _statusLabel(status),
                          style: const TextStyle(
                              color: Colors.white,
                              fontSize: 13,
                              fontWeight: FontWeight.w600),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ),
          SliverToBoxAdapter(
            child: Padding(
              padding: const EdgeInsets.all(20),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // Title
                  Text(
                    a['title'] ?? '',
                    style: GoogleFonts.inter(
                      fontSize: 24,
                      fontWeight: FontWeight.bold,
                      color: Colors.black87,
                    ),
                  ),
                  const SizedBox(height: 20),

                  // Info cards
                  _infoCard([
                    _infoRow(Icons.location_on_rounded, '活动地点',
                        a['address'] ?? '-',
                        color: Colors.redAccent),
                    const Divider(height: 20),
                    _infoRow(Icons.play_circle_outline_rounded, '开始时间',
                        _formatDateTime(a['startTime'])),
                    const SizedBox(height: 12),
                    _infoRow(Icons.stop_circle_outlined, '结束时间',
                        _formatDateTime(a['endTime'])),
                    const SizedBox(height: 12),
                    _infoRow(Icons.alarm_rounded, '报名截止',
                        _formatDateTime(a['signupDeadline']),
                        color: Colors.orange),
                  ]),
                  const SizedBox(height: 16),

                  // Description
                  if (a['description'] != null &&
                      (a['description'] as String).isNotEmpty) ...[
                    Text(
                      '活动简介',
                      style: GoogleFonts.inter(
                          fontSize: 16,
                          fontWeight: FontWeight.w600,
                          color: Colors.black87),
                    ),
                    const SizedBox(height: 10),
                    Container(
                      width: double.infinity,
                      padding: const EdgeInsets.all(16),
                      decoration: BoxDecoration(
                        color: Colors.white,
                        borderRadius: BorderRadius.circular(14),
                        boxShadow: [
                          BoxShadow(
                              color: Colors.black.withOpacity(0.04),
                              blurRadius: 8)
                        ],
                      ),
                      child: Text(
                        a['description'],
                        style: GoogleFonts.inter(
                            fontSize: 14,
                            height: 1.7,
                            color: Colors.grey.shade700),
                      ),
                    ),
                    const SizedBox(height: 80),
                  ] else
                    const SizedBox(height: 80),
                ],
              ),
            ),
          ),
        ],
      ),
      bottomNavigationBar: canSignup
          ? SafeArea(
              child: Padding(
                padding: const EdgeInsets.fromLTRB(20, 8, 20, 12),
                child: ElevatedButton.icon(
                  onPressed: _signing ? null : _signup,
                  icon: _signing
                      ? const SizedBox(
                          width: 18,
                          height: 18,
                          child: CircularProgressIndicator(
                              color: Colors.white, strokeWidth: 2))
                      : const Icon(Icons.how_to_reg_rounded),
                  label: Text(_signing ? '报名中...' : '立即报名'),
                  style: ElevatedButton.styleFrom(
                    minimumSize: const Size(double.infinity, 52),
                  ),
                ),
              ),
            )
          : SafeArea(
              child: Padding(
                padding: const EdgeInsets.fromLTRB(20, 8, 20, 12),
                child: ElevatedButton(
                  onPressed: null,
                  style: ElevatedButton.styleFrom(
                    minimumSize: const Size(double.infinity, 52),
                    backgroundColor: Colors.grey.shade200,
                    foregroundColor: Colors.grey,
                  ),
                  child: Text(_statusLabel(status) == '报名中'
                      ? '立即报名'
                      : '活动${_statusLabel(status)}'),
                ),
              ),
            ),
    );
  }

  Widget _infoCard(List<Widget> children) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(14),
        boxShadow: [
          BoxShadow(
              color: Colors.black.withOpacity(0.04), blurRadius: 8)
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: children,
      ),
    );
  }

  Widget _infoRow(IconData icon, String label, String value,
      {Color color = const Color(0xFF6C63FF)}) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Icon(icon, size: 18, color: color),
        const SizedBox(width: 10),
        Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(label,
                style:
                    TextStyle(fontSize: 11, color: Colors.grey.shade400)),
            const SizedBox(height: 2),
            Text(value,
                style: GoogleFonts.inter(
                    fontSize: 14,
                    fontWeight: FontWeight.w500,
                    color: Colors.black87)),
          ],
        ),
      ],
    );
  }
}
