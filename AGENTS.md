# MeetSpace — Agent Guide

Full-stack activity discovery platform: **Spring Boot 3.4.5 (Java 21) backend** + **Flutter 3.41.6 (Dart ^3.11.4) frontend**.

---

## Project structure

```
meetspace/
├── pom.xml                          # Maven — Spring Boot + MyBatis-Plus + Redis/Redisson
├── src/main/java/com/venus/meetspace/
│   ├── common/                      # Enums, constants, exceptions, Result<T> wrapper
│   ├── config/                      # Security, Redis, MyBatis-Plus, Jackson, Knife4j, WebMvc
│   ├── controller/                  # REST endpoints (6 controllers)
│   ├── convert/                     # MapStruct (ActivityConvert, UserConvert)
│   ├── model/
│   │   ├── entity/                  # DB entities (@TableName, @Data)
│   │   ├── cmd/                     # Write DTOs (XxxCmd)
│   │   ├── vo/                      # Response DTOs (XxxVO)
│   │   └── query/                   # Search DTOs (XxxQuery)
│   ├── repository/                  # MyBatis-Plus Mappers + XML in resources/repository/
│   ├── service/ + impl/             # Interface-first, extend IService<Entity>
│   ├── security/                    # CustomUserDetails, SecurityUtil (static current-user access)
│   ├── cache/                       # CacheService interface + Redis impl (with lock/penetration defense)
│   └── aspect/                      # ActivityScheduler (cron: close expired activities every minute)
├── frontend/
│   ├── pubspec.yaml                 # Flutter — Dio, flutter_map, google_fonts, json_serializable
│   └── lib/
│       ├── main.dart                # Entry: init ApiClient, ThemeManager, LocationSearchService
│       ├── api/api_client.dart       # Singleton Dio client, token persistence, auto-host-detect
│       ├── model/                    # @JsonSerializable() models + .g.dart generated files
│       │   └── request/              # Request DTOs (XxxRequest)
│       ├── service/                  # Business logic wrappers calling ApiClient.dio
│       ├── page/                     # 10 screens (StatefulWidget, no router package)
│       ├── component/                # 4 shared widgets (ActivityCard, CustomButton, PageTitle, SearchLocationField)
│       ├── theme/theme_manager.dart  # Singleton, SharedPreferences-persisted light/dark toggle
│       └── l10n/                     # Custom AppLocalizations (zh/en), context.l10n extension
```

---

## Essential commands

### Backend (Java/Maven)

| Command | Purpose |
|---|---|
| `./mvnw spring-boot:run` | Start backend on port 8080 |
| `./mvnw clean compile` | Compile only |
| `./mvnw clean test -Dtest=MyBatisPlusTest` | Run specific test |
| `./mvnw clean test` | Run all tests |
| `./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=dev"` | Run with profile |

### Frontend (Flutter)

| Command | Purpose |
|---|---|
| `flutter pub get` | Install dependencies |
| `flutter clean` | Clean build |
| `flutter pub run build_runner build --delete-conflicting-outputs` | Regenerate `.g.dart` files |
| `flutter run -d <device>` | Run on device/emulator |
| `flutter test` | Run widget tests |
| `flutter analyze` | Lint check |

**For Android emulator**: `flutter run -d emulator-5554` (default uses `10.0.2.2` for host localhost)

**For real device**: `--dart-define=BASE_URL=http://<your-lan-ip>:8080`

---

## 🔴 CRITICAL: Frontend-backend API path mismatch

This is the **single most important gotcha** in this codebase. The backend was refactored to RESTful `/api/v1/` paths, but **the frontend was never updated** to match.

| Frontend calls (`service/*.dart`) | Backend expects (`controller/`) |
|---|---|
| `/user/register` | `/api/v1/auth/register` |
| `/user/login` | `/api/v1/auth/login` |
| `/activity/create` | `/api/v1/activities` (POST) |
| `/activity/update/{id}` | `/api/v1/activities/{id}` (PATCH) |
| `/activity/delete/{id}` | `/api/v1/activities/{id}` (DELETE) |
| `/activity/signup/{id}` | `/api/v1/activities/{id}/participants` (POST) |
| `/activity/quit/{id}` | `/api/v1/activities/{id}/participants/me` (DELETE) |
| `/activity/related` | `/api/v1/users/me/activities/participated` |
| `/activity/created` | `/api/v1/users/me/activities/created` |
| `/activity/signed_up` | `/api/v1/users/me/activities/signed-up` |
| `/activity/search` | `/api/v1/activities/search` (POST) |
| `/activity/list` | `/api/v1/activities` (GET — no such endpoint exists yet) |

**Any work involving frontend-backend integration must first align these paths.** See `change.md` for the full refactoring mapping.

---

## Auth: Session-based (NOT JWT)

- **Backend**: Spring Security with `HttpSession` and `JSESSIONID` cookie. `SecurityUtil.getCurrentUserId()` for controller access.
- **Frontend**: The `ApiClient` stores a string `token` in `SharedPreferences` and sends it as `Authorization: Bearer <token>` — this is a **leftover from the old JWT era** and is incompatible with Session auth. The frontend should send the `JSESSIONID` cookie (handled automatically by Dio if credentials are enabled).
- `SecurityConfig.java` permits unauthenticated access to `/api/v1/auth/**` and `/api/v1/health/**`; everything else requires auth.

---

## Backend conventions

