import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:url_launcher/url_launcher.dart';
import '../component/custom_button.dart';
import '../component/page_title.dart';
import '../l10n/app_localizations.dart';
import '../model/activity.dart';
import '../service/activity_service.dart';
import 'create_activity_page.dart';

class ActivityDetailPage extends StatefulWidget {
  final Activity activity;
  final bool isParticipated;
  final VoidCallback onRefresh;

  const ActivityDetailPage({
    super.key,
    required this.activity,
    this.isParticipated = false,
    required this.onRefresh,
  });

  @override
  State<ActivityDetailPage> createState() => _ActivityDetailPageState();
}

class _ActivityDetailPageState extends State<ActivityDetailPage> {
  late Activity _activity;
  late bool _isParticipated;
  final ActivityService _activityService = ActivityService();
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _activity = widget.activity;
    _isParticipated = widget.isParticipated;
  }

  Future<void> _handleSignup() async {
    setState(() => _isLoading = true);
    try {
      await _activityService.signupActivity(_activity.id.toInt());
      if (mounted) {
        setState(() => _isParticipated = true);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(context.l10n.signupSuccess)),
        );
        widget.onRefresh();
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(e.toString())),
        );
      }
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  Future<void> _handleDelete() async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: Text(context.l10n.delete),
        content: const Text('Are you sure you want to delete this activity?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx, false),
            child: const Text('Cancel'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(ctx, true),
            style: TextButton.styleFrom(foregroundColor: Colors.red),
            child: Text(context.l10n.delete),
          ),
        ],
      ),
    );

    if (confirm == true) {
      try {
        await _activityService.deleteActivity(_activity.id.toInt());
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('Activity deleted')),
          );
          widget.onRefresh();
          Navigator.of(context).pop();
        }
      } catch (e) {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text(e.toString())),
          );
        }
      }
    }
  }

  Future<void> _openNavigation() async {
    final lat = _activity.latitude;
    final lon = _activity.longitude;
    if (lat == null || lon == null) return;
    final uri = Uri.parse(
      'https://uri.amap.com/navigation?to=$lat,$lon&mode=car&coordinate=gaode&callnative=1',
    );
    if (await canLaunchUrl(uri)) {
      await launchUrl(uri, mode: LaunchMode.externalApplication);
    }
  }

  String _formatDateTime(DateTime dt) {
    return '${dt.year}-${dt.month.toString().padLeft(2, '0')}-${dt.day.toString().padLeft(2, '0')} ${dt.hour.toString().padLeft(2, '0')}:${dt.minute.toString().padLeft(2, '0')}';
  }

  List<Widget> _buildActionButtons() {
    final buttons = <Widget>[];
    final status = _activity.status;

    if (status.name == 'ready') {
      if (!_isParticipated) {
        buttons.add(CustomButton(
          text: context.l10n.signUpForActivity,
          onPressed: _handleSignup,
          isLoading: _isLoading,
        ));
      }
      buttons.add(const SizedBox(height: 10));
      buttons.add(CustomButton(
        text: context.l10n.edit,
        onPressed: () async {
          await Navigator.of(context).push(
            MaterialPageRoute(
              builder: (_) => CreateActivityPage(activity: _activity),
            ),
          );
          widget.onRefresh();
        },
        backgroundColor: Colors.blue[400],
      ));
    }

    if (_isParticipated && status.name == 'ready') {
      buttons.add(const SizedBox(height: 10));
      buttons.add(CustomButton(
        text: context.l10n.quit,
        onPressed: () async {
          // quit activity
        },
        backgroundColor: Colors.orange[400],
      ));
    }

    buttons.add(const SizedBox(height: 10));
    buttons.add(CustomButton(
      text: context.l10n.delete,
      onPressed: _handleDelete,
      backgroundColor: Colors.red[300],
    ));

    return buttons;
  }

  @override
  Widget build(BuildContext context) {
    final l10n = context.l10n;
    return Scaffold(
      backgroundColor: const Color(0xFFFAFCFA),
      appBar: AppBar(
        backgroundColor: Colors.white,
        elevation: 0,
        title: Text(
          _activity.title,
          style: GoogleFonts.inter(
            fontSize: 17,
            fontWeight: FontWeight.w600,
            color: Colors.grey[800],
          ),
        ),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.symmetric(horizontal: 16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            ClipRRect(
              borderRadius: BorderRadius.circular(12),
              child: _activity.image != null && _activity.image!.isNotEmpty
                  ? Image.network(
                      _activity.image!,
                      height: 200,
                      width: double.infinity,
                      fit: BoxFit.cover,
                      errorBuilder: (c, e, s) => _buildPlaceholderImage(),
                    )
                  : _buildPlaceholderImage(),
            ),
            const SizedBox(height: 16),
            PageTitle(title: l10n.activityDetail),
            _buildInfoRow(l10n.description, _activity.description ?? '-'),
            _buildInfoRow(l10n.address, _activity.address),
            _buildInfoRow(
                l10n.startTime, _formatDateTime(_activity.startTime)),
            _buildInfoRow(
                l10n.endTime, _formatDateTime(_activity.endTime)),
            _buildInfoRow(l10n.signupDeadline,
                _formatDateTime(_activity.signupDeadline)),
            _buildInfoRow(
                l10n.participants,
                '${_activity.minParticipants} - ${_activity.maxParticipants}'),
            if (_activity.latitude != null &&
                _activity.longitude != null) ...[
              const SizedBox(height: 8),
              GestureDetector(
                onTap: _openNavigation,
                child: Container(
                  padding: const EdgeInsets.all(14),
                  decoration: BoxDecoration(
                    color: Colors.blue[50],
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Row(
                    children: [
                      Icon(Icons.navigation, color: Colors.blue[600]),
                      const SizedBox(width: 10),
                      Text(
                        l10n.navigate,
                        style: GoogleFonts.inter(
                          fontSize: 15,
                          fontWeight: FontWeight.w500,
                          color: Colors.blue[700],
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ],
            const SizedBox(height: 24),
            ..._buildActionButtons(),
            const SizedBox(height: 32),
          ],
        ),
      ),
    );
  }

  Widget _buildPlaceholderImage() {
    return Container(
      height: 200,
      color: Colors.grey[200],
      child: Center(
        child: Icon(Icons.image_outlined, size: 48, color: Colors.grey[400]),
      ),
    );
  }

  Widget _buildInfoRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 12),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 90,
            child: Text(
              '$label:',
              style: GoogleFonts.inter(
                fontSize: 14,
                fontWeight: FontWeight.w500,
                color: Colors.grey[700],
              ),
            ),
          ),
          Expanded(
            child: Text(
              value,
              style: GoogleFonts.inter(fontSize: 14, color: Colors.grey[600]),
            ),
          ),
        ],
      ),
    );
  }
}
