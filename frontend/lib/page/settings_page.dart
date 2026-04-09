import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../component/page_title.dart';
import '../l10n/app_localizations.dart';
import '../theme/theme_manager.dart';

class SettingsPage extends StatefulWidget {
  const SettingsPage({super.key});

  @override
  State<SettingsPage> createState() => _SettingsPageState();
}

class _SettingsPageState extends State<SettingsPage> {
  @override
  Widget build(BuildContext context) {
    final l10n = context.l10n;
    final isDark = ThemeManager().isDark;

    return Scaffold(
      backgroundColor: Theme.of(context).scaffoldBackgroundColor,
      appBar: AppBar(
        backgroundColor: Theme.of(context).appBarTheme.backgroundColor,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back_ios_new, size: 18),
          onPressed: () => Navigator.of(context).pop(),
        ),
        title: Text(
          l10n.settings,
          style: GoogleFonts.inter(
            fontSize: 17,
            fontWeight: FontWeight.w600,
            color: Theme.of(context).textTheme.titleLarge?.color,
          ),
        ),
      ),
      body: ListView(
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 16),
        children: [
          PageTitle(title: l10n.settings),
          const SizedBox(height: 12),
          _buildSettingItem(
            icon: Icons.language_outlined,
            title: l10n.language,
            subtitle: '简体中文',
            onTap: () {},
          ),
          _buildSettingItem(
            icon: isDark ? Icons.dark_mode : Icons.light_mode,
            title: l10n.theme,
            subtitle: isDark ? '深色模式' : '浅色模式',
            onTap: () async {
              await ThemeManager().toggleTheme(!isDark);
              if (mounted) setState(() {});
            },
            trailing: Switch(
              value: isDark,
              onChanged: (val) async {
                await ThemeManager().toggleTheme(val);
                if (mounted) setState(() {});
              },
              activeColor: const Color(0xFF4CAF50),
            ),
          ),
          _buildSettingItem(
            icon: Icons.info_outline,
            title: l10n.about,
            subtitle: l10n.versionInfo,
            onTap: () {},
          ),
        ],
      ),
    );
  }

  Widget _buildSettingItem({
    required IconData icon,
    required String title,
    required String subtitle,
    required VoidCallback onTap,
    Widget? trailing,
  }) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(12),
      child: Container(
        margin: const EdgeInsets.only(bottom: 8),
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 14),
        decoration: BoxDecoration(
          color: isDark ? Colors.grey[900] : Colors.white,
          borderRadius: BorderRadius.circular(12),
          boxShadow: [
            if (!isDark)
              BoxShadow(
                color: Colors.black.withValues(alpha: 0.03),
                blurRadius: 10,
                offset: const Offset(0, 4),
              ),
          ],
        ),
        child: Row(
          children: [
            Icon(
              icon,
              size: 22,
              color: isDark ? Colors.grey[400] : Colors.grey[600],
            ),
            const SizedBox(width: 14),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    title,
                    style: GoogleFonts.inter(
                      fontSize: 15,
                      fontWeight: FontWeight.w500,
                      color: isDark ? Colors.white : Colors.grey[800],
                    ),
                  ),
                  const SizedBox(height: 2),
                  Text(
                    subtitle,
                    style: GoogleFonts.inter(
                      fontSize: 13,
                      color: isDark ? Colors.grey[500] : Colors.grey[500],
                    ),
                  ),
                ],
              ),
            ),
            if (trailing != null)
              trailing
            else
              Icon(
                Icons.chevron_right,
                size: 20,
                color: isDark ? Colors.grey[600] : Colors.grey[300],
              ),
          ],
        ),
      ),
    );
  }
}
