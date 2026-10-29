# YouTube Music Native Lab

Separate native-APK research and patch preparation for the user's supplied YouTube Music 9.43.51 files. This project does not use a website wrapper. It is independent of the Pulse player source/build and does not publish Google app binaries or decompiled source.

**There is no installable modified APK yet.** An experimental native DEX patch exists and passes static checks. Native playback, ad suppression, sign-in, background behavior and Android installation have not been verified. No Premium entitlement or protected download capability is claimed.

## Work completed

- Reconstructed a local **analysis ZIP** from the extracted manifest, resource table, six DEX files, 277 assets, 2,500 resource files and 25 ARM64 libraries. Forty `(1)` filename suffixes were mapped back to paths referenced by the resource table; these mappings are inferred, not authenticated against the original APK. The SDK reports no missing resource-file warnings after this repair. Native ABI paths are restored from the existing hashed inventory.
- Verified original DEX SHA-1/Adler-32 fields and native-library SHA-256 values against the prior inspection inventory.
- Mapped 154 actual `const-string` instructions concerning ads/background playback to their native classes and methods. Strings alone are investigation leads, not patch locations or proof that a path executes.
- Traced `whv.H(): List` into the legacy placement-renderer list and the `sbc` ad-break scheduler. The broader `sbc.b` routine also constructs a non-ad content entry and is intentionally left unchanged.
- Implemented a version-locked experimental patch that makes `whv.H()` return a new empty mutable `ArrayList`. It changes one method body, updates DEX integrity fields, preserves code-item layout and rejects unknown input or a mismatched method fingerprint. It does not cover other ad-slot/player-bytes paths and does not establish complete ad removal.
- Added intact APK/split intake checks for package, matching version codes, cryptographic APK signatures and matching split signers. Signature validity alone does not authenticate Google as publisher.

## Required input before an installable build

An untouched APK/export including the required split packages is needed to verify the original signature, preserve the complete package structure and prepare an installable variant. The supplied manifest declares `android:requiredSplitTypes="base__density"` and `com.android.vending.splits.required=true`. No separately signed density split was supplied. Having all file paths referenced by the base resource table does not establish that the complete installed split set is present.

The originally uploaded intact APK exceeds the workspace transfer tool's 32 MiB limit. Its Drive link is blocked by this environment's current network policy. Export the installed app as a complete split-APK bundle and split the export into **25 MB parts** for transfer; retain every part. Do not send more individually extracted resource files. The parts must reconstruct the exact original bytes, including the APK signing block.

The official upstream ReVanced patches repository currently returns HTTP 451, so no current upstream patch compatibility has been verified or assumed. This project's small experimental patch is independently implemented from the provided bytecode, with its narrow scope documented above.

## Reproduce the local work

Python 3.12+, JDK and Android SDK build tools 35.0.0 are used. Commands do not install the supplied code or sign into any account.

```sh
python3 tools/prepare_inputs.py --parts /path/to/previous-inspection --output work/reconstructed-analysis.zip --sdk /path/to/android-sdk --reports reports
python3 tools/map_native_candidates.py --parts /path/to/previous-inspection --dexdump /path/to/android-sdk/build-tools/35.0.0/dexdump --output reports/native-candidates.json
python3 tools/experimental_ad_patch.py --dex /path/to/previous-inspection/classes2.dex --output work/patched-ad-placement.dex --lock patch-lock.json --report reports/experimental-patch.json --experimental-ad-placement
```

Inspect an intact exported base and its matching density split when available:

```sh
python3 tools/check_original_apk.py --base inputs/base.apk --split inputs/config.xxxhdpi.apk --sdk /path/to/android-sdk --report reports/original-apk-check.json
```

The exact split filename depends on the device. The tool checks the manifest's split type rather than trusting the filename.

Packaging and signing remain pending. A changed signing key affects compatibility with installed originals, Google sign-in and signature-based service access. An eventual standalone package also needs a reviewed package-ID/provider-authority strategy; renaming a launcher label alone does not make it an independent install. No integrity or account checks have been patched by this project.

## Verification boundary

Android SDK `dexdump -c` checks the patched DEX checksum. Independent JADX simple-mode output confirms that the replacement method returns a new `ArrayList`. The original input hash is unchanged, and a byte-difference audit limits modifications to the selected code item and DEX integrity header. These checks do not substitute for ART verification or real app playback. The intake checker was tested to reject the signed Pulse APK because its application package is different.

Original app parts and patched DEX outputs stay under ignored local work/input directories. Repository content is the independent tooling, version lock and factual status reports.
