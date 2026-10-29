# Pulse Media Player for Android

Pure-black Android 8+ audio and video player. Version 1.18 reduces the style picker to a single short row, with an image preview that expands only when requested or when choosing another style. The v1.10 native filtering engine and bundled YouTube page rules remain included.

## Download

[Download Pulse Media Player v1.18 APK](https://github.com/sduos4029-CODE/Audio_video-player-/raw/refs/heads/main/downloads/Pulse-Media-Player-v1.18.apk)

This is a debug-signed preview build. Compilation, Android lint, JVM regression tests and Android-simulated rendering checks are available; physical-device codecs, live online playback and lock-screen behavior still require device verification.

## Using the player

- **Library** is a full searchable screen with Audio/Video/Downloads filters, multiple-file import, artwork, artist/duration metadata and track menus. Selecting a track opens the native player; imported URI permissions persist where the document provider supports it. Removing an item removes its library entry without deleting the original file.
- **Online** accepts a YouTube watch/Shorts/live/share link, or a direct HTTPS media URL. Website pages from other services are not media URLs. HTTP errors and decoder failures can be inspected by tapping the error line at the bottom.
- Direct streams use AndroidX Media3, including progressive files, HLS and DASH. Typical containers include MP3, WAV, MP4, WebM, Matroska and Ogg; actual codec and DRM support depend on the device. No player can promise all formats.
- **Audio** is the default native mode. The home screen has **Audio / Video** controls and remembers your choice for available video tracks. Video plays inline across the home player’s width; swipe right on its thumbnail or use the fullscreen icon for the dedicated video screen. Tap the video to reveal bottom previous/next, ±10-second and play/pause controls; they hide during playback. The top controls switch audio/video, rotate and exit fullscreen. Tap the bottom-right aspect-ratio icon to cycle Fit screen → Crop → Stretch; hold it for the compact zoom menu. Fit preserves the whole frame, Crop trims the edges to fill the available area, and Stretch fills it by changing proportions. Pinch to zoom (50–400%), drag the enlarged picture, or use the menu’s −/+ and Reset zoom buttons. Choosing a sizing mode resets zoom and pan. Sizing and zoom are remembered across home/fullscreen and rotation. Locking the phone/backgrounding the player detaches video while service audio continues. The separate in-player touch lock blocks accidental controls and is released by holding its unlock button. Device verification is still needed.
- **Download** saves permitted direct files (maximum 250 MB) into the offline library. Adaptive playlists, DRM sources and YouTube downloads are not supported. App-private downloads are deleted on uninstall.
- **Menu** controls white/red/yellow/blue/purple/combination themes, normal/dot-matrix rendering, filled/outline buttons and opt-in lock-screen mode. Dot mode uses an explicit 5×7 font and coverage sampling for complete outlines. Embedded artwork or a local video frame replaces the default moon when available, and is rendered entirely as dots in matrix mode.
- Playback includes previous/next tracks, ±10 seconds, seeking, device volume, audio focus, headphone-disconnect pause, media notification and system lock-screen controls. Select a track from the library to create a queue in its visible title order, including the current search/category filter. Previous/next use this stable snapshot; reaching the first/last track disables that direction. Finished tracks advance automatically; the last track stops without repeating. A separately opened direct URL has a one-item queue. Notification and media-session skip controls appear when that direction is available; Android controls their lock-screen presentation.
- Optional player over the lock screen works while the activity is open, where Android allows it. It neither replaces secure authentication nor forces itself open from the background.

## YouTube

**Online → Browse YouTube / Browse YouTube Music** opens the live website inside Pulse. Paste a video link to open its web watch page inside the same Activity. Search, thumbnails and playback use the website's controls. Links to installed apps and external sites are blocked; Pulse launches no external player/browser. Back, reload and website fullscreen video are supported. Update Android System WebView if a site fails to load. Website availability, login restrictions and playback compatibility vary by device; no live device playback is verified by unit tests.

This is an in-app browser, not a separate YouTube backend or native catalog player. YouTube pages retain their own UI; they are not converted into Pulse's dot-matrix player. The web player is retained by a foreground service with standard media-session controls (play/pause, seek, ±10 seconds, stop). **Options → Background audio (experimental)** is enabled by default and avoids Pulse forcing a pause on screen lock or when leaving the browser. The WebView/website or Android can still suspend playback; physical-device screen-off continuity is not verified. Disabling this option restores pause-on-leave. Stop web audio from Options or its notification to release the retained browser. This does not extract a separate YouTube audio stream, provide offline YouTube downloads, or unlock Premium entitlements. Pulse's own themed native audio/video player and background controls still apply to local files and direct media streams.

## Browser controls and filtering

YouTube fills the available safe page area by default, without a Pulse header. A small floating **⋮** button gives access to reload, return to the native player, stop audio, background audio, filtering and **Show Pulse toolbar**. The optional toolbar retains 48dp targets and system-bar/cutout insets. Website video fullscreen hides Android's system bars until a swipe or exit; ordinary page browsing keeps status/navigation areas safe.

**Ad and tracker filtering** is enabled by default. Version 1.10 uses the pinned **adblock-rust 0.13.5** engine for intercepted subresource/service-worker requests, including matching exceptions and bounded local resource replacements. Bundled EasyList and selected uBlock Origin lists resolve domain-specific cosmetic rules and scriptlets for mobile, desktop and Music pages. The existing Pulse response cleanup/skip control remains as an additional layer. Document-start injection requires WebView support; older WebViews use a later fallback. Turning filtering off reloads the page without the scripts.

The native engine is used by Brave, but Pulse still uses Android System WebView rather than Brave's complete Chromium integration. Some request context is inferred. The rules/scripts are pinned inside the APK and do not self-update; refreshing the snapshot requires a new build. Live ads, account behavior and physical-phone playback have not been verified. No 100% ad-free or Premium claim is made. Disable filtering and reload if playback breaks.

Use **⋮ → Filter diagnostics** to see whether the native engine loaded, early-injection support and local counters. **Open-source licenses** exposes the component notices and full license texts. [Implementation and validation boundaries](docs/ADBLOCK_V1_10.md) · [Source archives and reproducible native build](third_party/README.md). No uploaded Google APK implementation is included.

## Build and test

Open in Android Studio with JDK 17/21 and Android SDK 35, or run:

```sh
./gradlew :app:assembleDebug :app:lintDebug :app:testDebugUnitTest
node tools/test-web-filter.cjs
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Unit tests cover preserving thin strokes between dot samples, edge cells, YouTube / Music link validation, navigation-policy and simulated in-app routing and Android-simulated drawing of complete controls/time labels. Robolectric rendering screenshots are written to `app/build/player-screenshots/`. Tests also cover Android 15 phone-density system/cutout insets, 48dp targets, retained browser ownership, notification transport actions, background-disable behavior and filtering that preserves media/login endpoints. These do not establish hardware decoding, actual audio continuity, blocked live ads or live YouTube availability.

On a physical device, test local audio and video with embedded artwork, ±10 seconds, pausing during buffering, audio focus and headphones; stream a permitted HTTPS MP4, HLS and DASH source; play downloaded files with networking disabled; paste YouTube watch/share/Shorts links and verify real artwork, in-app navigation, playback and fullscreen with no app handoff; lock the phone in direct-video playback and verify audio continuity; test all themes and both rendering modes. TalkBack currently exposes a canvas summary rather than individually focusable player controls. The native-player visualizations observe decoded PCM: verify that the selected line/dot style responds to audio and flattens on pause. WebView audio and compressed passthrough are outside this analyzer.

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

## v1.9 selected waveform styles

Normal mode uses one continuous curved line through a short 64-point trace of decoded PCM, averaged into display buckets from the loudest channel to avoid opposite-phase cancellation. Matrix mode uses a mirrored frequency envelope rendered directly as equally spaced round dots. The colors follow the selected theme. Paused or stale audio produces a flat display; there is no synthetic moving waveform. The normal trace shows sample polarity/amplitude while the dots show logarithmic FFT band energy, so their shapes deliberately differ. Both use the existing decoded local/direct-stream audio path without a microphone permission. WebView YouTube audio remains outside that path.

The previews use a known 440 Hz test signal through the production rendering helpers: [Smooth line](docs/screenshots/waveform-smooth-v1.9.png) · [Dot wave](docs/screenshots/waveform-dots-v1.9.png). These are test renders, not screenshots of physical-device playback.

Validation for v1.9: 40 JVM/Robolectric tests pass, including PCM trace polarity, anti-phase stereo, paused output, the curved line and spaced dot rendering. Build and lint complete with the existing warnings. Live-device playback remains unverified.

## v1.10 filtering validation

The release includes native engine tests, an actual JNI/JVM smoke check with the bundled rules, browser fixtures for all three YouTube interfaces, and the Android regression suite. Browser fixtures fulfill every request locally; they do not measure live YouTube ad suppression. Android processor libraries are provided for arm64-v8a, armeabi-v7a and x86_64, with 16 KB ELF segment alignment. The 122,872-line rule source includes comments/directives and is not a parsed active-rule count. There are 201 bundled scriptlet/helper/replacement resources. See the linked implementation report for limitations.

## v1.11 player interface

The three-dot menu now opens a compact dark bottom sheet with Normal/Dot matrix segments, color swatches, two combination previews, filled/outline play and lock-screen switches. Theme choices remain in the sheet and apply immediately. Normal mode uses mirrored cashew-shaped previous/next buttons around the playback controls.

The normal home player has a full-width media stage. Selecting Video expands that stage and uses the shared overlay player; Audio restores the cover, measured smooth line and audio controls. A local video frame or embedded artwork is used when available, with a fallback icon otherwise. Matrix mode retains dotted artwork and has dotted audio/video icons; its video action opens the dedicated player.

Home and fullscreen video share the existing service player and position. Surface ownership prevents a closing old screen from clearing the new screen’s output. The current surface is reused when queue navigation creates a replacement decoder. Fullscreen rotation handles configuration changes in place. Screen-off/background transitions disable the video track without issuing an audio pause.

These changes concern Pulse’s native local/direct-stream player; embedded YouTube pages retain their own interface. The separate native YouTube Music lab is not part of this APK.

Previews: [Settings](docs/screenshots/settings-v1.11.png) · [Audio and cashew controls](docs/screenshots/native-audio-v1.11.png) · [Home video — Fit](docs/screenshots/home-video-v1.11.png) · [Home video — Fill](docs/screenshots/home-video-fill-v1.11.png) · [Portrait video](docs/screenshots/video-portrait-v1.11.png) · [Landscape video](docs/screenshots/video-landscape-v1.11.png). These are Android-simulated UI renders using a synthetic poster and playback state, not live decoded video frames.

The Android regression suite covers compact settings persistence, live mode changes, full-width layout, cashew controls, tap-to-show/auto-hide, rotation requests, seek/track controls, Fit/Fill sizing and stale-surface ownership. Surface validity and playback state are stubbed in the relevant tests; physical-device video decoding, rotation, audio continuity and screen-lock behavior remain unverified.

Validation for v1.11: 48 JVM/Robolectric tests pass; build and lint complete with six existing warnings. APK signature, 16 KB zip alignment, pinned filter asset hashes and packaged native libraries were checked.


## v1.12 video sizing and zoom

Tap the video, then the resize icon for a compact **Video size** sheet. Choose **Fit**, **Crop** or **Stretch**, adjust zoom with **− / +** (50–400%), or **Reset zoom**. Pinch anywhere on the video to scale around your fingers. Drag an enlarged picture to reposition it; panning is bounded so it cannot reveal empty edges beyond those inherent in the chosen fit/zoom. Zooming out below 100% deliberately leaves black space. Playback controls remain fixed and touchable. Selecting a mode restores 100% zoom and centers the picture. The previous Fill preference migrates to Crop.

The same geometry transforms the TextureView picture and its poster, without resizing the decoder surface on every gesture. Sizing, zoom and the relative picture offset persist across home/fullscreen, rotation and reopening the app. Audio artwork remains unaffected. These controls apply to Pulse’s native local/direct-stream video; YouTube’s embedded website retains its own video controls.

Previews: [Video size menu](docs/screenshots/video-sizing-v1.12.png) · [Crop at 125%](docs/screenshots/video-zoom-v1.12.png). These are Android-simulated UI renders with sample artwork, not physical-device playback.

All 53 JVM/Robolectric regression tests pass, including portrait/landscape sizing, zoom/pan bounds, menu actions, unchanged playback position/control sizes, legacy Fill preference, audio artwork, pinch touch routing and persistence. APK signature and 16 KB zip alignment are checked; Android lint completes with the six existing warnings. Real-phone multitouch, hardware video composition and codec behavior still need device verification.


## v1.13 compact video controls and gestures

The Video size panel is capped at 300 dp wide and uses three short rows: title/Reset/Close, Fit/Crop/Stretch, and −/percentage/+. All actions retain 48 dp touch targets. The video transport buttons now sit above the seek bar at the bottom in both inline and fullscreen video, leaving the center clear.

On the picture, swipe vertically in the right 40% to adjust media volume or in the left 40% to adjust player-window brightness. A small percentage overlay confirms the adjustment. Volume uses Android’s media stream and respects its available range and restrictions. Brightness changes only this Activity’s window and restores its previous value on exit, backgrounding, or switching to audio; it needs no system-settings permission. Pinch zoom remains available; central dragging or horizontal dragging pans the enlarged picture. Touching actual buttons and the seek bar retains their normal behavior.

Double-tap the right half of the picture to seek forward 10 seconds, or the left half to rewind 10 seconds. Single taps toggle the overlay after the double-tap recognition interval. Pinching cancels pending taps, and quick-scale is disabled so double taps do not also zoom. These controls concern Pulse’s native local/direct-stream video; embedded YouTube keeps its website controls.

All 57 JVM/Robolectric tests pass. Build and lint complete with the six existing warnings. Tests cover swipe direction and stream isolation, brightness limits/restoration, double-tap seek offsets, single-tap overlay behavior, zoom/pan interactions, compact sizing-panel bounds and bottom-control placement in both orientations. Previews use simulated playback and a sample poster: [Compact size panel](docs/screenshots/video-size-compact-v1.13.png) · [Portrait bottom controls](docs/screenshots/video-bottom-v1.13.png) · [Landscape bottom controls](docs/screenshots/video-bottom-landscape-v1.13.png). Real-phone gesture sensitivity, hardware rendering, display brightness and audio-device behavior remain to be verified.


## v1.14 sizing shortcut and rotation handling

The aspect-ratio button now sits at the bottom right beside the transport controls, below the seek bar and time labels. It opens the compact **Fit screen / Crop / Stretch** menu and changes its icon with the selected mode. The layout reserves space for the shortcut on portrait phones and keeps transport controls centered in landscape. Zoom and gestures continue to work.

The native player now attaches output through Media3’s `setVideoTextureView` / `clearVideoTextureView`, allowing Media3 to handle TextureView resize, surface destruction/recreation and surface dimensions. Pulse no longer installs its own competing SurfaceTexture listener or manually allocates/releases the output Surface. View ownership still rejects stale releases, and queue changes attach the current view to the replacement decoder. The service’s view reference is weak and is cleared when output detaches.

Buffering completion no longer issues another Play call or requests audio focus again: Media3 preserves the existing play intent. Orientation changes retain the same player and view without an artificial Pause/Play workaround. Backgrounding and Audio mode still detach video and allow background audio.

All 60 JVM/Robolectric tests pass; build and lint complete with the six existing warnings. The regression suite covers player/view continuity and unchanged playback position/play intent across portrait-to-landscape rotation, buffering-to-ready transitions without another Play call, queue replacement, stale output release, and bottom-right menu placement/mode icons. The player interface is recorded in rotation tests; they do not reproduce a physical phone’s decoder stall or prove frame pacing. Please verify repeated rotation during playback and while paused on the affected phone.

Previews: [Landscape controls](docs/screenshots/video-sizing-corner-landscape-v1.14.png) · [Portrait controls](docs/screenshots/video-sizing-corner-portrait-v1.14.png). These use a sample poster and simulated playback. These changes apply to native local/direct-stream video, not the embedded YouTube website.


## v1.15 approved controls, touch lock and GPU sketch

- The bottom-right sizing button cycles **Fit screen → Crop → Stretch** in one tap, updates its icon and briefly names the mode. Holding it opens zoom options. The bottom bar has fixed edge actions (lock and sizing), centered evenly spaced rewind/previous/play/next/forward, and a truly circular play background. Narrow phones use a second compact action row to preserve 48 dp targets.
- **Touch lock** hides transport controls and blocks picture gestures, seeking and accidental taps. Hold the visible unlock button to restore controls; Back first unlocks the view. It survives activity recreation but does not replace Android’s security lock or block system media controls.
- The pencil icon switches **Sketch video** on/off. The actual decoded frames run through the bundled Media3 1.5.1 effect pipeline and Pulse’s GLES shader: luminance edges, local detail, static paper texture and crosshatching. The interface and audio remain unchanged. The effect is initialized before prepare and its enabled flag is read per frame, avoiding player reconstruction for ordinary toggles. A paused toggle seeks to the same position to request a refreshed frame without issuing Play. This shader is a stylized pencil treatment, not a promise of frame-for-frame equivalence to the generated concept art.
- Foreground Audio/Video switching keeps the same player, output and selected video decoder while covering the picture in Audio mode, preserving position, play intent and the ready frame. This increases foreground power use. Backgrounding/locking the phone still clears video output and disables video decoding. Source/network/decoder delays can still occur; zero buffering is not guaranteed.
- Decoder fallback is enabled for alternative installed decoders. Tapping the title opens source resolution/frame-rate/codec, audio format and advertised display HDR/decoder details. Errors distinguish unsupported profiles, device limits, decoder initialization, DRM and HTTP failures. No additional software codec pack or Dolby entitlement is bundled. 2K/4K/UHD/8K, HDR and Dolby support remain conditional on the media and phone/output hardware. Sketch alters color grading and adds GPU cost; its HDR treatment is not HDR-faithful. Protected DRM video is outside the sketch pipeline’s support.
- If the GPU video frame processor fails, Pulse makes one fallback to the normal renderer, preserving position and paused/playing intent, and disables sketch for that service session. The explanation is available in Playback details.

Validation: **68 JVM/Robolectric tests pass**, covering cycling/icons, lock gesture suppression and restoration, responsive non-overlapping control targets, retained foreground decoder/output, background release, per-frame effect setup/toggling, paused refresh, GPU failure fallback and capability messaging. Build and lint complete with the six existing warnings. Playback interfaces and state are recorded/stubbed in these tests.

`python3 tools/test-sketch-gpu.py` compiles and renders the exact bundled vertex/fragment shaders in a surfaceless GLES context. On llvmpipe it verified exact pass-through when off, all 32,768 test pixels transformed when on, deterministic output for unchanged input, and restoration when toggled off. It is a software-GL shader check, not Android codec integration, frame-rate, battery or phone HDR validation. Physical-device testing remains necessary, particularly at high resolution and during mode changes.

Previews: [Landscape controls](docs/screenshots/video-controls-v1.15.png) · [Touch lock](docs/screenshots/video-locked-v1.15.png) · [Portrait controls](docs/screenshots/video-portrait-v1.15.png). These are simulated app renders with a sample poster, not live phone playback. [Shader test input](docs/screenshots/shader-normal-v1.15.png) · [Actual shader test output](docs/screenshots/shader-sketch-v1.15.png) use a synthetic pattern, not a movie frame. Media3’s [Apache license and notice](app/src/main/assets/PLAYER-THIRD-PARTY.txt) are included in the APK. These changes apply to Pulse’s native local/direct-stream player; embedded YouTube retains its own controls and rendering.


## v1.16 six styles and corrected Fit sizing

Tap the pencil icon to choose **Normal video, Fine Pencil, Charcoal, Ink Hatch, Color Pencil, White Outline or Comic**. The selection applies to the complete decoded video picture and is remembered. Hold the pencil to turn the effect off/on using the previous style. Styles share the existing GPU pipeline and do not recreate the player or seek during active playback; a paused change requests a frame at the same position. The generated comparison board is art direction, not an exact representation of these real-time shader outputs.

Fit previously applied a TextureView shrink after Media3’s GPU output had already fitted/letterboxed the frame. That second fit compressed a portrait screen’s video into a narrow band. The transform now maps the actual picture region inside the GPU output to the desired Fit/Crop/Stretch region. Fit uses the GPU’s existing fit once; Crop uniformly enlarges it; Stretch fills the viewport by changing its proportions. The normal-renderer fallback still maps an unfitted decoder picture. Source dimensions, pixel shape and rotation come from the active format when available rather than relying only on video-size callbacks. Poster artwork retains its separate image geometry.

Fit preserves the entire source, so a wide movie in portrait will still have black space. Crop trims edges to fill the screen, and Stretch changes proportions. Pinch zoom, one-tap size cycling, rotation and touch lock remain available. The same sizing correction applies while sketch is off because the GPU pipeline is still used for normal pass-through. The underlying Media3 stage explicitly uses [`Presentation.LAYOUT_SCALE_TO_FIT`](https://github.com/androidx/media/blob/1.5.1/libraries/effect/src/main/java/androidx/media3/effect/FinalShaderProgramWrapper.java#L580) when drawing to the output surface.

All 76 JVM/Robolectric tests pass. Build and lint complete with the six existing warnings. Validation covers already-fitted 16:9, cinematic, 4:3 and portrait pictures in both screen orientations, source pixel ratio/rotation, the fallback renderer, and a pixel-level circle/height regression using a simulated letterboxed output buffer. Picker tests select all six modes, preserve position/play intent, remember the selected style, restore it after a service restart and keep touch lock effective. Shader tests compile and draw all six modes, verify six distinct outputs, exact normal pass-through, stable repeated frames, monochrome ink/charcoal/outline, color retention in pencil/comic, and black background coverage in outline mode. These checks use simulated Android state and software GLES, not a physical phone’s decoder/compositor or high-resolution performance.

[Style selector preview](docs/screenshots/sketch-picker-v1.16.png) uses an Android-simulated dialog. Exact shader outputs from a synthetic test pattern: [Fine Pencil](docs/screenshots/shader-fine-pencil-v1.16.png) · [Charcoal](docs/screenshots/shader-charcoal-v1.16.png) · [Ink Hatch](docs/screenshots/shader-ink-hatch-v1.16.png) · [Color Pencil](docs/screenshots/shader-color-pencil-v1.16.png) · [White Outline](docs/screenshots/shader-white-outline-v1.16.png) · [Comic](docs/screenshots/shader-comic-v1.16.png).


## v1.17 compact dropdown with frame previews

Tap the pencil icon to open a small anchored panel (maximum 296 dp wide, approximately 312 dp high on a typical phone). Use the dropdown to browse Normal video and all six styles. Each option has a thumbnail, and selecting it updates the larger image. **Apply** changes playback and remembers the choice; **Cancel**, closing the popup or tapping outside leaves the active style unchanged. Holding the pencil still toggles the last applied style on/off.

Previews are captured once from the GPU pipeline’s **unfiltered input texture**, so switching options never stacks a new effect on an already-filtered screenshot. The same production shader renders all seven preview images into a small offscreen target, capped at 256 × 144 with the source aspect ratio preserved. The preview path adds display transfer for Bitmap output and restores the video render target/state before normal frame rendering continues. It does not write the style preference or reprepare the player. A paused, seekable source requests a frame at its current position without issuing Play; unavailable frames time out after three seconds with an explanatory message and an option to apply without a preview. HDR previews are labelled as an SDR approximation.

This is a thumbnail of a captured frame, not a live video window or an exact pixel match to every full-resolution/HDR output. The earlier AI-generated comparison board cannot be reproduced exactly by these real-time effects; this panel shows the actual implemented filter instead. The six filter calculations are unchanged from v1.16.

Pending captures are cancelled on close, timeout, touch lock, backgrounding and source changes. Late results are discarded. Choosing an option only changes the candidate image until Apply. Source changes close the panel to avoid applying against a stale preview.

Validation: **82 JVM/Robolectric tests pass**; build and lint complete with the six existing warnings. Tests cover dropdown image bindings, Apply/Cancel, playback/preference preservation, bounded popup dimensions, capture cancellation/timeout, source/lock cleanup, paused requests, RGBA color/vertical readback and one-time delivery. UI preview fixtures are software-GLES renders of a synthetic test pattern and are excluded from the APK. The GLES smoke check additionally verifies preview/video color-transfer correspondence, all six distinct styles, offscreen framebuffer readback and restoration of normal video output. Physical Android GPU capture latency, driver behavior and high-resolution performance remain unverified.

[Compact picker UI preview](docs/screenshots/compact-style-popup-v1.17.png) is an Android-simulated layout with shader-rendered synthetic test data. In the app, the image area uses a captured video frame. No Google APK implementation or AI generation service is added to playback.


## v1.18 one-row style picker

The pencil opens one short row with a preview thumbnail, style dropdown, Apply checkmark and Close button. The default panel is capped at **264 × 64 dp**, compared with the previous 296 × 312 dp panel. The redundant title and footer rows are removed, while action targets remain at least 48 dp.

Selecting a different style expands a small preview below the row. Tap the thumbnail to expand/collapse it manually. The expanded panel is normally at most **264 × 164 dp**. Dropdown options retain their image thumbnails and full names. Browsing does not apply a style; the checkmark applies it and Close/outside dismissal cancels. Captured-frame rendering and cancellation behavior are unchanged.

All 82 JVM/Robolectric tests pass; build and lint complete with the six existing warnings. The preview test now checks the collapsed 64 dp height, 264 dp width cap, expansion on selection, manual collapse and 48 dp action heights, alongside the existing capture/Apply/Cancel checks. Physical-device testing remains needed.

Previews: [Collapsed row](docs/screenshots/style-row-v1.18.png) · [Expanded image](docs/screenshots/style-preview-expanded-v1.18.png). These are simulated app layouts using a synthetic shader test frame. Actual app previews use the video frame; the AI concept board is not an exact rendering reference.


Full-player UI previews with the compact selector: [Portrait — collapsed](docs/screenshots/full-player-portrait-compact-v1.18.png) · [Portrait — expanded](docs/screenshots/full-player-portrait-preview-v1.18.png) · [Portrait — dropdown](docs/screenshots/full-player-portrait-dropdown-v1.18.png) · [Landscape — collapsed](docs/screenshots/full-player-landscape-compact-v1.18.png) · [Landscape — expanded](docs/screenshots/full-player-landscape-preview-v1.18.png) · [Landscape — dropdown](docs/screenshots/full-player-landscape-dropdown-v1.18.png). These are software-rendered app views with simulated playback and a synthetic sample frame. Popup layers are composited at the pencil anchor for the complete layout; the dropdown is rendered from the native list. These are layout previews, not physical-device screenshots. The APK is unchanged.
