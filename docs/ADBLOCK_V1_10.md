# Pulse v1.10 filtering implementation

This release replaces the small network matcher as the primary filter with the actual published **adblock-rust 0.13.5** engine used by Brave. This is an engine integration into Android System WebView, not an embedded Brave browser or a claim of complete Brave equivalence.

## Implemented

- Native rule matching through JNI for intercepted subresource and service-worker requests. Network exceptions are respected; an allowed native result is not overridden by the legacy host matcher. The legacy matcher is used when native initialization is unavailable.
- Asset-pinned EasyList and selected uBlock Origin lists, including mobile, quick fixes and unbreak rules, preprocessed for the supported mobile Chromium capabilities.
- 201 upstream scriptlet/helper/replacement resources. Host-specific page rules are compiled with the same native engine and injected at document start when supported. The page bundle covers youtube.com, www.youtube.com, m.youtube.com and music.youtube.com.
- Local data-URI replacement bodies for matching redirect rules. They are bounded and never fetched from an arbitrary URL.
- Existing targeted Pulse player-response cleanup and cosmetic/skip controls retained after the upstream page script. The skip counter now counts actual available-button clicks.
- APK-pinned scripts/lists, processor-specific native libraries, no native JavaScript bridge, no new permissions, existing HTTPS verification and external-navigation restrictions. No cookies, tokens or URLs are persisted by the filtering layer.
- Native initialization occurs off the UI thread. Interception waits up to eight seconds for initialization on the WebView request thread; failure falls back to the existing host rules and is reported in diagnostics. Initialization measured approximately 114–140 ms on this build host, not on a phone.
- Browser menu diagnostics identify the engine state and page-rule snapshot; open-source notices and license texts are available from the menu.

## Limits that remain

WebView does not expose Chromium's complete request/renderer context. Resource type and initiator are inferred from available fetch headers, referrer, top-level page and URL when exact values are unavailable. Main-frame navigation is not ad-blocked. The package does not implement Brave's CNAME handling, CSP changes, arbitrary response rewriting, generic live class/id selector discovery, or a complete procedural cosmetic interpreter. The selected host resources currently resolve zero procedural actions; ordinary selectors and scriptlets are applied.

The current sources are versioned inside the signed APK. They do **not** update themselves from upstream in the background. Script/list refreshes require a new build. New YouTube responses, stale rules, unsupported script syntax or late injection on an older WebView can still leave ads or cause playback failure. The filtering switch and reload remain available for recovery.

No live signed-in/signed-out YouTube ad test or physical-phone playback test was performed. The synthetic tests establish integration behavior only. This build cannot truthfully be labeled verified 100% ad-free, and it does not grant Premium entitlements or protected downloads.

## Verification

Native Rust tests cover blocking, exceptions, first/third-party context and host-specific cosmetics. A host JVM smoke test loads the actual JNI library, compiles the bundled lists/resources, checks a known ad request and preserves normal media, player, thumbnail and login fixtures. Chromium tests execute the exact packaged page script independently and combined with Pulse's fallback for mobile, desktop and Music hosts, with all network requests fulfilled locally. They check bootstrap ad-field removal, player fetch/XHR cleanup, preserved streaming/caption data and preserved unavailable-video status. Android regression tests cover the wrapper's request-type inference and bounded replacements alongside the existing player/browser suite.

Native libraries have 16 KB load-segment alignment. APK signature, zip alignment and published-file hash checks accompany delivery. These checks do not establish live ad-blocking effectiveness or a complete security audit.

See [source archives, licenses and reproduction instructions](../third_party/README.md).

Delivery checks: 42 Android JVM/Robolectric tests passed with eight existing lint warnings; two native Rust tests and the host JNI smoke checks passed; six Chromium host/mode fixtures passed, including actual CSS hiding. APK asset hashes match the manifest, all three packaged native libraries match the build outputs, signature verification succeeds and zipalign verifies 16 KB native-library alignment.
