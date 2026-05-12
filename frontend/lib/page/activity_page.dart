import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../component/activity_card.dart';
import '../l10n/app_localizations.dart';
import '../model/activity.dart';
import '../service/activity_service.dart';
import 'activity_detail_page.dart';
import 'create_activity_page.dart';

class ActivityPage extends StatefulWidget {
  const ActivityPage({super.key});

  @override
  State<ActivityPage> createState() => _ActivityPageState();
}

enum ActivityFilter { created, participated, all }

class _ActivityPageState extends State<ActivityPage> {
  final ActivityService _activityService = ActivityService();
  ActivityFilter _currentFilter = ActivityFilter.all;
  List<Activity> _createdActivities = [];
  List<Activity> _participatedActivities = [];
  List<Activity> _allActivities = [];
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    setState(() => _isLoading = true);
    try {
      final results = await Future.wait([
        _activityService.getCreatedActivities(),
        _activityService.getSignedUpActivities(),
        _activityService.getRelatedActivities(),
      ]);
      if (mounted) {
        setState(() {
          _createdActivities = results[0].data ?? [];
          _participatedActivities = results[1].data ?? [];
          _allActivities = results[2].data ?? [];
          _isLoading = false;
        });
      }
    } catch (e) {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  List<Activity> get _filteredActivities {
    switch (_currentFilter) {
      case ActivityFilter.created:
        return _createdActivities;
      case ActivityFilter.participated:
        return _participatedActivities;
      case ActivityFilter.all:
        return _allActivities;
    }
  }

  String _filterLabel(ActivityFilter filter) {
    switch (filter) {
      case ActivityFilter.created:
        return context.l10n.created;
      case ActivityFilter.participated:
        return context.l10n.signedUpTab;
      case ActivityFilter.all:
        return context.l10n.allActivities;
    }
  }

  Color _filterColor(ActivityFilter filter) {
    if (_currentFilter == filter) {
      return const Color(0xFF4CAF50);
    }
    return Colors.grey[300]!;
  }

  void _navigateToDetail(Activity activity, {bool isParticipated = false}) {
    Navigator.of(context).push(
      MaterialPageRoute(
        builder: (_) => ActivityDetailPage(
          activity: activity,
          isParticipated:
              isParticipated ||
              _participatedActivities.any((p) => p.id == activity.id),
          onRefresh: _loadData,
        ),
      ),
    );
  }

  Future<void> _handleSignup(Activity activity) async {
    try {
      await _activityService.signupActivity(activity.id.toInt());
      if (mounted) {
        _loadData();
        ScaffoldMessenger.of(
          context,
        ).showSnackBar(const SnackBar(content: Text('报名成功！')));
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(
          context,
        ).showSnackBar(SnackBar(content: Text('报名失败：$e')));
      }
    }
  }

  Future<void> _handleQuit(Activity activity) async {
    try {
      await _activityService.quitActivity(activity.id.toInt());
      if (mounted) {
        _loadData();
        ScaffoldMessenger.of(
          context,
        ).showSnackBar(const SnackBar(content: Text('已退出活动！')));
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(
          context,
        ).showSnackBar(SnackBar(content: Text('退出失败：$e')));
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final l10n = context.l10n;
    return Scaffold(
      backgroundColor: Theme.of(context).scaffoldBackgroundColor,
      appBar: AppBar(
        automaticallyImplyLeading: false,
        elevation: 0,
        title: Text(
          l10n.activities,
          style: GoogleFonts.inter(fontSize: 17, fontWeight: FontWeight.w600),
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.add_circle_outline),
            onPressed: () async {
              final result = await Navigator.of(context).push(
                MaterialPageRoute(builder: (_) => const CreateActivityPage()),
              );
              if (result == true) {
                _loadData();
              }
            },
          ),
        ],
      ),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            child: Row(
              children: [
                _buildFilterChip(ActivityFilter.created),
                const SizedBox(width: 8),
                _buildFilterChip(ActivityFilter.participated),
                const SizedBox(width: 8),
                _buildFilterChip(ActivityFilter.all),
              ],
            ),
          ),
          const SizedBox(height: 8),
          Expanded(
            child: RefreshIndicator(
              onRefresh: _loadData,
              child: _isLoading
                  ? const Center(child: CircularProgressIndicator())
                  : _filteredActivities.isEmpty
                  ? Center(
                      child: Text(
                        'No activities',
                        style: GoogleFonts.inter(color: Colors.grey[500]),
                      ),
                    )
                  : ListView.builder(
                      padding: const EdgeInsets.only(bottom: 80),
                      itemCount: _filteredActivities.length,
                      itemBuilder: (context, index) {
                        final activity = _filteredActivities[index];
                        final isPart = _participatedActivities.any(
                          (p) => p.id == activity.id,
                        );
                        return ActivityCard(
                          activity: activity,
                          isParticipated: isPart,
                          enabled: !activity.isOverOrDeleted,
                          onTap: () => _navigateToDetail(
                            activity,
                            isParticipated: isPart,
                          ),
                        );
                      },
                    ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildFilterChip(ActivityFilter filter) {
    return GestureDetector(
      onTap: () => setState(() => _currentFilter = filter),
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 7),
        decoration: BoxDecoration(
          color: _filterColor(filter),
          borderRadius: BorderRadius.circular(20),
        ),
        child: Text(
          _filterLabel(filter),
          style: GoogleFonts.inter(
            fontSize: 13,
            fontWeight: FontWeight.w500,
            color: _currentFilter == filter ? Colors.white : Colors.grey[700],
          ),
        ),
      ),
    );
  }
}
