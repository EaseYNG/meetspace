import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:meetspace_frontend/services/api_service.dart';
import 'package:meetspace_frontend/services/token_service.dart';

class ProfilePage extends StatefulWidget {
  const ProfilePage({super.key});

  @override
  State<ProfilePage> createState() => _ProfilePageState();
}

class _ProfilePageState extends State<ProfilePage> {
  Map<String, dynamic>? _profile;
  bool _loading = true;
  bool _editing = false;
  bool _saving = false;

  final _firstnameCtrl = TextEditingController();
  final _lastnameCtrl = TextEditingController();
  final _emailCtrl = TextEditingController();
  final _ageCtrl = TextEditingController();
  String? _gender;

  @override
  void initState() {
    super.initState();
    _loadProfile();
  }

  @override
  void dispose() {
    _firstnameCtrl.dispose();
    _lastnameCtrl.dispose();
    _emailCtrl.dispose();
    _ageCtrl.dispose();
    super.dispose();
  }

  Future<void> _loadProfile() async {
    setState(() => _loading = true);
    try {
      final res = await ApiService.getProfile();
      if (res['code'] == 'SUCCESS' && res['data'] != null) {
        final data = res['data'] as Map<String, dynamic>;
        setState(() {
          _profile = data;
          _fillControllers(data);
        });
      }
    } catch (_) {}
    if (mounted) setState(() => _loading = false);
  }

  void _fillControllers(Map<String, dynamic> data) {
    _firstnameCtrl.text = data['firstname'] ?? '';
    _lastnameCtrl.text = data['lastname'] ?? '';
    _emailCtrl.text = data['email'] ?? '';
    _ageCtrl.text = (data['age'] ?? 0).toString();
    _gender = data['gender'];
  }

