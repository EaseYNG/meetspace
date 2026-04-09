import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../component/custom_button.dart';
import '../component/page_title.dart';
import '../l10n/app_localizations.dart';
import '../model/activity.dart';
import '../model/request/activity_create_request.dart';
import '../model/request/activity_update_request.dart';
import '../service/activity_service.dart';
import '../service/location_service.dart';
import 'map_picker_page.dart';

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

  double? _latitude;
  double? _longitude;

  final ActivityService _activityService = ActivityService();
  final LocationService _locationService = LocationService();
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
      _latitude = a.latitude;
      _longitude = a.longitude;
    } else {
      _autoLocate();
    }
  }

  Future<void> _autoLocate() async {
    final locationResult = await _locationService
        .getCurrentLocationWithAddress();
    if (mounted && locationResult != null) {
      setState(() {
        _latitude = locationResult.latitude;
        _longitude = locationResult.longitude;
        // 自动填充地址，如果当前地址栏为空
        if (_addressController.text.isEmpty) {
          _addressController.text = locationResult.address ?? "";
        }
      });
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
            minParticipants: int.tryParse(_minController.text),
            maxParticipants: int.tryParse(_maxController.text),
            image: _imageController.text.trim(),
            description: _descriptionController.text.trim(),
            latitude: _latitude,
            longitude: _longitude,
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
            minParticipants: int.tryParse(_minController.text) ?? 1,
            maxParticipants: int.tryParse(_maxController.text) ?? 10,
            image: _imageController.text.trim(),
            description: _descriptionController.text.trim(),
            latitude: _latitude,
            longitude: _longitude,
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
      backgroundColor: Theme.of(context).scaffoldBackgroundColor,
      appBar: AppBar(
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back_ios_new, size: 18),
          onPressed: () => Navigator.of(context).pop(),
        ),
        title: Text(
          _isEdit ? l10n.edit : l10n.createActivity,
          style: GoogleFonts.inter(fontSize: 17, fontWeight: FontWeight.w600),
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
              _buildLocationField(),
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

  Widget _buildLocationField() {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    final l10n = context.l10n;

    return Container(
      decoration: BoxDecoration(
        color: isDark ? const Color(0xFF1E1E1E) : Colors.white,
        borderRadius: BorderRadius.circular(12),
        border: Border.all(
          color: isDark ? Colors.transparent : Colors.grey[300]!,
        ),
      ),
      child: Column(
        children: [
          TextFormField(
            controller: _addressController,
            style: GoogleFonts.inter(
              color: isDark ? Colors.white : Colors.grey[900],
            ),
            decoration: InputDecoration(
              labelText: l10n.address,
              labelStyle: GoogleFonts.inter(
                color: isDark ? Colors.grey[400] : Colors.grey[600],
              ),
              border: InputBorder.none,
              contentPadding: const EdgeInsets.symmetric(
                horizontal: 12,
                vertical: 16,
              ),
            ),
          ),
          Divider(
            height: 1,
            color: isDark ? Colors.grey[800] : Colors.grey[200],
          ),
          Row(
            children: [
              Expanded(
                child: TextButton.icon(
                  onPressed: () async {
                    setState(() => _isLoading = true);
                    final locationResult = await _locationService
                        .getCurrentLocationWithAddress();
                    setState(() => _isLoading = false);

                    if (locationResult != null) {
                      setState(() {
                        _latitude = locationResult.latitude;
                        _longitude = locationResult.longitude;
                        _addressController.text =
                            locationResult.address ??
                            "当前定位 (${locationResult.latitude.toStringAsFixed(4)}, ${locationResult.longitude.toStringAsFixed(4)})";
                      });
                    } else if (mounted) {
                      ScaffoldMessenger.of(context).showSnackBar(
                        const SnackBar(content: Text('无法获取当前位置，请检查权限或网络')),
                      );
                    }
                  },
                  icon: Icon(
                    Icons.my_location,
                    size: 18,
                    color: Colors.blue[400],
                  ),
                  label: Text(
                    '自动定位',
                    style: GoogleFonts.inter(
                      color: Colors.blue[400],
                      fontSize: 13,
                    ),
                  ),
                ),
              ),
              Container(
                width: 1,
                height: 24,
                color: isDark ? Colors.grey[800] : Colors.grey[200],
              ),
              Expanded(
                child: TextButton.icon(
                  onPressed: () async {
                    final result = await Navigator.of(context)
                        .push<Map<String, dynamic>>(
                          MaterialPageRoute(
                            builder: (_) => MapPickerPage(
                              initialLat: _latitude,
                              initialLng: _longitude,
                            ),
                          ),
                        );
                    if (result != null) {
                      setState(() {
                        _latitude = result['lat'] as double;
                        _longitude = result['lng'] as double;
                        final addr = result['address'] as String;
                        _addressController.text =
                            addr.isNotEmpty &&
                                addr != '无法解析当前地址' &&
                                addr != '无法获取该位置的详细地址'
                            ? addr
                            : "地图选点 (${_latitude!.toStringAsFixed(4)}, ${_longitude!.toStringAsFixed(4)})";
                      });
                    }
                  },
                  icon: Icon(
                    Icons.map_outlined,
                    size: 18,
                    color: Colors.orange[400],
                  ),
                  label: Text(
                    '地图选点',
                    style: GoogleFonts.inter(
                      color: Colors.orange[400],
                      fontSize: 13,
                    ),
                  ),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildTextField(
    TextEditingController controller,
    String label, {
    int maxLines = 1,
    bool isNumber = false,
  }) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    return TextFormField(
      controller: controller,
      maxLines: maxLines,
      keyboardType: isNumber ? TextInputType.number : TextInputType.text,
      style: GoogleFonts.inter(color: isDark ? Colors.white : Colors.grey[900]),
      decoration: InputDecoration(
        labelText: label,
        labelStyle: GoogleFonts.inter(
          color: isDark ? Colors.grey[400] : Colors.grey[600],
        ),
        filled: true,
        fillColor: isDark ? const Color(0xFF1E1E1E) : Colors.white,
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: isDark
              ? BorderSide.none
              : BorderSide(color: Colors.grey[300]!),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(color: Color(0xFF4CAF50), width: 1.5),
        ),
      ),
    );
  }

  Widget _buildDateField(String label, DateTime? dt, VoidCallback onTap) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    return GestureDetector(
      onTap: onTap,
      child: Container(
        margin: const EdgeInsets.only(bottom: 12),
        padding: const EdgeInsets.all(12),
        decoration: BoxDecoration(
          color: isDark ? const Color(0xFF1E1E1E) : Colors.white,
          borderRadius: BorderRadius.circular(12),
          border: Border.all(
            color: isDark ? Colors.transparent : Colors.grey[300]!,
          ),
        ),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(
              label,
              style: GoogleFonts.inter(
                color: isDark ? Colors.grey[400] : Colors.grey[600],
              ),
            ),
            Text(
              _formatDt(dt),
              style: GoogleFonts.inter(
                color: isDark ? Colors.white : Colors.grey[800],
                fontWeight: FontWeight.w500,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
