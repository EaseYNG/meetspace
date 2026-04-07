import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:meetspace_frontend/services/api_service.dart';

class CreateActivityPage extends StatefulWidget {
  final Map<String, dynamic>? existing; // 若非空则为编辑模式

  const CreateActivityPage({super.key, this.existing});

  @override
  State<CreateActivityPage> createState() => _CreateActivityPageState();
}

class _CreateActivityPageState extends State<CreateActivityPage> {
  final _formKey = GlobalKey<FormState>();
  final _titleCtrl = TextEditingController();
  final _addressCtrl = TextEditingController();
  final _descCtrl = TextEditingController();

  DateTime? _startTime;
  DateTime? _endTime;
  DateTime? _signupDeadline;
  bool _loading = false;

  bool get _isEdit => widget.existing != null;

  @override
  void initState() {
    super.initState();
    if (_isEdit) {
      final a = widget.existing!;
      _titleCtrl.text = a['title'] ?? '';
      _addressCtrl.text = a['address'] ?? '';
      _descCtrl.text = a['description'] ?? '';
      _startTime = _parseTime(a['startTime']);
      _endTime = _parseTime(a['endTime']);
      _signupDeadline = _parseTime(a['signupDeadline']);
    }
  }

  DateTime? _parseTime(dynamic t) {
    if (t == null) return null;
    try {
      return DateTime.parse(t.toString());
    } catch (_) {
      return null;
    }
  }

  @override
  void dispose() {
    _titleCtrl.dispose();
    _addressCtrl.dispose();
    _descCtrl.dispose();
    super.dispose();
  }

  Future<void> _pickDateTime(String field) async {
    final now = DateTime.now();
    final date = await showDatePicker(
      context: context,
      initialDate: now.add(const Duration(days: 1)),
      firstDate: now,
      lastDate: now.add(const Duration(days: 365)),
      builder: (ctx, child) => Theme(
        data: Theme.of(ctx).copyWith(
          colorScheme: Theme.of(ctx)
              .colorScheme
              .copyWith(primary: const Color(0xFF6C63FF)),
        ),
        child: child!,
      ),
    );
    if (date == null || !mounted) return;
    final time = await showTimePicker(
      context: context,
      initialTime: TimeOfDay.now(),
      builder: (ctx, child) => Theme(
        data: Theme.of(ctx).copyWith(
          colorScheme: Theme.of(ctx)
              .colorScheme
              .copyWith(primary: const Color(0xFF6C63FF)),
        ),
        child: child!,
      ),
    );
    if (time == null) return;
    final dt =
        DateTime(date.year, date.month, date.day, time.hour, time.minute);
    setState(() {
      if (field == 'start') _startTime = dt;
      if (field == 'end') _endTime = dt;
      if (field == 'deadline') _signupDeadline = dt;
    });
  }

  String _formatDt(DateTime? dt) {
    if (dt == null) return '点击选择';
    return '${dt.year}-${dt.month.toString().padLeft(2, '0')}-${dt.day.toString().padLeft(2, '0')}  ${dt.hour.toString().padLeft(2, '0')}:${dt.minute.toString().padLeft(2, '0')}';
  }

