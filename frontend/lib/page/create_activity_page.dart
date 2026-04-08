import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../component/custom_button.dart';
import '../component/page_title.dart';
import '../l10n/app_localizations.dart';
import '../model/activity.dart';
import '../model/request/activity_create_request.dart';
import '../model/request/activity_update_request.dart';
import '../service/activity_service.dart';

class CreateActivityPage extends StatefulWidget {
  final Activity? activity;

  const CreateActivityPage({super.key, this.activity});

  @override
  State<CreateActivityPage> createState() => _CreateActivityPageState();
}

class _CreateActivityPageState extends State<CreateActivityPage> {
  final _formKey = GlobalKey<FormState>();
  final _titleController = TextEditingController();
  final _addressController = TextEditingController();
  final _descriptionController = TextEditingController();
  final _imageController = TextEditingController();
  final _minController = TextEditingController(text: '1');
  final _maxController = TextEditingController(text: '10');

  DateTime? _startTime;
  DateTime? _endTime;
  DateTime? _signupDeadline;

  final ActivityService _activityService = ActivityService();
  bool _isLoading = false;
  bool get _isEdit => widget.activity != null;

  @override
  void initState() {
    super.initState();
    if (_isEdit) {
      final a = widget.activity!;
      _titleController.text = a.title;
      _addressController.text = a.address;
      _descriptionController.text = a.description ?? '';
      _imageController.text = a.image ?? '';
      _minController.text = a.minParticipants.toString();
      _maxController.text = a.maxParticipants.toString();
      _startTime = a.startTime;
      _endTime = a.endTime;
      _signupDeadline = a.signupDeadline;
    }
  }

  @override
  void dispose() {
    _titleController.dispose();
    _addressController.dispose();
    _descriptionController.dispose();
    _imageController.dispose();
    _minController.dispose();
    _maxController.dispose();
    super.dispose();
  }

  Future<void> _pickDate(String field) async {
    final date = await showDatePicker(
      context: context,
      initialDate: DateTime.now(),
      firstDate: DateTime.now(),
      lastDate: DateTime(2030),
    );
    if (date == null || !mounted) return;

    final time = await showTimePicker(
      context: context,
      initialTime: TimeOfDay.now(),
    );
    if (time == null || !mounted) return;

    final dt = DateTime(
      date.year,
      date.month,
      date.day,
      time.hour,
      time.minute,
    );
    setState(() {
      if (field == 'start') {
        _startTime = dt;
      } else if (field == 'end') {
        _endTime = dt;
      } else if (field == 'deadline') {
        _signupDeadline = dt;
      }
    });
  }

  Future<void> _handleSubmit() async {
    if (!_formKey.currentState!.validate()) return;
    if (_startTime == null || _endTime == null || _signupDeadline == null) {
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(const SnackBar(content: Text('Please select all times')));
      return;
    }

    setState(() => _isLoading = true);
    try {
      if (_isEdit) {
        await _activityService.updateActivity(
          widget.activity!.id,
          ActivityUpdateRequest(
            title: _titleController.text.trim(),
            startTime: _startTime!,
            endTime: _endTime!,
            signupDeadline: _signupDeadline!,
            address: _addressController.text.trim(),
            image: _imageController.text.trim(),
            description: _descriptionController.text.trim(),
          ),
        );
      } else {
        await _activityService.createActivity(
          ActivityCreateRequest(
            title: _titleController.text.trim(),
            startTime: _startTime!,
            endTime: _endTime!,
            signupDeadline: _signupDeadline!,
            address: _addressController.text.trim(),
            image: _imageController.text.trim(),
            description: _descriptionController.text.trim(),
          ),
        );
      }
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(_isEdit ? 'Updated!' : 'Created!')),
        );
        Navigator.of(context).pop();
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(
          context,
        ).showSnackBar(SnackBar(content: Text(e.toString())));
      }
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  String _formatDt(DateTime? dt) {
    if (dt == null) return 'Select...';
    return '${dt.year}-${dt.month.toString().padLeft(2, '0')}-${dt.day.toString().padLeft(2, '0')} ${dt.hour.toString().padLeft(2, '0')}:${dt.minute.toString().padLeft(2, '0')}';
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
          onPressed: () => Navigator.of(context).pop(),
        ),
        title: Text(
          _isEdit ? l10n.edit : l10n.createActivity,
          style: GoogleFonts.inter(
            fontSize: 17,
            fontWeight: FontWeight.w600,
            color: Colors.grey[800],
          ),
        ),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.symmetric(horizontal: 16),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              PageTitle(title: _isEdit ? l10n.edit : l10n.createActivity),
              _buildTextField(_titleController, l10n.explore),
              const SizedBox(height: 12),
              _buildTextField(_addressController, l10n.address),
              const SizedBox(height: 12),
              _buildTextField(
                _descriptionController,
                l10n.description,
                maxLines: 3,
              ),
              const SizedBox(height: 12),
              _buildTextField(_imageController, 'Image URL'),
              const SizedBox(height: 12),
              _buildDateField(
                l10n.startTime,
                _startTime,
                () => _pickDate('start'),
              ),
              _buildDateField(l10n.endTime, _endTime, () => _pickDate('end')),
              _buildDateField(
                l10n.signupDeadline,
                _signupDeadline,
                () => _pickDate('deadline'),
              ),
              const SizedBox(height: 12),
              Row(
                children: [
                  Expanded(
                    child: _buildTextField(
                      _minController,
                      'Min',
                      isNumber: true,
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: _buildTextField(
                      _maxController,
                      'Max',
                      isNumber: true,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 28),
              CustomButton(
                text: _isEdit ? 'Update' : 'Create',
                onPressed: _handleSubmit,
                isLoading: _isLoading,
              ),
              const SizedBox(height: 32),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildTextField(
    TextEditingController controller,
    String label, {
    int maxLines = 1,
    bool isNumber = false,
  }) {
    return TextFormField(
      controller: controller,
      maxLines: maxLines,
      keyboardType: isNumber ? TextInputType.number : TextInputType.text,
      decoration: InputDecoration(
        labelText: label,
        labelStyle: GoogleFonts.inter(color: Colors.grey[600]),
        border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(color: Color(0xFF4CAF50), width: 1.5),
        ),
      ),
    );
  }

  Widget _buildDateField(String label, DateTime? value, VoidCallback onTap) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 12),
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: InputDecorator(
          decoration: InputDecoration(
            labelText: label,
            labelStyle: GoogleFonts.inter(color: Colors.grey[600]),
            border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
            suffixIcon: Icon(Icons.calendar_today, color: Colors.grey[500]),
          ),
          child: Text(
            _formatDt(value),
            style: GoogleFonts.inter(fontSize: 15, color: Colors.grey[800]),
          ),
        ),
      ),
    );
  }
}
