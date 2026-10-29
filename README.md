# Pulse Media Player for Android

Pure-black Android 8+ audio and video player. Version 1.8 adds previous/next tracks, automatic queue advancement and matching media-session controls. It retains the shared combo colors, clean matrix clock and measured native audio spectrum. Dot-matrix mode and in-app YouTube browsing remain available.

## Download

[Download Pulse Media Player v1.8 APK](https://github.com/sduos4029-CODE/Audio_video-player-/raw/refs/heads/main/downloads/Pulse-Media-Player-v1.8.apk)

This is a debug-signed preview build. Compilation, Android lint, JVM regression tests and Android-simulated rendering checks are available; physical-device codecs, live online playback and lock-screen behavior still require device verification.

## Using the player

- **Library** is a full searchable screen with Audio/Video/Downloads filters, multiple-file import, artwork, artist/duration metadata and track menus. Selecting a track opens the native player; imported URI permissions persist where the document provider supports it. Removing an item removes its library entry without deleting the original file.
- **Online** accepts a YouTube watch/Shorts/live/share link, or a direct HTTPS media URL. Website pages from other services are not media URLs. HTTP errors and decoder failures can be inspected by tapping the error line at the bottom.
- Direct streams use AndroidX Media3, including progressive files, HLS and DASH. Typical containers include MP3, WAV, MP4, WebM, Matroska and Ogg; actual codec and DRM support depend on the device. No player can promise all formats.
- Direct/local sources start in **audio-only** mode. Tap their artwork or use **Menu → Watch current video**, then choose **Audio only** to return. Leaving the video screen, including locking the phone, detaches video and keeps service audio playing.
- **Download** saves permitted direct files (maximum 250 MB) into the offline library. Adaptive playlists, DRM sources and YouTube downloads are not supported. App-private downloads are deleted on uninstall.
- **Menu** controls white/red/yellow/blue/purple/combination themes, normal/dot-matrix rendering, filled/outline buttons and opt-in lock-screen mode. Dot mode uses an explicit 5×7 font and coverage sampling for complete outlines. Embedded artwork or a local video frame replaces the default moon when available, and is rendered entirely as dots in matrix mode.
- Playback includes previous/next tracks, ±10 seconds, seeking, device volume, audio focus, headphone-disconnect pause, media notification and system lock-screen controls. Select a track from the library to create a queue in its visible title order, including the current search/category filter. Previous/next use this stable snapshot; reaching the first/last track disables that direction. Finished tracks advance automatically; the last track stops without repeating. A separately opened direct URL has a one-item queue. Notification and media-session skip controls appear when that direction is available; Android controls their lock-screen presentation.
- Optional player over the lock screen works while the activity is open, where Android allows it. It neither replaces secure authentication nor forces itself open from the background.

## YouTube

**Online → Browse YouTube / Browse YouTube Music** opens the live website inside Pulse. Paste a video link to open its web watch page inside the same Activity. Search, thumbnails and playback use the website's controls. Links to installed apps and external sites are blocked; Pulse launches no external player/browser. Back, reload and website fullscreen video are supported. Update Android System WebView if a site fails to load. Website availability, login restrictions and playback compatibility vary by device; no live device playback is verified by unit tests.

This is an in-app browser, not a separate YouTube backend or native catalog player. YouTube pages retain their own UI; they are not converted into Pulse's dot-matrix player. The web player is retained by a foreground service with standard media-session controls (play/pause, seek, ±10 seconds, stop). **Options → Background audio (experimental)** is enabled by default and avoids Pulse forcing a pause on screen lock or when leaving the browser. The WebView/website or Android can still suspend playback; physical-device screen-off continuity is not verified. Disabling this option restores pause-on-leave. Stop web audio from Options or its notification to release the retained browser. This does not extract a separate YouTube audio stream, provide offline YouTube downloads, or unlock Premium entitlements. Pulse's own themed native audio/video player and background controls still apply to local files and direct media streams.

## Browser controls and filtering

YouTube fills the available safe page area by default, without a Pulse header. A small floating **⋮** button gives access to reload, return to the native player, stop audio, background audio, filtering and **Show Pulse toolbar**. The optional toolbar retains 48dp targets and system-bar/cutout insets. Website video fullscreen hides Android's system bars until a swipe or exit; ordinary page browsing keeps status/navigation areas safe.

**Ad and tracker filtering** is enabled by default. Version 1.5 adds document-start JavaScript where the installed WebView supports it (page-start/page-finish fallback otherwise), targeted ad-metadata removal from player bootstrap/fetch/XHR responses, persistent cosmetic filtering across page changes, available skip-button clicking, and known-host/path filtering for ordinary and service-worker requests. It preserves request bodies/credentials, playback data, non-player APIs and unavailable-video status in regression fixtures. Turning filtering off removes document-start injection and reloads the page.

This is not Brave's adblock engine. Synthetic tests do not establish live YouTube ad suppression, and same-stream ads, unsupported WebViews, site changes or anti-filtering responses can still prevent an ad-free experience. Update Android System WebView. Disable filtering and reload if playback breaks. No claim of all ads blocked, Premium access or live phone playback verification is made.

[Brave source review](docs/BRAVE_ADBLOCK_SOURCE_REVIEW.md) traces request matching, filter updates, renderer scripts and Android YouTube background handling at a pinned public source revision. This targeted review does not establish complete browser coverage or verified live ad suppression. No Brave code or uploaded YouTube Music implementation was copied into Pulse.

## Build and test

Open in Android Studio with JDK 17/21 and Android SDK 35, or run:

```sh
./gradlew :app:assembleDebug :app:lintDebug :app:testDebugUnitTest
node tools/test-web-filter.cjs
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Unit tests cover preserving thin strokes between dot samples, edge cells, YouTube / Music link validation, navigation-policy and simulated in-app routing and Android-simulated drawing of complete controls/time labels. Robolectric rendering screenshots are written to `app/build/player-screenshots/`. Tests also cover Android 15 phone-density system/cutout insets, 48dp targets, retained browser ownership, notification transport actions, background-disable behavior and filtering that preserves media/login endpoints. These do not establish hardware decoding, actual audio continuity, blocked live ads or live YouTube availability.

On a physical device, test local audio and video with embedded artwork, ±10 seconds, pausing during buffering, audio focus and headphones; stream a permitted HTTPS MP4, HLS and DASH source; play downloaded files with networking disabled; paste YouTube watch/share/Shorts links and verify real artwork, in-app navigation, playback and fullscreen with no app handoff; lock the phone in direct-video playback and verify audio continuity; test all themes and both rendering modes. TalkBack currently exposes a canvas summary rather than individually focusable player controls. The native-player spectrum now measures decoded PCM: verify that bass/treble and pauses change it in both modes. WebView audio and compressed passthrough are outside this analyzer.

On a real phone, additionally test the notification and ±10-second controls while locked, background audio with the setting enabled/disabled, headset disconnection, switching back to native playback, and filter-enabled playback across several YouTube videos. Verify the toolbar remains touchable around the phone’s status icons and camera cutout.

For v1.5, additionally check full-page browsing, floating-menu reachability, optional toolbar, video fullscreen enter/exit, ad behavior before and after navigation, and filter off/on reloads on a real phone. The Node fixture suite executes the exact bundled filter script in isolated test contexts; it does not contact YouTube.

## v1.6 privacy and architecture review

[Playback engines, modified APKs and security review](docs/PLAYBACK_AND_SECURITY_REVIEW.md) explains the official Brave/patcher/extractor sources examined, what was implemented and the remaining limitations. Current preview builds do not provide all Premium features or verified ad-free YouTube playback. Use **⋮ → Filter diagnostics** for local counters, and **Clear browsing data** to sign out/clear web data without erasing local music.

## v1.7 player corrections

Normal and matrix players share explicit theme roles: red/blue for the outline and transport button, yellow/purple for progress, volume and spectrum, and white text/filled play glyphs in combo themes. Single-color themes and outline buttons remain selectable. The lock-screen matrix clock uses plain ASCII `h:mm AM/PM` or `HH:mm`, following the phone's 12/24-hour preference and timezone; spacing/format controls are ignored by the dot font.

The waveform is now a 64-band logarithmic frequency spectrum computed from decoded playback PCM with a 1024-point Hann-windowed FFT. Channel power combines without stereo phase cancellation. Analysis runs on a worker at most 25 times per second; the PCM buffer is observed without consuming it. Pausing, silence and stale samples flatten the display. No microphone permission or system-audio recording is used. This applies to local files and direct streams decoded by Pulse's Media3 player, including their audio-only video mode. YouTube's WebView renderer and compressed offload/passthrough do not feed this analyzer. This is not a waveform recording or a calibrated scientific instrument.

Validation: 32 JVM/Robolectric tests pass, covering clock strings, distinct combo roles, filled/outline buttons, frequency peaks, silence/DC, stereo phase and real PCM decoding without changing the playback buffer position. Build and lint complete; lint retains existing warnings. Physical-device playback remains unverified. [Red/yellow/white preview](docs/screenshots/combo-red-yellow-white-v1.7.png) · [Blue/purple/white preview](docs/screenshots/combo-blue-purple-white-v1.7.png). These are simulated idle UI renders, with a flat spectrum.

## v1.8 track navigation and filtering roadmap

Normal mode has separate Previous/Next controls; matrix mode has dotted previous/next icons beside the existing ±10-second controls. Queue navigation also works through the native media session and notification, and finished tracks advance to the next item. This queue covers imported/downloaded library media. YouTube uses its website playlist controls; this release does not extract a native YouTube queue or change the browser filter.

[Ad blocking next steps](docs/AD_BLOCKING_NEXT_STEPS.md) explains the work required: live failure reproduction, a maintained engine, trusted list/script updates, page lifecycle integration and real-device regression testing. It sets measurable goals without promising permanent removal of every ad.

For v1.8, 38 JVM/Robolectric tests pass, including queue boundaries, filtered selection snapshots, separate track/seek actions, automatic advancement, notification PendingIntents and both player layouts. Build and lint complete with the existing lint warnings. [Normal controls preview](docs/screenshots/track-controls-normal-v1.8.png) · [Matrix controls preview](docs/screenshots/track-controls-dots-v1.8.png). These screenshots use a simulated selected track, not physical-device playback.
