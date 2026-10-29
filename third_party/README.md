# Filtering source and notices

The source archives here correspond to the components packaged into Pulse v1.10. Their original licenses and copyright notices are retained. Generated filtering assets remain readable under `app/src/main/assets/adblock/`; full license texts and native dependency notices are shipped in the APK and accessible through the browser's **Open-source licenses** menu.

- `adblock-0.13.5.crate`: the published source crate for Brave's adblock-rust 0.13.5, MPL-2.0. Cargo.lock pins the Rust dependency graph with registry checksums. The unmodified engine is linked through Pulse's own JNI wrapper in `native/adblock`.
- `ublock-source.tar.gz`: complete uBlock Origin source at `aaefbdb76ed367da08d8aec193d6e8fa1b84485d`, GPL-3.0-or-later. The generated resource bundle contains registered scriptlet functions and their dependency metadata, plus local replacement resources. Original source and the build tools for producing these forms are available in this archive.
- `filter-sources.tar.gz`: the selected original uAssets/EasyList filter files and license at uAssets revision `332cd21cf59b8dc7d5c9331eeeb9899a856ee225`. uAssets uses GPL-3.0; the included EasyList is distributed under its GPL-3.0 option. List data is preprocessed for mobile Chromium without HTML-response filtering support before engine compilation.

The 122,872-line generated rule text includes comments and directives; that is not a count of successfully parsed or active rules. The resource manifest records 201 scriptlet/helper/replacement resources, plus SHA-256 checksums of generated assets. Only rules resolved for the four supported YouTube hostnames become the bundled page script. There are no downloaded executable updates at runtime; refreshes currently require rebuilding the snapshot and publishing a new APK.

To reproduce assets, extract the uBlock source archive into a directory and use the pinned uAssets checkout (or reconstruct a Git checkout of the pinned revision). Run:

```sh
PULSE_UBLOCK_REVISION=aaefbdb76ed367da08d8aec193d6e8fa1b84485d PULSE_UASSETS_REVISION=332cd21cf59b8dc7d5c9331eeeb9899a856ee225 node tools/prepare-adblock-assets.mjs /path/to/ublock /path/to/uAssets app/src/main/assets/adblock
ANDROID_NDK_ROOT=/path/to/android-ndk-r27c bash tools/build-native-adblock.sh
```

The preparation script records source Git revisions. The explicit revision variables shown above allow reproduction from the source archives without Git metadata. Build requires Rust 1.90.0 and Android Rust targets `aarch64-linux-android`, `armv7-linux-androideabi`, and `x86_64-linux-android`, Node.js, and NDK r27c or newer. Native libraries are built with 16 KB ELF segment alignment and packaged for arm64-v8a, armeabi-v7a and x86_64. The build-time host library is not shipped in the APK.

Verify the JNI boundary against the actual bundled rules:

```sh
javac -d /tmp/pulse-native-smoke app/src/main/java/com/noir/player/NativeAdblock.java tools/NativeAdblockSmoke.java
java -Djava.library.path="$PWD/native/adblock/target/release" -cp /tmp/pulse-native-smoke NativeAdblockSmoke app/src/main/assets/adblock
node tools/test-browser-filter.cjs
```

The browser fixture runner requires Playwright and a local Chromium binary. It fulfills every request with local fixtures; it does not contact YouTube or establish live ad suppression. No Google APK implementation is included. Pulse is independent of the upstream projects and their trademarks.
