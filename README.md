# Pulse Media Player for Android

Native Android audio player with a pure-black interface. Requires Android 8.0 or later.

## Download Android APK

[Download Pulse Media Player APK](https://github.com/sduos4029-CODE/Audio_video-player-/raw/refs/heads/main/downloads/Pulse-Media-Player.apk)

This is a debug build for testing. Open the link in your Android browser, download the APK, and allow installation from that browser if prompted. Device playback and lock-screen behavior have not yet been verified.

## Features

- Normal rendering or full-interface dot-matrix rendering (artwork, text and controls).
- White, red, yellow, blue, purple, red/yellow/white and blue/purple/white themes.
- Filled or outline play/pause button, seek bar, ±10 seconds and device volume.
- Local audio import using Android's document picker; no broad storage permission.
- Direct HTTPS audio streaming and permitted downloads to an app-private offline library.
- Background playback, audio focus, headphone-disconnect pause, media notification and system lock-screen controls.
- Optional full-screen player over the lock screen while this activity is open. It does not bypass authentication, replace Android's secure lock screen, or launch itself from the background. Behavior varies by device.
- Embedded local artwork when available, with a drawn moon/ocean fallback.

## Run

Open this directory in Android Studio, use JDK 17 or 21, install Android SDK 35, sync, then run `app` on a device. Grant notification permission for media notifications.

From a shell with the Android SDK configured:

```sh
./gradlew :app:assembleDebug :app:lintDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Menu (top right): theme, dot matrix, button fill and lock-screen mode. Tap the Pulse Media Player title or bottom center to toggle normal/dot-matrix. Library imports or plays local and downloaded tracks. Online accepts a direct HTTPS audio URL and optional title; choose Stream or Download. Downloads have a 250 MB limit. App-private downloads are removed on uninstall.

There is no bundled commercial music catalog, account backend, DRM bypass or Spotify/YouTube/JioSaavn integration. Service web-page links are not audio URLs. Use sources you have permission to stream/download. The waveform is a playback-driven decorative animation, not a measured spectrum. Live streams may not support seeking.

## Device validation

Build and lint do not verify physical-device behavior. On Android 13+ verify notification permission; import an audio file with embedded art; play/pause and seek in app and media notification; disconnect headphones; switch to another audio app to check focus; stream a permitted direct HTTPS audio link; download and play it with networking disabled; test all themes and normal/dot modes. Enable lock-screen mode, leave Pulse Media Player open, lock the device, and verify the full-screen player where supported and device authentication when leaving it. Test TalkBack separately: the custom canvas currently exposes a summary rather than individually focusable controls.
