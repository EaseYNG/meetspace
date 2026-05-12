# MeetSpace Frontend AI Agent Instructions

## What this workspace is

This is the Flutter frontend for the MeetSpace app, a cross-platform activity discovery and event management client.

Key points:

- Flutter app targeting mobile, desktop, and web
- Built with Flutter 3.41.6 and Dart ^3.11.4
- Uses Material 3 with a clean, light UI style
- Internationalized for Chinese and English
- Network layer using Dio and JWT-based backend communication
- Project already includes Android emulator networking guidance in `ANDROID_SETUP.md`

## Important files and directories

- `lib/main.dart` – app entry point; initializes `ApiClient` and `ThemeManager`
- `lib/api/` – API client, networking, request/response handling
- `lib/model/` – data models and JSON serializable DTOs
- `lib/service/` – business logic and service wrappers
- `lib/page/` – screens and page layouts
- `lib/component/` – reusable UI widgets
- `lib/theme/` – theme management and colors
- `lib/l10n/` – localization delegates and generated translation resources

## Existing documentation to reference

- `README.md` — project overview, architecture, and technology stack
- `code_instruction.md` — frontend coding and UI style conventions
- `ANDROID_SETUP.md` — Android/Dio networking notes and emulator host setup

## How to help effectively

When modifying or writing frontend code:

- Keep UI clean and simple; avoid flashy or AI-style gradients
- Prefer soft, light colors with green/blue/yellow accents
- Use `GoogleFonts.inter()` for text styles unless there is a strong reason not to
- Follow existing widget and page structure instead of inventing a completely new architecture
- Keep code idiomatic Dart and declarative Flutter
- Use null safety and prefer explicit types when helpful
- For model classes, use `json_serializable` and keep request/response shapes aligned with the backend
- Preserve i18n by sourcing strings from `lib/l10n/app_localizations.dart`

## Local development commands

Common Flutter commands for this repo:

- `flutter pub get`
- `flutter clean`
- `flutter pub run build_runner build --delete-conflicting-outputs`
- `flutter run -d <device>`
- `flutter test`

## Environment notes

- `pubspec.yaml` declares dependencies such as `dio`, `google_fonts`, `json_annotation`, `url_launcher`, `intl`, `shared_preferences`, `geolocator`, `flutter_map`, and `latlong2`
- Android emulator backend access is handled via `10.0.2.2` for host machine connections
- `main.dart` sets default locale to Chinese (`Locale('zh')`) and supports both `zh` and `en`

## What to avoid

- Do not apply heavy visual themes or bright neon/gradient-heavy UI
- Do not bypass the existing page/component structure without a strong reason
- Avoid hardcoding strings directly in widgets instead use localization
- Avoid mixing backend logic inside UI widgets; use the service layer when possible

## Example prompts

- "Add a new page under `lib/page/` that shows the current user's created events using the existing card style."
- "Refactor `lib/service/activity_service.dart` so activity filtering is cleaner and add a search bar in `lib/page/explore_page.dart`."
- "Improve the login form in `lib/page/login_page.dart` to show field validation errors and use `GoogleFonts.inter()` consistently."
- "Update the Android network base URL logic in `lib/api/api_client.dart` and document the emulator setup in `ANDROID_SETUP.md`."
