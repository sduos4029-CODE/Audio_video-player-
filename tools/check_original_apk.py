#!/usr/bin/env python3
"""Inspect an intact base APK and optional split APKs; do not install or patch."""
import argparse
import hashlib
import json
import re
import subprocess
import zipfile
from pathlib import Path


def inspect(path, sdk):
    tools = sdk / "build-tools/35.0.0"
    with zipfile.ZipFile(path) as archive:
        if archive.testzip() is not None or "AndroidManifest.xml" not in archive.namelist():
            raise ValueError("Invalid APK archive")
    manifest = subprocess.run([str(tools / "aapt2"), "dump", "xmltree", str(path), "--file", "AndroidManifest.xml"], capture_output=True, text=True)
    if manifest.returncode:
        raise ValueError("Manifest decoding failed")
    signature = subprocess.run([str(tools / "apksigner"), "verify", "--print-certs", str(path)], capture_output=True, text=True)
    def attr(name):
        match = re.search(r'A: (?:[^\n]*:)?' + re.escape(name) + r'(?:\([^)]*\))?="([^"]+)"', manifest.stdout)
        return match.group(1) if match else None
    def integer(name):
        match = re.search(r'android:' + re.escape(name) + r'\([^)]*\)=([0-9]+)', manifest.stdout)
        return int(match.group(1)) if match else 0
    certs = re.findall(r"certificate SHA-256 digest: ([0-9a-f]{64})", signature.stdout)
    return {"file": path.name, "sha256": hashlib.sha256(path.read_bytes()).hexdigest(),
            "bytes": path.stat().st_size, "package": attr("package"), "version_name": attr("versionName"),
            "version_code": integer("versionCode"), "version_code_major": integer("versionCodeMajor"),
            "split": attr("split"), "required_split_types": attr("requiredSplitTypes"),
            "split_types": attr("splitTypes"), "valid_apk_signature": signature.returncode == 0,
            "signer_sha256": certs, "publisher_authenticated": False}


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--base", type=Path, required=True)
    parser.add_argument("--split", type=Path, action="append", default=[])
    parser.add_argument("--sdk", type=Path, required=True)
    parser.add_argument("--report", type=Path, required=True)
    args = parser.parse_args()
    base = inspect(args.base, args.sdk)
    splits = [inspect(path, args.sdk) for path in args.split]
    failures = []
    if base["package"] != "com.google.android.apps.youtube.music":
        failures.append("Input is not the expected native YouTube Music package")
    if not base["valid_apk_signature"]:
        failures.append("Original APK signature verification failed")
    for split in splits:
        if split["package"] != base["package"] or not split["valid_apk_signature"] or split["signer_sha256"] != base["signer_sha256"]:
            failures.append("Split package/signature mismatch: " + split["file"])
        if split["version_name"] and split["version_name"] != base["version_name"]:
            failures.append("Split version mismatch: " + split["file"])
        if (split["version_code"],split["version_code_major"]) != (base["version_code"],base["version_code_major"]):
            failures.append("Split version-code mismatch: " + split["file"])
    if "base__density" in (base["required_split_types"] or "") and not any("base__density" in (s["split_types"] or "") for s in splits):
        failures.append("Required density split has not been supplied")
    report = {"base": base, "splits": splits, "checks_passed": not failures, "failures": failures,
              "note": "A valid APK signature does not itself authenticate Google as publisher. Compare signer hashes with an independently trusted installed original before account use."}
    args.report.parent.mkdir(parents=True, exist_ok=True)
    args.report.write_text(json.dumps(report, indent=2))
    print(json.dumps(report, indent=2))
    raise SystemExit(1 if failures else 0)
