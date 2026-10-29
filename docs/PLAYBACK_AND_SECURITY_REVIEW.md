# Playback engines, modified APKs and Pulse v1.6

## Evidence and scope

This review used official project documentation and the earlier static inspection of the user-supplied YouTube Music files. No third-party mod APK was downloaded, installed or executed. No Google account password/token was requested. Pulse is not a repacked Google APK and no uploaded Google implementation was copied into it.

Sources consulted:

- [Brave adblock-rust](https://github.com/brave/adblock-rust): network filtering, cosmetic filtering, resource replacement and filter syntax support.
- [ReVanced Patcher](https://github.com/ReVanced/revanced-patcher): changes Dalvik bytecode, APK resources and arbitrary APK files.
- [ReVanced CLI](https://github.com/ReVanced/revanced-cli): applies patch bundles and lists package/version compatibility.
- [NewPipeExtractor](https://github.com/TeamNewPipe/NewPipeExtractor): independent streaming-site extraction library, GPLv3-or-later; Android integration/desugaring requirements are documented by the project.

The current `ReVanced/revanced-patches` repository returned HTTP 451 in this environment. Its current YouTube/Music patch implementation and supported-version list were not inspected or verified. This report does not attribute every advertised “Premium feature” to a verified working patch.

## Why these approaches differ

| Approach | What it does | What Pulse can claim now |
|---|---|---|
| Brave | Browser-integrated adblock engine, request/cosmetic/resource filtering | Pulse currently uses WebView hooks and a small custom filter. It does not include Brave's complete engine or guarantee Brave-equivalent results. |
| Version-specific APK patching | Changes the original application's bytecode/resources, then packages a modified build | This is a different architecture from Pulse. Patching generally requires compatible original app versions and re-signing the modified artifact. A source-stamp fingerprint is not Google's private signing key. |
| Independent extractor + native player | Parses site responses to obtain metadata/playable streams for its own player | A credible native-player route, but it requires dependency/license integration, ongoing site compatibility maintenance and live playback tests. It is not included in v1.6. |
| Account/server subscription | Determines entitlements, licenses and server-controlled access | Changing a client does not establish a server-issued entitlement. The inspection does not prove unrestricted access to every Premium feature, DRM stream or offline license. |

Client-side behavior such as UI, some ads and background restrictions can differ in modified clients. That does not substantiate a claim that every subscription-controlled capability works. Native stream extraction, account access, DRM licensing and ordinary browser filtering should not be treated as interchangeable.

## Implemented in v1.6

- Corrected the app's duplicate system-inset propagation: the safe area is applied once, then consumed before child views/WebView receive it. Tests check the web viewport fills the screen minus actual system/cutout areas. Website-generated padding and device-specific rendering still need phone verification.
- Replaced the offline/local library popup with a searchable screen, Audio/Video/Downloads filters, multiple-file import, background metadata/artwork loading, track selection and removal from the library without deleting originals.
- Replaced normal-mode canvas controls with native Android views: actual artwork, labeled volume, real seek control, times and accessible play/pause/±10-second buttons. Dot-matrix mode remains available.
- Added bounded artwork decoding, bounded thumbnail caching and guarded metadata reads. Native metadata reads do not block the list UI.
- Added local filter diagnostics: request counts, player-response inspection/removal counts, available skip-control clicks and the page's ad-state signal. These are untrusted page/runtime observations, not proof of an ad-free experience. Diagnostics do not include URLs, titles, cookies or tokens and are not uploaded by Pulse.
- Added a user-confirmed **Clear browsing data** action, which clears the app's web cookies/cache/storage while retaining its music library.
- Rejects credential-bearing direct media URLs. Existing HTTPS-only navigation, disabled file/content access in WebView, mixed-content rejection and no permissive SSL-error override remain in place. No native JavaScript bridge is exposed.

This release does **not** claim to solve all YouTube ads or unlock Premium. The existing browser filter remains experimental. Actual ad delivery, login behavior, codecs and screen-off continuity have not been verified on a physical device.

## Security implications

A modified APK runs with its own signing identity and permission surface. Its advertised name or familiar UI does not establish that it matches published source/patches. Unverified builds can contain unrelated behavior, and signing/account compatibility may differ from the original app. Open-source patch tools do not authenticate every prebuilt APK bearing their name.

Pulse's source and versioned APK are published together. APK signature and downloaded-file hash checks confirm consistency with the generated build; they are not a Google signature or an independent security audit. These are still debug-signed preview builds, retaining the existing signing identity so installed previews can update without a forced uninstall. Production release signing, dependency review, physical-device verification and an independent security review remain separate work.

NewPipeExtractor uses GPLv3-or-later. Incorporating it would require a compatible source-distribution/licensing plan and a tested Android downloader/extractor integration. It cannot be silently substituted for the current engine or assumed to provide account Premium entitlements.

## Verification and practical limits

Tests cover legacy library migration, duplicate imports, search/categories, track selection, normal/matrix mode switching, native touch targets, Android 15 safe-area consumption/full web height, local diagnostics privacy, clearing web data without losing the library, browser retention and standard notification actions. The bundled filter has separate Node fixtures. These are not live ad-blocking, live catalog, account-entitlement or hardware playback tests.

The resource/DEX/native-file inspection is targeted static analysis, not “analysis of everything”: backend code, a signed original installation, runtime account state and live service behavior were not supplied or verified.