  Future<void> _submit() async {
    if (!_formKey.currentState!.validate()) return;
    if (_startTime == null || _endTime == null || _signupDeadline == null) {
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(
        content: Text('请填写所有时间'),
        backgroundColor: Colors.redAccent,
        behavior: SnackBarBehavior.floating,
      ));
      return;
    }
    setState(() => _loading = true);
    try {
      final data = {
        'title': _titleCtrl.text.trim(),
        'address': _addressCtrl.text.trim(),
        'description': _descCtrl.text.trim(),
        'startTime': _startTime!.toIso8601String(),
        'endTime': _endTime!.toIso8601String(),
        'signupDeadline': _signupDeadline!.toIso8601String(),
      };
      Map<String, dynamic> res;
      if (_isEdit) {
        res = await ApiService.updateActivity(
            widget.existing!['id'] as int, data);
      } else {
        res = await ApiService.createActivity(data);
      }
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(
          content: Text(res['msg'] ?? '操作完成'),
          backgroundColor:
              res['code'] == 'SUCCESS' ? Colors.green : Colors.redAccent,
          behavior: SnackBarBehavior.floating,
          shape:
              RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
        ));
        if (res['code'] == 'SUCCESS') Navigator.pop(context, true);
      }
    } catch (_) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(const SnackBar(
          content: Text('网络错误'),
          backgroundColor: Colors.redAccent,
          behavior: SnackBarBehavior.floating,
        ));
      }
    } finally {
      if (mounted) setState(() => _loading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: AppBar(
        backgroundColor: Colors.white,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back_ios_new_rounded),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text(
          _isEdit ? '编辑活动' : '创建活动',
          style: GoogleFonts.inter(
              fontSize: 18,
              fontWeight: FontWeight.w600,
              color: Colors.black87),
        ),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 8),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _label('活动标题 *'),
              const SizedBox(height: 8),
              TextFormField(
                controller: _titleCtrl,
                decoration: const InputDecoration(
                    hintText: '给活动起个好名字',
                    prefixIcon: Icon(Icons.title_rounded)),
                validator: (v) => v == null || v.isEmpty ? '请输入活动标题' : null,
              ),
              const SizedBox(height: 20),

              _label('活动地点 *'),
              const SizedBox(height: 8),
              TextFormField(
                controller: _addressCtrl,
                decoration: const InputDecoration(
                    hintText: '填写详细地址',
                    prefixIcon: Icon(Icons.location_on_outlined)),
                validator: (v) => v == null || v.isEmpty ? '请输入活动地点' : null,
              ),
              const SizedBox(height: 20),

              _label('活动简介'),
              const SizedBox(height: 8),
              TextFormField(
                controller: _descCtrl,
                maxLines: 4,
                decoration: InputDecoration(
                  hintText: '描述一下这个活动（可选）',
                  alignLabelWithHint: true,
                  border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(12)),
                  enabledBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(12),
                    borderSide: BorderSide(color: Colors.grey.shade300),
                  ),
                  focusedBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(12),
                    borderSide: const BorderSide(
                        color: Color(0xFF6C63FF), width: 2),
                  ),
                ),
              ),
              const SizedBox(height: 20),

              _label('时间设置 *'),
              const SizedBox(height: 10),
              _timePicker('开始时间', _startTime, () => _pickDateTime('start'),
                  Icons.play_circle_outline_rounded),
              const SizedBox(height: 10),
              _timePicker('结束时间', _endTime, () => _pickDateTime('end'),
                  Icons.stop_circle_outlined),
              const SizedBox(height: 10),
              _timePicker(
                  '报名截止',
                  _signupDeadline,
                  () => _pickDateTime('deadline'),
                  Icons.alarm_rounded,
                  color: Colors.orange),
              const SizedBox(height: 36),

              ElevatedButton(
                onPressed: _loading ? null : _submit,
                child: _loading
                    ? const SizedBox(
                        width: 22,
                        height: 22,
                        child: CircularProgressIndicator(
                            color: Colors.white, strokeWidth: 2))
                    : Text(_isEdit ? '保存修改' : '发布活动'),
              ),
              const SizedBox(height: 24),
            ],
          ),
        ),
      ),
    );
  }

  Widget _label(String text) => Text(
        text,
        style: GoogleFonts.inter(
            fontSize: 14,
            fontWeight: FontWeight.w500,
            color: Colors.black87),
      );

  Widget _timePicker(String label, DateTime? value, VoidCallback onTap,
      IconData icon,
      {Color color = const Color(0xFF6C63FF)}) {
    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(12),
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
        decoration: BoxDecoration(
          color: Colors.grey.shade50,
          borderRadius: BorderRadius.circular(12),
          border: Border.all(color: Colors.grey.shade300),
        ),
        child: Row(
          children: [
            Icon(icon, size: 20, color: color),
            const SizedBox(width: 12),
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(label,
                    style: TextStyle(
                        fontSize: 11, color: Colors.grey.shade500)),
                const SizedBox(height: 2),
                Text(
                  _formatDt(value),
                  style: GoogleFonts.inter(
                      fontSize: 14,
                      fontWeight:
                          value != null ? FontWeight.w500 : FontWeight.normal,
                      color: value != null
                          ? Colors.black87
                          : Colors.grey.shade400),
                ),
              ],
            ),
            const Spacer(),
            Icon(Icons.chevron_right_rounded,
                color: Colors.grey.shade400, size: 20),
          ],
        ),
      ),
    );
  }
}
