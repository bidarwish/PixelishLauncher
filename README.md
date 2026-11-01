# Pixelish Launcher (Android 12+)

An early Java starter project for an Android home launcher. It declares HOME/DEFAULT intent categories, lists launchable apps, supports filtering, launches apps, and opens app info on long press.

## Build
- Install Android Studio and Android SDK Platform 34.
- Open this folder as a project and allow Gradle sync (Android Gradle Plugin 8.2.2).
- Choose Build > Build APK(s).
- APK output: `app/build/outputs/apk/debug/app-debug.apk`.

## Current limitations
This is a starter implementation, not a full Pixel Launcher clone. It does not yet implement true home-screen drag/drop, folders, widgets, wallpaper picker integration, notification dots, At a Glance, gesture navigation integration, or Pixel-exclusive Google features. Android Studio/Gradle and an Android SDK are required to compile it.
