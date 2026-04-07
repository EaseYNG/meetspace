import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:meetspace_frontend/services/api_service.dart';
import 'package:meetspace_frontend/services/token_service.dart';
import 'package:meetspace_frontend/pages/activity_detail_page.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  List<dynamic> _activities = [];
  Map<String, dynamic>? _profile;
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    setState(() => _loading = true);
    try {
      final profileRes = await ApiService.getProfile();
      final activitiesRes = await ApiService.getParticipatedActivities();

      if (mounted) {
        setState(() {
          if (profileRes['code'] == 'SUCCESS') {
            _profile = profileRes['data'];
          }
          if (activitiesRes['code'] == 'SUCCESS') {
            _activities = activitiesRes['data'] ?? [];
          }
        });
      }
    } catch (_) {}
    if (mounted) setState(() => _loading = false);
  }

  String get _greeting {
    final hour = DateTime.now().hour;
    if (hour < 6) return '凌晨好';
    if (hour < 12) return '早上好';
    if (hour < 14) return '中午好';
    if (hour < 18) return '下午好';
    return '晚上好';
  }

  String get _nickname {
    if (_profile == null) return '朋友';
    final fn = _profile!['firstname'] ?? '';
    final ln = _profile!['lastname'] ?? '';
    if (fn.isEmpty && ln.isEmpty) return '朋友';
    return '$fn$ln';
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF8F7FF),
      body: RefreshIndicator(
        onRefresh: _loadData,
        color: const Color(0xFF6C63FF),
        child: CustomScrollView(
          slivers: [
            _buildAppBar(),
            SliverToBoxAdapter(child: _buildBanner()),
            SliverToBoxAdapter(child: _buildSectionTitle('我参加的活动')),
            _loading
                ? const SliverFillRemaining(
                    child: Center(
                        child: CircularProgressIndicator(
                            color: Color(0xFF6C63FF))))
                : _activities.isEmpty
                    ? SliverToBoxAdapter(child: _buildEmpty())
                    : SliverList(
                        delegate: SliverChildBuilderDelegate(
                          (ctx, i) => _ActivityCard(
                              activity: _activities[i],
                              onTap: () => _openDetail(_activities[i])),
                          childCount: _activities.length,
                        ),
                      ),
            const SliverToBoxAdapter(child: SizedBox(height: 24)),
          ],
        ),
      ),
    );
  }

  SliverAppBar _buildAppBar() {
    return SliverAppBar(
      expandedHeight: 120,
      floating: true,
      snap: true,
      backgroundColor: Colors.white,
      elevation: 0,
      flexibleSpace: FlexibleSpaceBar(
        background: Container(
          color: Colors.white,
          padding: const EdgeInsets.fromLTRB(20, 50, 20, 0),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                mainAxisSize: MainAxisSize.min,
                children: [
                  Text(
                    '$_greeting，$_nickname 👋',
                    style: GoogleFonts.inter(
                      fontSize: 22,
                      fontWeight: FontWeight.bold,
                      color: Colors.black87,
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    '来看看你的活动吧',
                    style: GoogleFonts.inter(
                        fontSize: 14, color: Colors.grey.shade500),
                  ),
                ],
              ),
              CircleAvatar(
                radius: 24,
                backgroundColor: const Color(0xFF6C63FF).withOpacity(0.1),
                child: const Icon(Icons.person_rounded,
                    color: Color(0xFF6C63FF), size: 28),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildBanner() {
    return Container(
      margin: const EdgeInsets.fromLTRB(16, 8, 16, 8),
      height: 130,
      decoration: BoxDecoration(
        gradient: const LinearGradient(
          colors: [Color(0xFF6C63FF), Color(0xFF9C88FF)],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        borderRadius: BorderRadius.circular(18),
      ),
      child: Stack(
        children: [
          Positioned(
            right: -20,
            bottom: -20,
            child: Container(
              width: 110,
              height: 110,
              decoration: BoxDecoration(
                color: Colors.white.withOpacity(0.1),
                shape: BoxShape.circle,
              ),
            ),
          ),
          Padding(
            padding: const EdgeInsets.all(20),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text('探索新活动',
                    style: GoogleFonts.inter(
                        fontSize: 20,
                        fontWeight: FontWeight.bold,
                        color: Colors.white)),
                const SizedBox(height: 6),
                Text('发现志同道合的伙伴',
                    style: GoogleFonts.inter(
                        fontSize: 13, color: Colors.white.withOpacity(0.85))),
                const SizedBox(height: 14),
                InkWell(
                  onTap: () {},
                  child: Container(
                    padding: const EdgeInsets.symmetric(
                        horizontal: 14, vertical: 6),
                    decoration: BoxDecoration(
                      color: Colors.white,
                      borderRadius: BorderRadius.circular(20),
                    ),
                    child: Text('去广场逛逛',
                        style: GoogleFonts.inter(
                            fontSize: 12,
                            fontWeight: FontWeight.w600,
                            color: const Color(0xFF6C63FF))),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildSectionTitle(String title) {
    return Padding(
      padding: const EdgeInsets.fromLTRB(20, 12, 20, 8),
      child: Text(
        title,
        style: GoogleFonts.inter(
            fontSize: 17,
            fontWeight: FontWeight.w600,
            color: Colors.black87),
      ),
    );
  }

  Widget _buildEmpty() {
    return SizedBox(
      height: 200,
      child: Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(Icons.event_busy_rounded,
                size: 54, color: Colors.grey.shade300),
            const SizedBox(height: 12),
            Text('还没有参加任何活动',
                style: GoogleFonts.inter(
                    color: Colors.grey.shade400, fontSize: 14)),
          ],
        ),
      ),
    );
  }

  void _openDetail(Map<String, dynamic> activity) {
    Navigator.push(
      context,
      MaterialPageRoute(
          builder: (_) => ActivityDetailPage(activity: activity)),
    );
  }
}

class _ActivityCard extends StatelessWidget {
  final Map<String, dynamic> activity;
  final VoidCallback onTap;

  const _ActivityCard({required this.activity, required this.onTap});

  Color _statusColor(String? status) {
    switch (status) {
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

  String _statusLabel(String? status) {
    switch (status) {
      case 'READY':
        return '报名中';
      case 'CLOSED':
        return '已截止';
      case 'OVER':
        return '已结束';
      case 'DELETED':
        return '已删除';
      default:
        return status ?? '';
    }
  }

  @override
  Widget build(BuildContext context) {
    final status = activity['status'] as String?;
    return GestureDetector(
      onTap: onTap,
      child: Container(
        margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 6),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(16),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.05),
              blurRadius: 10,
              offset: const Offset(0, 2),
            ),
          ],
        ),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Row(
            children: [
              // Left color bar
              Container(
                width: 4,
                height: 70,
                decoration: BoxDecoration(
                  color: _statusColor(status),
                  borderRadius: BorderRadius.circular(4),
                ),
              ),
              const SizedBox(width: 14),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: [
                        Expanded(
                          child: Text(
                            activity['title'] ?? '',
                            style: GoogleFonts.inter(
                                fontSize: 15,
                                fontWeight: FontWeight.w600,
                                color: Colors.black87),
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis,
                          ),
                        ),
                        Container(
                          padding: const EdgeInsets.symmetric(
                              horizontal: 8, vertical: 3),
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
                    const SizedBox(height: 8),
                    _iconText(
                        Icons.location_on_outlined, activity['address'] ?? ''),
                    const SizedBox(height: 4),
                    _iconText(Icons.access_time_outlined,
                        _formatTime(activity['startTime'])),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _iconText(IconData icon, String text) {
    return Row(
      children: [
        Icon(icon, size: 14, color: Colors.grey.shade400),
        const SizedBox(width: 4),
        Expanded(
          child: Text(
            text,
            style: TextStyle(fontSize: 12, color: Colors.grey.shade500),
            maxLines: 1,
            overflow: TextOverflow.ellipsis,
          ),
        ),
      ],
    );
  }

  String _formatTime(dynamic time) {
    if (time == null) return '';
    try {
      final dt = DateTime.parse(time.toString());
      return '${dt.year}-${dt.month.toString().padLeft(2, '0')}-${dt.day.toString().padLeft(2, '0')} ${dt.hour.toString().padLeft(2, '0')}:${dt.minute.toString().padLeft(2, '0')}';
    } catch (_) {
      return time.toString();
    }
  }
}
