# Separate native project status

Native app observed in the supplied files: YouTube Music 9.43.51, package `com.google.android.apps.youtube.music`.

Completed: six original DEX integrity checks; 25 native library inventory checks; restoration of 40 renamed resource paths; all 2,500 base resource file paths present; zero AAPT resource warnings; 154 disassembled ad/background string-reference candidates; a version-locked experimental patch of `whv.H(): List`.

The patched method returns a new empty mutable list. Android SDK dexdump verifies the DEX checksum; independent JADX output confirms the replacement; a byte-difference audit confines changes to that code item and DEX integrity metadata. The original DEX hash is unchanged. The patch rejects a different input DEX. The APK intake checker rejects Pulse's signed APK as the wrong application.

No device install, native playback test, Google sign-in test or live ad-removal test has occurred. This patch covers one placement-list path and is not established to remove every ad. Broader content/ad scheduling code and entitlement/integrity checks remain untouched.

No installable modified APK has been produced. The intact original APK and required split package(s) are missing from the usable workspace inputs. The large original upload exceeds this tool's 32 MiB transfer limit, and the supplied Drive destination is blocked by current network policy. A complete untouched app export transferred as 25 MB parts is the next required input. More individual resource files are not needed.

The existing Pulse APK/source are unchanged by this separate project. Original Google app files, local decompilations and patched DEX outputs are excluded from the published source branch.
