# MatchMate

MatchMate is a starter Android application written in Kotlin. The initial project uses a single `app` module, Jetpack Compose, and Material 3.

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
app/
  src/main/java/com/example/matchmate/  Kotlin source
  src/main/java/.../ui/theme/           Compose theme
  src/main/res/                         Android resources
  src/test/                             Local unit tests
  src/androidTest/                      Instrumented tests
gradle/libs.versions.toml               Dependency versions
```

## Useful commands

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleDebug
```
