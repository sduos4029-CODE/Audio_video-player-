#!/usr/bin/env bash
set -euo pipefail
: "${ANDROID_NDK_ROOT:?Set ANDROID_NDK_ROOT to Android NDK r27c or newer}"
task_root=$(cd "$(dirname "$0")/.." && pwd)
task_llvm="$ANDROID_NDK_ROOT/toolchains/llvm/prebuilt/linux-x86_64/bin"
export CARGO_TARGET_AARCH64_LINUX_ANDROID_LINKER="$task_llvm/aarch64-linux-android26-clang"
export CARGO_TARGET_ARMV7_LINUX_ANDROIDEABI_LINKER="$task_llvm/armv7a-linux-androideabi26-clang"
export CARGO_TARGET_X86_64_LINUX_ANDROID_LINKER="$task_llvm/x86_64-linux-android26-clang"
task_manifest="$task_root/native/adblock/Cargo.toml"
cargo +1.90.0 test --locked --manifest-path "$task_manifest"
cargo +1.90.0 build --locked --release --manifest-path "$task_manifest"
"$task_root/native/adblock/target/release/prepare" "$task_root/app/src/main/assets/adblock/rules.txt" "$task_root/app/src/main/assets/adblock/resources.json" "$task_root/app/src/main/assets/adblock/pages.json"
node "$task_root/tools/build-web-rules.mjs" "$task_root/app/src/main/assets/adblock"
export RUSTFLAGS='-C link-arg=-Wl,-z,max-page-size=16384 -C link-arg=-Wl,-z,common-page-size=16384'
for task_pair in aarch64-linux-android:arm64-v8a armv7-linux-androideabi:armeabi-v7a x86_64-linux-android:x86_64; do
 task_target=${task_pair%%:*}
 task_abi=${task_pair##*:}
 cargo +1.90.0 build --locked --release --lib --manifest-path "$task_manifest" --target "$task_target"
 mkdir -p "$task_root/app/src/main/jniLibs/$task_abi"
 cp "$task_root/native/adblock/target/$task_target/release/libpulse_adblock.so" "$task_root/app/src/main/jniLibs/$task_abi/"
done
