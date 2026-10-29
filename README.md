# Pulse Media Player for Android

Pure-black Android 8+ audio and video player. Version 1.3 keeps YouTube browsing and playback inside Pulse through Android System WebView. The installed YouTube / YouTube Music apps are not required.

## Download

[Download Pulse Media Player v1.3 APK](https://github.com/sduos4029-CODE/Audio_video-player-/raw/refs/heads/main/downloads/Pulse-Media-Player-v1.3.apk)

This is a debug-signed preview build. Compilation, Android lint, JVM regression tests and Android-simulated rendering checks are available; physical-device codecs, live online playback and lock-screen behavior still require device verification.

## Using the player

- **Library** imports audio and video through Android's document picker; supported files retain permission across restarts.
- **Online** accepts a YouTube watch/Shorts/live/share link, or a direct HTTPS media URL. Website pages from other services are not media URLs. HTTP errors and decoder failures can be inspected by tapping the error line at the bottom.
- Direct streams use AndroidX Media3, including progressive files, HLS and DASH. Typical containers include MP3, WAV, MP4, WebM, Matroska and Ogg; actual codec and DRM support depend on the device. No player can promise all formats.
- Direct/local sources start in **audio-only** mode. Tap their artwork or use **Menu → Watch current video**, then choose **Audio only** to return. Leaving the video screen, including locking the phone, detaches video and keeps service audio playing.
- **Download** saves permitted direct files (maximum 250 MB) into the offline library. Adaptive playlists, DRM sources and YouTube downloads are not supported. App-private downloads are deleted on uninstall.
- **Menu** controls white/red/yellow/blue/purple/combination themes, normal/dot-matrix rendering, filled/outline buttons and opt-in lock-screen mode. Dot mode uses an explicit 5×7 font and coverage sampling for complete outlines. Embedded artwork or a local video frame replaces the default moon when available, and is rendered entirely as dots in matrix mode.
- Playback includes ±10 seconds, seeking, device volume, audio focus, headphone-disconnect pause, media notification and system lock-screen controls.
- Optional player over the lock screen works while the activity is open, where Android allows it. It neither replaces secure authentication nor forces itself open from the background.

## YouTube

**Online → Browse YouTube / Browse YouTube Music** opens the live website inside Pulse. Paste a video link to open its web watch page inside the same Activity. Search, thumbnails and playback use the website's controls. Links to installed apps and external sites are blocked; Pulse launches no external player/browser. Back, reload and website fullscreen video are supported. Update Android System WebView if a site fails to load. Website availability, login restrictions and playback compatibility vary by device; no live device playback is verified by unit tests.

This is an in-app browser, not a separate YouTube backend or native catalog player. YouTube pages retain their own UI and ads; they are not converted into Pulse's dot-matrix player. This build pauses web audio/video when leaving the screen or locking. It does not provide YouTube audio-only, background playback, offline downloads or Premium entitlements. Pulse's own themed native audio/video player and background controls still apply to local files and direct media streams.

## Build and test

Open in Android Studio with JDK 17/21 and Android SDK 35, or run:

```sh
./gradlew :app:assembleDebug :app:lintDebug :app:testDebugUnitTest
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Unit tests cover preserving thin strokes between dot samples, edge cells, YouTube / Music link validation, navigation-policy and simulated in-app routing and Android-simulated drawing of complete controls/time labels. Robolectric rendering screenshots are written to `app/build/player-screenshots/`. These do not establish hardware decoding or live YouTube availability.

On a physical device, test local audio and video with embedded artwork, ±10 seconds, pausing during buffering, audio focus and headphones; stream a permitted HTTPS MP4, HLS and DASH source; play downloaded files with networking disabled; paste YouTube watch/share/Shorts links and verify real artwork, in-app navigation, playback and fullscreen with no app handoff; lock the phone in direct-video playback and verify audio continuity; test all themes and both rendering modes. TalkBack currently exposes a canvas summary rather than individually focusable player controls. The waveform is decorative rather than a measured spectrum.
