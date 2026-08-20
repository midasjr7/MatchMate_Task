# MatchMate

MatchMate is an offline-first matrimonial matching sample. It downloads profiles from the Random User API, presents them as modern match cards, and persists accept or decline decisions locally.

## Features

- Match cards rendered with Jetpack Compose and `LazyColumn`
- Accept and decline decisions with immediate visual feedback
- Room-backed cache and decision persistence
- Cached profiles and decisions remain usable offline
- Retrofit refresh with recoverable error states
- Connectivity-constrained background refresh through WorkManager

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

## API synchronization note

Profiles come from `https://randomuser.me/api/?results=10`. This read-only API has no endpoint for uploading decisions. Accept and decline choices are stored locally, while profiles refresh when connectivity is available. Decisions are preserved when a refreshed profile has the same login UUID.

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
  worker/                Connectivity-constrained refresh
  ui/theme/              Material theme
gradle/libs.versions.toml Dependency versions
```

## Useful commands

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat lintDebug
.\gradlew.bat assembleDebug
```
