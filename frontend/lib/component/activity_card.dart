import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../model/activity.dart';
import '../model/activity_status.dart';

class ActivityCard extends StatefulWidget {
  final Activity activity;
  final bool isParticipated;
  final VoidCallback? onTap;
  final bool enabled;

  const ActivityCard({
    super.key,
    required this.activity,
    this.isParticipated = false,
    this.onTap,
    this.enabled = true,
  });

  @override
  State<ActivityCard> createState() => _ActivityCardState();
}

class _ActivityCardState extends State<ActivityCard> {
  bool _isHovered = false;
  bool _isPressed = false;

  String _statusText() {
    switch (widget.activity.status) {
      case ActivityStatus.READY:
        return '报名中';
      case ActivityStatus.CLOSED:
        return '已截止';
      case ActivityStatus.OVER:
        return '已结束';
      case ActivityStatus.DELETED:
        return '已删除';
    }
  }

  Color _statusColor() {
    switch (widget.activity.status) {
      case ActivityStatus.READY:
        return Colors.green[500]!;
      case ActivityStatus.CLOSED:
        return Colors.blue[500]!;
      case ActivityStatus.OVER:
        return Colors.grey[500]!;
      case ActivityStatus.DELETED:
        return Colors.red[400]!;
    }
  }

  Color _borderColor() {
    if (widget.activity.status == ActivityStatus.READY) {
      return Colors.green[400]!;
    } else if (widget.activity.status == ActivityStatus.CLOSED) {
      return Colors.blue[400]!;
    }
    return Colors.grey[400]!;
  }

  String _formatDate(DateTime dt) {
    return '${dt.month.toString().padLeft(2, '0')}-${dt.day.toString().padLeft(2, '0')} ${dt.hour.toString().padLeft(2, '0')}:${dt.minute.toString().padLeft(2, '0')}';
  }

  @override
  Widget build(BuildContext context) {
    final borderColor = _borderColor();
    final isDark = Theme.of(context).brightness == Brightness.dark;

    return MouseRegion(
      cursor: SystemMouseCursors.click,
      onEnter: (_) => setState(() => _isHovered = true),
      onExit: (_) => setState(() => _isHovered = false),
      child: GestureDetector(
        onTapDown: (_) => setState(() => _isPressed = true),
        onTapUp: (_) => setState(() => _isPressed = false),
        onTapCancel: () => setState(() => _isPressed = false),
        onTap: widget.enabled ? widget.onTap : null,
        child: AnimatedScale(
          scale: _isPressed ? 0.96 : (_isHovered ? 1.02 : 1.0),
          duration: const Duration(milliseconds: 200),
          curve: Curves.easeOutCubic,
          child: Container(
            margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 6),
            decoration: BoxDecoration(
              color: isDark ? const Color(0xFF1E1E1E) : Colors.white,
              borderRadius: BorderRadius.circular(16),
              border: Border.all(color: borderColor, width: 3.5),
              boxShadow: [
                BoxShadow(
                  color: Colors.black.withValues(alpha: isDark ? 0.3 : 0.1),
                  blurRadius: _isHovered ? 20 : 12,
                  spreadRadius: _isHovered ? 4 : 2,
                  offset: Offset(0, _isHovered ? 8 : 4),
                ),
              ],
            ),
            clipBehavior: Clip.antiAlias,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Stack(
                  children: [
                    ClipRRect(
                      borderRadius: const BorderRadius.vertical(
                        top: Radius.circular(12),
                      ),
                      child: SizedBox(
                        width: double.infinity,
                        height: 140,
                        child:
                            widget.activity.image != null &&
                                widget.activity.image!.isNotEmpty
                            ? Image.network(
                                widget.activity.image!,
                                fit: BoxFit.cover,
                                errorBuilder: (context, error, stackTrace) =>
                                    _buildPlaceholderImage(),
                              )
                            : _buildPlaceholderImage(),
                      ),
                    ),
                    Positioned(
                      top: 8,
                      right: 8,
                      child: Container(
                        padding: const EdgeInsets.symmetric(
                          horizontal: 10,
                          vertical: 4,
                        ),
                        decoration: BoxDecoration(
                          color: _statusColor(),
                          borderRadius: BorderRadius.circular(12),
                        ),
                        child: Text(
                          _statusText(),
                          style: GoogleFonts.inter(
                            fontSize: 11,
                            color: Colors.white,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                      ),
                    ),
                  ],
                ),
                Padding(
                  padding: const EdgeInsets.all(12),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        widget.activity.title,
                        style: GoogleFonts.inter(
                          fontSize: 16,
                          fontWeight: FontWeight.w700,
                          color: isDark ? Colors.white : Colors.grey[900],
                        ),
                        maxLines: 1,
                        overflow: TextOverflow.ellipsis,
                      ),
                      const SizedBox(height: 4),
                      if (widget.activity.description != null &&
                          widget.activity.description!.isNotEmpty)
                        Text(
                          widget.activity.description!,
                          style: GoogleFonts.inter(
                            fontSize: 13,
                            color: isDark ? Colors.grey[400] : Colors.grey[600],
                          ),
                          maxLines: 2,
                          overflow: TextOverflow.ellipsis,
                        ),
                      const SizedBox(height: 8),
                      Row(
                        children: [
                          Icon(
                            Icons.access_time_rounded,
                            size: 14,
                            color: Colors.grey[500],
                          ),
                          const SizedBox(width: 4),
                          Expanded(
                            child: Text(
                              '${_formatDate(widget.activity.startTime)} - ${_formatDate(widget.activity.endTime)}',
                              style: GoogleFonts.inter(
                                fontSize: 12,
                                color: isDark
                                    ? Colors.grey[400]
                                    : Colors.grey[600],
                              ),
                              overflow: TextOverflow.ellipsis,
                            ),
                          ),
                        ],
                      ),
                      const SizedBox(height: 4),
                      Row(
                        children: [
                          Icon(
                            Icons.people_outline_rounded,
                            size: 14,
                            color: Colors.grey[500],
                          ),
                          const SizedBox(width: 4),
                          Text(
                            '${widget.activity.minParticipants} - ${widget.activity.maxParticipants}',
                            style: GoogleFonts.inter(
                              fontSize: 12,
                              color: isDark
                                  ? Colors.grey[400]
                                  : Colors.grey[600],
                            ),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildPlaceholderImage() {
    return Container(
      color: Colors.grey[100],
      child: Center(
        child: Icon(Icons.image_outlined, size: 40, color: Colors.grey[300]),
      ),
    );
  }
}
