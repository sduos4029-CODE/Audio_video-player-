# Steps toward stronger YouTube filtering

Historical scope: this document describes the earlier release named below. [v1.10 now integrates adblock-rust and pinned upstream page rules](ADBLOCK_V1_10.md); remaining limits are documented there.

Pulse v1.8 adds native-library track navigation. Its YouTube filter is unchanged and remains experimental. The [pinned Brave source review](BRAVE_ADBLOCK_SOURCE_REVIEW.md) explains the architectural gap. This document is an implementation roadmap, not a claim that the work below has been completed.

## Scope and achievable outcome

The useful target is measurable suppression of site-delivered ads with reliable playback, an off switch and continuing updates. No third-party blocker can guarantee permanent removal of every YouTube ad across accounts, regions, devices and future website changes. YouTube Premium is the official route for removing YouTube-served ads where supported; creator sponsorships included in a video are a different category. Blocking ads does not grant paid catalog entitlements, DRM licenses or offline-download rights.

## Work in order

1. **Establish live failures.** On an actual Android phone, record the Pulse/WebView version, whether the account is signed in, the type of ad and whether it appears on an initial load or after navigation. Use local diagnostic counts and sanitized reproduction notes. Never collect cookies, authorization headers or account tokens. Compare filtering on/off and confirm that an apparent fix still plays the intended video. Our current fixtures cannot establish this baseline.
2. **Select and integrate a maintained engine.** Evaluate an Android-compatible rule engine, for example a JNI integration of adblock-rust, including its current license, list licenses, supported syntax, build reproducibility and maintenance. Feed URL, initiator, resource type and applicable request context to matching, including allow/exception and redirect handling. WebView supplies less network context/control than Brave's Chromium integration; enumerate these gaps before claiming parity. Blocking broad Google or googlevideo domains will also block normal media.
3. **Ship trusted rules and page resources.** Use versioned filter lists, domain-specific cosmetic rules and compatible scriptlet/redirect resources. Implement bounded downloads, authenticated update artifacts, expiration/last-known-good fallback and rollback. Treat script resources as executable input. A general matcher without relevant current lists and resources will not reproduce Brave's behavior.
4. **Integrate page timing and lifecycle.** Apply supported document-start resources only to intended origins, handle SPA navigation and frames, and clean up when disabled. Preserve playability errors, media data, consent and login flows. Avoid hiding security dialogs. Keep background audio/media-session handling separate from filtering so regressions can be isolated.
5. **Validate and maintain it.** Run reproducible tests on real phones across supported Android/WebView versions, signed-in and signed-out playback, several regions where available, initial/mid-roll ads, playlists, Music, Shorts, fullscreen and screen lock. Check seek, pause, captions, login, live streams, battery use and filter-off recovery. Maintain regression fixtures from sanitized failures and roll back broken rules promptly. A successful test sample still does not prove every ad is blocked.

If WebView's missing hooks remain the limiting factor, evaluate a browser engine that exposes the required network/renderer controls. Maintaining a Chromium-based browser is a separate large project: build size, native integration, ongoing browser security patches, licensing and distribution all need ownership. It is not supplied by wrapping an installed Brave or YouTube APK. Pulse has not embedded that engine.

## Current delivery boundary

The app now has previous/next controls for a snapshot of the selected local library list, including search/category filtering. Single direct URLs form a one-item queue. YouTube continues to use its own website playlist controls inside the browser; Pulse does not fabricate a native queue from those pages. Ad blocking milestones above remain future implementation work, and live phone ad suppression is unverified.