  Future<void> _saveProfile() async {
    setState(() => _saving = true);
    try {
      final data = {
        'firstname': _firstnameCtrl.text.trim(),
        'lastname': _lastnameCtrl.text.trim(),
        'email': _emailCtrl.text.trim(),
        'age': int.tryParse(_ageCtrl.text) ?? 0,
        'gender': _gender ?? '',
      };
      final res = await ApiService.setProfile(data);
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(
          content: Text(res['msg'] ?? '保存完成'),
          backgroundColor:
              res['code'] == 'SUCCESS' ? Colors.green : Colors.redAccent,
          behavior: SnackBarBehavior.floating,
          shape:
              RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
        ));
        if (res['code'] == 'SUCCESS') {
          setState(() {
            _editing = false;
            _profile = data;
          });
        }
      }
    } catch (_) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(const SnackBar(
          content: Text('保存失败'),
          backgroundColor: Colors.redAccent,
          behavior: SnackBarBehavior.floating,
        ));
      }
    } finally {
      if (mounted) setState(() => _saving = false);
    }
  }

  Future<void> _logout() async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (_) => AlertDialog(
        title: const Text('退出登录'),
        content: const Text('确定要退出登录吗？'),
        actions: [
          TextButton(
              onPressed: () => Navigator.pop(context, false),
              child: const Text('取消')),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text('退出', style: TextStyle(color: Colors.red)),
          ),
        ],
      ),
    );
    if (confirm != true) return;
    await TokenService.clearToken();
    if (mounted) {
      Navigator.pushNamedAndRemoveUntil(context, '/login', (_) => false);
    }
  }

  String get _displayName {
    final fn = _profile?['firstname'] ?? '';
    final ln = _profile?['lastname'] ?? '';
    if (fn.isEmpty && ln.isEmpty) return '未设置姓名';
    return '$fn$ln';
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF8F7FF),
      body: _loading
          ? const Center(
              child: CircularProgressIndicator(color: Color(0xFF6C63FF)))
          : CustomScrollView(
              slivers: [
                SliverAppBar(
                  expandedHeight: 220,
                  pinned: true,
                  backgroundColor: const Color(0xFF6C63FF),
                  automaticallyImplyLeading: false,
                  actions: [
                    IconButton(
                      icon: Icon(
                        _editing ? Icons.close_rounded : Icons.edit_outlined,
                        color: Colors.white,
                      ),
                      onPressed: () {
                        setState(() {
                          if (_editing && _profile != null) {
                            _fillControllers(_profile!);
                          }
                          _editing = !_editing;
                        });
                      },
                    ),
                  ],
                  flexibleSpace: FlexibleSpaceBar(
                    background: Container(
                      decoration: const BoxDecoration(
                        gradient: LinearGradient(
                          colors: [Color(0xFF6C63FF), Color(0xFF9C88FF)],
                          begin: Alignment.topLeft,
                          end: Alignment.bottomRight,
                        ),
                      ),
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          const SizedBox(height: 40),
                          CircleAvatar(
                            radius: 40,
                            backgroundColor:
                                Colors.white.withOpacity(0.3),
                            child: const Icon(Icons.person_rounded,
                                size: 44, color: Colors.white),
                          ),
                          const SizedBox(height: 12),
                          Text(
                            _displayName,
                            style: GoogleFonts.inter(
                              fontSize: 20,
                              fontWeight: FontWeight.bold,
                              color: Colors.white,
                            ),
                          ),
                          const SizedBox(height: 4),
                          Text(
                            _profile?['email'] ?? '未设置邮箱',
                            style: GoogleFonts.inter(
                              fontSize: 13,
                              color: Colors.white.withOpacity(0.8),
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
                SliverToBoxAdapter(
                  child: Padding(
                    padding: const EdgeInsets.all(20),
                    child: Column(
                      children: [
                        _buildInfoCard(),
                        const SizedBox(height: 20),
                        if (_editing) ...[
                          ElevatedButton(
                            onPressed: _saving ? null : _saveProfile,
                            child: _saving
                                ? const SizedBox(
                                    width: 22,
                                    height: 22,
                                    child: CircularProgressIndicator(
                                        color: Colors.white, strokeWidth: 2))
                                : const Text('保存修改'),
                          ),
                          const SizedBox(height: 12),
                        ],
                        _buildActionButton(
                          icon: Icons.logout_rounded,
                          label: '退出登录',
                          color: Colors.red,
                          onTap: _logout,
                        ),
                        const SizedBox(height: 24),
                      ],
                    ),
                  ),
                ),
              ],
            ),
    );
  }

  Widget _buildInfoCard() {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
              color: Colors.black.withOpacity(0.05),
              blurRadius: 10,
              offset: const Offset(0, 2))
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text('个人信息',
              style: GoogleFonts.inter(
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                  color: Colors.black87)),
          const SizedBox(height: 16),
          if (_editing) ...[
            _editField('名', _firstnameCtrl, Icons.person_outline),
            const SizedBox(height: 14),
            _editField('姓', _lastnameCtrl, Icons.person_outline),
            const SizedBox(height: 14),
            _editField('邮箱', _emailCtrl, Icons.email_outlined,
                keyboardType: TextInputType.emailAddress),
            const SizedBox(height: 14),
            _editField('年龄', _ageCtrl, Icons.cake_outlined,
                keyboardType: TextInputType.number),
            const SizedBox(height: 14),
            _genderSelector(),
          ] else ...[
            _infoRow('名', _profile?['firstname'] ?? '-',
                Icons.person_outline),
            _divider(),
            _infoRow('姓', _profile?['lastname'] ?? '-',
                Icons.person_outline),
            _divider(),
            _infoRow(
                '邮箱', _profile?['email'] ?? '-', Icons.email_outlined),
            _divider(),
            _infoRow(
                '年龄',
                (_profile?['age'] ?? 0).toString() == '0'
                    ? '未填写'
                    : '${_profile?['age']} 岁',
                Icons.cake_outlined),
            _divider(),
            _infoRow(
                '性别',
                _profile?['gender'] == null ||
                        _profile!['gender'].toString().isEmpty
                    ? '未填写'
                    : _profile!['gender'],
                Icons.wc_outlined),
          ],
        ],
      ),
    );
  }

  Widget _infoRow(String label, String value, IconData icon) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        children: [
          Icon(icon, size: 18, color: const Color(0xFF6C63FF)),
          const SizedBox(width: 12),
          SizedBox(
            width: 60,
            child: Text(label,
                style: TextStyle(
                    fontSize: 13, color: Colors.grey.shade500)),
          ),
          Expanded(
            child: Text(
              value,
              style: GoogleFonts.inter(
                  fontSize: 14,
                  fontWeight: FontWeight.w500,
                  color: Colors.black87),
            ),
          ),
        ],
      ),
    );
  }

  Widget _divider() => Divider(color: Colors.grey.shade100, height: 1);

  Widget _editField(String label, TextEditingController ctrl, IconData icon,
      {TextInputType keyboardType = TextInputType.text}) {
    return TextFormField(
      controller: ctrl,
      keyboardType: keyboardType,
      decoration: InputDecoration(
        labelText: label,
        prefixIcon: Icon(icon, size: 20),
      ),
    );
  }

  Widget _genderSelector() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text('性别',
            style: TextStyle(fontSize: 13, color: Colors.grey.shade500)),
        const SizedBox(height: 8),
        Row(
          children: [
            _genderBtn('男', '男'),
            const SizedBox(width: 12),
            _genderBtn('女', '女'),
            const SizedBox(width: 12),
            _genderBtn('其他', '其他'),
          ],
        ),
      ],
    );
  }

  Widget _genderBtn(String label, String value) {
    final selected = _gender == value;
    return GestureDetector(
      onTap: () => setState(() => _gender = value),
      child: Container(
        padding:
            const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
        decoration: BoxDecoration(
          color: selected
              ? const Color(0xFF6C63FF)
              : Colors.grey.shade100,
          borderRadius: BorderRadius.circular(8),
        ),
        child: Text(
          label,
          style: TextStyle(
            color: selected ? Colors.white : Colors.grey.shade600,
            fontWeight: FontWeight.w500,
            fontSize: 13,
          ),
        ),
      ),
    );
  }

  Widget _buildActionButton({
    required IconData icon,
    required String label,
    required Color color,
    required VoidCallback onTap,
  }) {
    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(14),
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 16),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(14),
          boxShadow: [
            BoxShadow(
                color: Colors.black.withOpacity(0.05), blurRadius: 8)
          ],
        ),
        child: Row(
          children: [
            Icon(icon, color: color, size: 22),
            const SizedBox(width: 14),
            Text(label,
                style: GoogleFonts.inter(
                    fontSize: 15,
                    fontWeight: FontWeight.w500,
                    color: color)),
            const Spacer(),
            Icon(Icons.chevron_right_rounded,
                color: Colors.grey.shade300, size: 22),
          ],
        ),
      ),
    );
  }
}
