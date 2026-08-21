# MatchMate

MatchMate is an offline-first matrimonial matching sample. It downloads profiles from the Random User API, presents them as modern match cards, and persists accept or decline decisions locally.

## Features

- Match cards rendered with Jetpack Compose and `LazyColumn`
- Gender-aware profile placeholders with in-card photo retry
- Debug-only deterministic image failures for testing placeholder and retry states
- Accept and decline decisions with immediate visual feedback
- Room-backed cache and decision persistence
- Cached profiles and decisions remain usable offline
- Retrofit refresh with recoverable error states
- Connectivity-constrained background refresh through WorkManager
- Incremental profile loading with Paging 3, Room, and `RemoteMediator`
- Lifecycle-bound foreground tasks and cancellation-safe error handling
- Supervised activity startup tasks with sanitized exception logging
- Supervised ViewModel operations with cancellation-safe UI cleanup
- Idempotent periodic refresh scheduling when the main activity starts

## Architecture

```text
Compose UI -> ViewModel -> Repository -> Room
                                 |
                                 +-> Random User API
```

Room is the source of truth. Network refreshes update it transactionally while preserving existing decisions. The assignment mentions RecyclerView and LiveData; this implementation uses their modern Jetpack equivalents: Compose `LazyColumn` and lifecycle-aware `StateFlow`.

## Libraries

- Jetpack Compose and Material 3
- Lifecycle ViewModel and StateFlow
- Room with KSP
- Retrofit, Gson, and OkHttp
- Coil Compose
- WorkManager
- Hilt dependency injection
- Paging 3 and Paging Compose

## API synchronization note

Profiles come from `https://randomuser.me/api/?results=10`. A stable API seed keeps the ten generated profile UUIDs consistent across refreshes. This read-only API has no endpoint for uploading decisions, so accept and decline choices are stored locally while profiles refresh when connectivity is available.

The HTTPS base URL is generated into `BuildConfig` from the `PROFILE_BASE_URL` Gradle property. Override it for another environment with `-PPROFILE_BASE_URL=https://example.com/` (the trailing slash is required by Retrofit). URLs and other `BuildConfig` values are not treated as secrets because they can be recovered from an APK. Cleartext traffic and Android backup are disabled, and HTTP logging is enabled only in debug builds.

Profile names, locations, email addresses, and phone numbers are encrypted before Room persistence using AES-256-GCM. The encryption key is generated and retained by Android Keystore and is not embedded in the APK. The single `1 → 2` migration adds pagination and gender columns and encrypts profile data left by version 1. Profile IDs and decision status remain plaintext because they are required for stable identity and database updates.

Debug builds intentionally replace roughly one in five profile image URLs with a missing local file so the placeholder and retry UI can be exercised. `SIMULATE_IMAGE_FAILURES` is generated as `false` for release builds.

## Requirements

- Android Studio with JDK 17 or newer
- Android SDK 37
- An emulator or device running Android 7.0 (API 24) or newer

## Getting started

1. Open this directory in Android Studio.
2. Allow Gradle to sync and install any requested SDK components.
3. Select the `app` run configuration and an emulator or connected device.
4. Click **Run**, or build from a terminal:

   ```powershell
   .\gradlew.bat assembleDebug
   ```

The debug APK is generated under `app/build/outputs/apk/debug/`.

## Project structure

```text
app/src/main/java/com/example/matchmate/
  data/                  Retrofit, Room, and repository
  domain/                Match model and decision status
  ui/matches/            Compose screen and ViewModel
  ui/components/         Reusable match cards and screen states
  worker/                Connectivity-constrained refresh
  di/                    Hilt dependency providers
  ui/theme/              Material theme
gradle/libs.versions.toml Dependency versions
```

## Useful commands

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat connectedDebugAndroidTest
.\gradlew.bat lintDebug
.\gradlew.bat assembleDebug
```

Local tests cover DTO/entity encryption mapping, gender/status preservation, safe error messages, and ViewModel accept/decline success and failure cleanup. Instrumented tests cover Room-backed Paging refresh and append, cached-data retention after network failure, decision preservation, Compose decision actions/states, and failed-image placeholder retry behavior.
