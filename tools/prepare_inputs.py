#!/usr/bin/env python3
"""Reconstruct a resource-complete analysis ZIP from the user's extracted files.

This never signs an APK, removes split requirements, modifies code, or claims
publisher authenticity. Original files remain untouched.
"""
import argparse
import hashlib
import json
import re
import struct
import subprocess
import zipfile
import zlib
from pathlib import Path, PurePosixPath


def sha(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def safe_name(value):
    name = PurePosixPath(value)
    if name.is_absolute() or ".." in name.parts or "\\" in value:
        raise ValueError("Unsafe archive name")
    return str(name)


def prepare(root, output, sdk, reports):
    reports.mkdir(parents=True, exist_ok=True)
    output.parent.mkdir(parents=True, exist_ok=True)
    entries = {}
    aliases = json.loads((root / "res-additional/inferred-filename-aliases.json").read_text())["aliases"]
    expected = set(json.loads((root / "res-additional/resource-file-map.json").read_text()))
    repaired = []

    def add(name, path):
        name = safe_name(name)
        if name in entries:
            raise ValueError("Duplicate archive path: " + name)
        if not path.resolve().is_relative_to(root.resolve()) or path.is_symlink():
            raise ValueError("Source escapes input directory")
        entries[name] = path

    for name in ["AndroidManifest.xml", "resources.arsc"]:
        add(name, root / name)
    dex = []
    for path in sorted(root.glob("classes*.dex")):
        data = path.read_bytes()
        valid = (data[:4] == b"dex\n" and len(data) >= 112
                 and struct.unpack_from("<I", data, 32)[0] == len(data)
                 and hashlib.sha1(data[32:]).digest() == data[12:32]
                 and zlib.adler32(data[12:]) & 0xffffffff == struct.unpack_from("<I", data, 8)[0])
        if not valid:
            raise ValueError("DEX integrity failed: " + path.name)
        dex.append({"file": path.name, "sha256": sha(path), "bytes": len(data)})
        add(path.name, path)
    for path in sorted((root / "res-additional/extracted").rglob("*")):
        if not path.is_file():
            continue
        old = path.relative_to(root / "res-additional/extracted").as_posix()
        name = aliases.get(old, old)
        if old != name:
            repaired.append({"supplied": old, "inferred_original": name, "sha256": sha(path)})
        add(name, path)
    for path in sorted((root / "assets-additional/extracted").rglob("*")):
        if path.is_file():
            add(path.relative_to(root / "assets-additional/extracted").as_posix(), path)
    native_index = json.loads((root / "native-additional/native-library-summary.json").read_text())
    native = {PurePosixPath(item["file"]).name: item for item in native_index}
    for path in sorted((root / "native-additional/libraries").rglob("*.so")):
        item = native[path.name]
        if sha(path) != item["sha256"]:
            raise ValueError("Native library integrity mismatch: " + path.name)
        add(item["file"], path)
    missing = sorted(expected - set(entries))
    if missing:
        raise ValueError("Missing referenced resources: " + ", ".join(missing))
    with zipfile.ZipFile(output, "w", compression=zipfile.ZIP_DEFLATED) as archive:
        for name, source in sorted(entries.items()):
            archive.write(source, name)
    aapt = sdk / "build-tools/35.0.0/aapt2"
    manifest = subprocess.run([str(aapt), "dump", "xmltree", str(output), "--file", "AndroidManifest.xml"], capture_output=True, text=True)
    resources = subprocess.run([str(aapt), "dump", "resources", str(output)], capture_output=True, text=True)
    splits = subprocess.run([str(aapt), "dump", "xmltree", str(output), "--file", "res/xml/splits0.xml"], capture_output=True, text=True)
    for name, result in [("manifest", manifest), ("resources", resources), ("splits", splits)]:
        (reports / (name + ".txt")).write_text(result.stdout)
        (reports / (name + "-warnings.txt")).write_text(result.stderr)
    if manifest.returncode or resources.returncode:
        raise ValueError("AAPT could not inspect reconstructed data; see report files")
    package = re.search(r'A: package="([^"]+)"', manifest.stdout).group(1)
    version = re.search(r'android:versionName\([^)]*\)="([^"]+)"', manifest.stdout).group(1)
    if package != "com.google.android.apps.youtube.music" or version != "9.43.51":
        raise ValueError("Unsupported input package/version")
    result = {
        "kind": "analysis ZIP, not an installable or authenticated APK",
        "package": package,
        "version": version,
        "dex": dex,
        "entry_count": len(entries),
        "referenced_resource_paths": len(expected),
        "missing_referenced_resource_paths": missing,
        "inferred_renames": repaired,
        "requires_density_split": "base__density" in manifest.stdout,
        "signature_verified": False,
        "dex_modified": False,
        "ads_patch_applied": False,
        "resource_warning_lines": len(resources.stderr.splitlines()),
        "archive_sha256": sha(output),
        "archive_bytes": output.stat().st_size,
        "blockers": ["Original signed APK is unavailable for publisher verification", "Declared density split package has not been supplied", "No verified native ad-removal patch has been selected for this version"],
    }
    (reports / "input-readiness.json").write_text(json.dumps(result, indent=2))
    print(json.dumps({k: v for k, v in result.items() if k not in ["dex", "inferred_renames"]}, indent=2))


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--parts", required=True, type=Path)
    parser.add_argument("--output", required=True, type=Path)
    parser.add_argument("--sdk", required=True, type=Path)
    parser.add_argument("--reports", required=True, type=Path)
    args = parser.parse_args()
    prepare(args.parts, args.output, args.sdk, args.reports)