- **Service pattern**: Interface extends `IService<Entity>`, Impl extends `ServiceImpl<Mapper, Entity>`, constructor injection
- **Exceptions**: Throw `BusinessException(ResultCode, message)`, caught by `GlobalExceptionHandler` → `Result<T>` JSON
- **Result wrapper**: Always `Result.success(data)` or `Result.success(data, msg)`. Controllers return `Result<T>`.
- **Soft delete**: Activity status set to `ActivityStatus.DELETED`, never actually removed.
- **DB tables**: MyBatis-Plus **does not auto-create** tables. Run the DDL from README manually.
- **Logging**: `@Slf4j` + `log.info(...)` on significant operations (create, update, delete).
- **Validation**: `@Valid` on `@RequestBody` cmd params. Bean Validation annotations on cmd fields.
- **MapStruct**: `ActivityConvert.toEntity(cmd)`, `.toVO(entity)`, `.toVOList(list)`, `.update(entity, cmd)`.
- **Mapper XML**: In `src/main/resources/repository/`, for custom queries. Simple CRUD uses MyBatis-Plus `BaseMapper`.
- **Scheduler**: `ActivityScheduler` runs every minute via `@Scheduled(cron = "0 * * * * *")` — auto-closes READY activities past their signup deadline.
- **Geo search**: `ActivityMapper.findByConditions` uses MySQL `ST_Distance_Sphere` for spatial distance filtering.

---

## Frontend conventions

- **UI style**: Material 3, light green accent (`colorSchemeSeed: 0xFF4CAF50`), clean/soft colors. **No blue-purple gradients.**
- **Typography**: `GoogleFonts.inter()` everywhere.
- **Localization**: All user-facing text via `context.l10n.<key>`. Supported: `zh` (default), `en`.
- **State management**: Manual `StatefulWidget` with `setState()`. No Riverpod/Bloc/Provider.
- **Navigation**: Direct `Navigator.of(context).push(MaterialPageRoute(...))` — no router package.
- **Network**: Singleton `ApiClient` with Dio. Auto-detects backend host (`localhost` / `10.0.2.2` / `127.0.0.1`). Verbose logging enabled.
- **Async patterns**: Always check `mounted` after `await`. Dispose controllers in `dispose()`. Use `try/catch/finally` with `setState` for loading state.
- **JSON models**: `@JsonSerializable()` + generated `.g.dart`. Run `build_runner` after model changes.
- **API token**: JWT token stored in SharedPreferences (leftover, not actually used by backend).
- **Map integration**: Amap (高德地图) with hardcoded API keys (`_webApiKey`, `_androidApiKey`). `flutter_map` for tile rendering, `amapuri://` schemes for navigation.

---

## Cache strategy (Redis + Redisson)

| Cache key pattern | TTL | Notes |
|---|---|---|
| `activity:{id}` | 30 min | Activity detail, invalidated on write |
| `user:profile:{id}` | 1 hour | User profile |
| `activity:ready:list` | 5 min | Ready activities list |

**Defense layers**:
1. **Penetration**: `CacheService.getOrLoad` — caches null values with short TTL
2. **Breakdown**: `CacheService.getOrLoadWithLock` — Redisson distributed lock, single-threaded DB rebuild
3. **Concurrent signup**: Redis `SETNX` atomic operation prevents double-signup

---

## Elasticsearch

**Planned but not yet implemented.** The README describes it, and `activity-settings.json` exists with IK analyzer config, but:
- No `spring-data-elasticsearch` dependency in `pom.xml`
- No `search/` package in the Java code
- No actual ES indexing or query code exists

The current search uses MySQL `findByConditions` with `ActivitySearchQuery`.

---

## Testing

- **Backend**: Mostly stubs. Only `MyBatisPlusTest` has an actual assertion. `MeetspaceApplicationTests` just loads context.
- **Frontend**: One widget smoke test (`widget_test.dart`).
- **No mock tests, no integration tests, no controller tests with assertions exist.**

---

## Key files

| File | Purpose |
|---|---|
| `change.md` | Documents the entire JWT→Session refactoring + REST API path migration |
| `start` | Dev startup script (Redis + ES + app) |
| `frontend/code_instruction.md` | Detailed Flutter coding conventions for AI agents |
| `frontend/.github/copilot-instructions.md` | Copilot-specific context for the Flutter frontend |
| `src/main/java/com/venus/meetspace/common/constant/ApiConstants.java` | API version prefix constant |
| `src/main/java/com/venus/meetspace/cache/CacheService.java` | Cache abstraction with penetration/breakdown protection |
| `src/main/java/com/venus/meetspace/aspect/ActivityScheduler.java` | Cron-based activity status auto-close |

---

## Things that will trip you up

1. **Frontend API paths are wrong** — they use the old `/activity/create`, `/user/login` style, not the current `/api/v1/activities`, `/api/v1/auth/login`.
2. **Frontend still has JWT auth** — `Authorization: Bearer <token>` header persists from old JWT code; backend now uses JSESSIONID cookies.
3. **ES is documented but not wired** — don't try to add ES queries unless you also add the pom dependency.
4. **No auto-DDL** — MySQL tables must be created manually.
5. **Tests are stubs** — don't rely on test coverage; add tests as you go.
6. **Amap API keys are hardcoded** — `2d6ee8e3071bf17f2d0694af2afc3b17` (web) and `7907bea639e73c134f435d6cd2aa2120` (Android) are in source.
7. **Frontend locale is hardcoded to `zh`** — `main.dart` sets `locale: const Locale('zh')`.
8. **MyBatis-Plus's `ServiceImpl`** — when extending `ServiceImpl<Mapper, Entity>`, you get `this.getById(id)`, `this.save(entity)`, `this.updateById(entity)`, `this.list(query)` for free.
9. **Handle `mounted`** — every frontend async method that calls `setState` must check `if (mounted)` first.
10. **Dio interceptor logging** — every request/response/error is printed to console via `LogInterceptor`. This is verbose but useful for debugging.
