import 'package:flutter/material.dart';
import 'messages_en.dart';
import 'messages_zh.dart';

class AppLocalizations {
  final Locale locale;

  AppLocalizations(this.locale);

  static AppLocalizations? of(BuildContext context) {
    return Localizations.of<AppLocalizations>(context, AppLocalizations);
  }

  static final Map<String, Map<String, String>> _localizedValues = {
    'en': MessagesEn.messages,
    'zh': MessagesZh.messages,
  };

  String? get(String key) {
    return _localizedValues[locale.languageCode]?[key] ?? key;
  }

  String get appName => get('appName') ?? 'MeetSpace';
  String get register => get('register') ?? 'Register';
  String get login => get('login') ?? 'Login';
  String get home => get('home') ?? 'Home';
  String get explore => get('explore') ?? 'Explore';
  String get activities => get('activities') ?? 'Activities';
  String get profile => get('profile') ?? 'Profile';
  String get settings => get('settings') ?? 'Settings';
  String get nickname => get('nickname') ?? 'Nickname';
  String get username => get('username') ?? 'Username';
  String get password => get('password') ?? 'Password';
  String get confirmPassword => get('confirmPassword') ?? 'Confirm Password';
  String get noAccount => get('noAccount') ?? "Don't have an account?";
  String get hasAccount => get('hasAccount') ?? 'Already have an account?';
  String get signupSuccess => get('signupSuccess') ?? 'Sign up successful!';
  String get loginSuccess => get('loginSuccess') ?? 'Login successful!';
  String get signedUp => get('signedUp') ?? 'Signed Up';
  String get notSignedUp => get('notSignedUp') ?? 'Not Signed Up';
  String get createActivity => get('createActivity') ?? 'Create Activity';
  String get participated => get('participated') ?? 'Participated';
  String get allActivities => get('allActivities') ?? 'All';
  String get activityDetail => get('activityDetail') ?? 'Activity Detail';
  String get signUp => get('signUp') ?? 'Sign Up';
  String get signUpForActivity => get('signUpForActivity') ?? 'Sign Up';
  String get edit => get('edit') ?? 'Edit';
  String get delete => get('delete') ?? 'Delete';
  String get quit => get('quit') ?? 'Quit';
  String get navigate => get('navigate') ?? 'Navigate';
  String get description => get('description') ?? 'Description';
  String get address => get('address') ?? 'Address';
  String get startTime => get('startTime') ?? 'Start Time';
  String get endTime => get('endTime') ?? 'End Time';
  String get signupDeadline => get('signupDeadline') ?? 'Signup Deadline';
  String get participants => get('participants') ?? 'Participants';
  String get searchActivities =>
      get('searchActivities') ?? 'Search activities...';
  String get editProfile => get('editProfile') ?? 'Edit Profile';
  String get logout => get('logout') ?? 'Logout';
  String get language => get('language') ?? 'Language';
  String get theme => get('theme') ?? 'Theme';
  String get about => get('about') ?? 'About';
  String get versionInfo => get('versionInfo') ?? 'MeetSpace v1.0 2026';
  String get searchLocation => get('searchLocation') ?? 'Search location...';
  String get noResults => get('noResults') ?? 'No results found';
  String get locating => get('locating') ?? 'Locating...';
  String get selectedLocation => get('selectedLocation') ?? 'Selected Location';
  String get addressLoading => get('addressLoading') ?? 'Loading address...';
  String get addressNotAvailable =>
      get('addressNotAvailable') ?? 'Address not available';
  String get latitude => get('latitude') ?? 'Latitude';
  String get longitude => get('longitude') ?? 'Longitude';
  String get confirm => get('confirm') ?? 'Confirm';
  String get cancel => get('cancel') ?? 'Cancel';
  String get selectLocation => get('selectLocation') ?? 'Select Location';

  static const LocalizationsDelegate<AppLocalizations> delegate =
      _AppLocalizationsDelegate();
}

class _AppLocalizationsDelegate
    extends LocalizationsDelegate<AppLocalizations> {
  const _AppLocalizationsDelegate();

  @override
  bool isSupported(Locale locale) => ['en', 'zh'].contains(locale.languageCode);

  @override
  Future<AppLocalizations> load(Locale locale) async {
    return AppLocalizations(locale);
  }

  @override
  bool shouldReload(LocalizationsDelegate<AppLocalizations> old) => false;
}

extension AppLocalizationsExtension on BuildContext {
  AppLocalizations get l10n => AppLocalizations.of(this)!;
}
