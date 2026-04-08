import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../component/activity_card.dart';
import '../l10n/app_localizations.dart';
import '../model/activity.dart';
import '../service/activity_service.dart';
import 'activity_detail_page.dart';

class ExplorePage extends StatefulWidget {
  const ExplorePage({super.key});

  @override
  State<ExplorePage> createState() => _ExplorePageState();
}

class _ExplorePageState extends State<ExplorePage> {
  final ActivityService _activityService = ActivityService();
  final TextEditingController _searchController = TextEditingController();
  List<Activity> _activities = [];
  bool _isLoading = true;
  Set<int> _participatedIds = {};

  @override
  void initState() {
    super.initState();
    _loadActivities();
    _loadParticipated();
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  Future<void> _loadActivities() async {
    setState(() => _isLoading = true);
    try {
      final result = await _activityService.getAllActivities();
      print('Load activities result: ${result}');
      if (mounted && result.isSuccess) {
        print('Activities loaded: ${result.data?.length ?? 0}');
        setState(() => _activities = result.data ?? []);
      } else if (mounted) {
        print('Failed to load activities: ${result.msg}');
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('加载活动失败：${result.msg ?? '未知错误'}')),
        );
      }
    } catch (e) {
      print('Error loading activities: $e');
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('错误：$e'),
            backgroundColor: Colors.red,
            duration: const Duration(seconds: 5),
          ),
        );
      }
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  Future<void> _loadParticipated() async {
    try {
      final result = await _activityService.getParticipatedActivities();
      print('Load participated result: ${result}');
      if (mounted && result.isSuccess) {
        print('Participated activities loaded: ${result.data?.length ?? 0}');
        setState(() {
          _participatedIds = (result.data ?? [])
              .map((a) => a.id.toInt())
              .toSet();
        });
      } else if (mounted) {
        print('Failed to load participated activities: ${result.msg}');
      }
    } catch (e) {
      print('Error loading participated activities: $e');
      // ignore - 不参与不影响主要功能
    }
  }

  void _navigateToDetail(Activity activity) {
    Navigator.of(context).push(
      MaterialPageRoute(
        builder: (_) => ActivityDetailPage(
          activity: activity,
          isParticipated: _participatedIds.contains(activity.id.toInt()),
          onRefresh: () {
            _loadActivities();
            _loadParticipated();
          },
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final l10n = context.l10n;
    return Scaffold(
      backgroundColor: const Color(0xFFFAFCFA),
      appBar: AppBar(
        backgroundColor: Colors.white,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back_ios_new, size: 18),
          onPressed: () {},
        ),
        title: Text(
          l10n.explore,
          style: GoogleFonts.inter(
            fontSize: 17,
            fontWeight: FontWeight.w600,
            color: Colors.grey[800],
          ),
        ),
      ),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            child: TextField(
              controller: _searchController,
              decoration: InputDecoration(
                hintText: l10n.searchActivities,
                hintStyle: GoogleFonts.inter(color: Colors.grey[400]),
                prefixIcon: Icon(Icons.search, color: Colors.grey[400]),
                filled: true,
                fillColor: Colors.grey[100],
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide.none,
                ),
                contentPadding: const EdgeInsets.symmetric(vertical: 12),
              ),
            ),
          ),
          Expanded(
            child: RefreshIndicator(
              onRefresh: () async {
                await _loadActivities();
                await _loadParticipated();
              },
              child: _isLoading
                  ? const Center(child: CircularProgressIndicator())
                  : _activities.isEmpty
                  ? Center(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Icon(
                            Icons.event_busy,
                            size: 64,
                            color: Colors.grey[400],
                          ),
                          const SizedBox(height: 16),
                          Text(
                            'No activities yet',
                            style: GoogleFonts.inter(
                              fontSize: 18,
                              color: Colors.grey[500],
                            ),
                          ),
                          const SizedBox(height: 8),
                          Text(
                            '当前活动列表为空',
                            style: GoogleFonts.inter(
                              fontSize: 14,
                              color: Colors.grey[400],
                            ),
                          ),
                        ],
                      ),
                    )
                  : ListView.builder(
                      padding: const EdgeInsets.only(top: 8, bottom: 80),
                      itemCount: _activities.length,
                      itemBuilder: (context, index) {
                        final activity = _activities[index];
                        return ActivityCard(
                          activity: activity,
                          isParticipated: _participatedIds.contains(
                            activity.id.toInt(),
                          ),
                          enabled: !activity.isOverOrDeleted,
                          onTap: () => _navigateToDetail(activity),
                        );
                      },
                    ),
            ),
          ),
        ],
      ),
    );
  }
}
