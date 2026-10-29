#!/usr/bin/env python3
"""Version-locked experimental patch of the native ad-placement list accessor.

Produces a research DEX only. It does not sign/install an APK or change account
entitlements. Unknown bytes, ambiguous targets and already-patched input fail.
"""
import argparse
import hashlib
import json
import struct
import zlib
from pathlib import Path


def uleb(data, offset):
    value = shift = 0
    for _ in range(5):
        byte = data[offset]
        offset += 1
        value |= (byte & 127) << shift
        if byte < 128:
            return value, offset
        shift += 7
    raise ValueError("Invalid ULEB128")


class Dex:
    def __init__(self, data):
        self.data = data
        if data[:8] != b"dex\n035\x00" or self.u32(32) != len(data):
            raise ValueError("Unsupported DEX header")
        if hashlib.sha1(data[32:]).digest() != data[12:32] or zlib.adler32(data[12:]) & 0xffffffff != self.u32(8):
            raise ValueError("DEX integrity check failed")
        self.strings = []
        for i in range(self.u32(56)):
            _, pos = uleb(data, self.u32(self.u32(60) + i * 4))
            self.strings.append(data[pos:data.index(0, pos)].decode("utf-8", "replace"))
        self.types = [self.strings[self.u32(self.u32(68) + i * 4)] for i in range(self.u32(64))]
        self.prototypes = []
        for i in range(self.u32(72)):
            pos = self.u32(76) + i * 12
            params = self.u32(pos + 8)
            arguments = [] if params == 0 else [self.types[self.u16(params + 4 + j * 2)] for j in range(self.u32(params))]
            self.prototypes.append("(" + "".join(arguments) + ")" + self.types[self.u32(pos + 4)])
        self.methods = []
        for i in range(self.u32(88)):
            cls, proto, name = struct.unpack_from("<HHI", data, self.u32(92) + i * 8)
            self.methods.append((self.types[cls], self.strings[name], self.prototypes[proto]))

    def u16(self, offset):
        return struct.unpack_from("<H", self.data, offset)[0]

    def u32(self, offset):
        return struct.unpack_from("<I", self.data, offset)[0]

    def code_for(self, target):
        found = []
        for i in range(self.u32(96)):
            cls = self.u32(100) + i * 32
            if self.types[self.u32(cls)] != target[0]:
                continue
            pos = self.u32(cls + 24)
            if pos == 0:
                continue
            counts = []
            for _ in range(4):
                count, pos = uleb(self.data, pos)
                counts.append(count)
            for _ in range(counts[0] + counts[1]):
                _, pos = uleb(self.data, pos)
                _, pos = uleb(self.data, pos)
            for count in counts[2:]:
                index = 0
                for _ in range(count):
                    delta, pos = uleb(self.data, pos)
                    index += delta
                    _, pos = uleb(self.data, pos)
                    code, pos = uleb(self.data, pos)
                    if self.methods[index] == tuple(target):
                        found.append(code)
        if len(found) != 1 or found[0] == 0:
            raise ValueError("Patch target is absent or ambiguous")
        return found[0]


def patch(source, output, lock_path, report_path):
    if source.resolve() == output.resolve():
        raise ValueError("Output must not overwrite the original")
    lock = json.loads(lock_path.read_text())
    original = source.read_bytes()
    if hashlib.sha256(original).hexdigest() != lock["dex_sha256"]:
        raise ValueError("Input does not match the reviewed version-locked DEX")
    dex = Dex(original)
    code = dex.code_for(lock["target"])
    count = dex.u32(code + 12)
    body = original[code + 16:code + 16 + count * 2]
    if hashlib.sha256(body).hexdigest() != lock["code_sha256"] or count < 6 or dex.u16(code) < 1:
        raise ValueError("Code fingerprint or register requirements do not match")
    if dex.u16(code + 6) != 0:
        raise ValueError("Target has exception handlers; patch is not supported")
    array_type = dex.types.index("Ljava/util/ArrayList;")
    init = dex.methods.index(("Ljava/util/ArrayList;", "<init>", "()V"))
    # new-instance v0, ArrayList; invoke-direct {v0}, ArrayList.<init>; return-object v0.
    replacement = struct.pack("<6H", 0x0022, array_type, 0x1070, init, 0, 0x0011)
    data = bytearray(original)
    data[code + 16:code + 16 + count * 2] = replacement + bytes(count * 2 - len(replacement))
    struct.pack_into("<I", data, code + 8, 0)  # No original debug-local stream on the replacement.
    struct.pack_into("<H", data, code + 4, max(1, dex.u16(code + 4)))
    data[12:32] = hashlib.sha1(data[32:]).digest()
    struct.pack_into("<I", data, 8, zlib.adler32(data[12:]) & 0xffffffff)
    Dex(data)
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_bytes(data)
    report_path.write_text(json.dumps({"status": "experimental research DEX; no runnable APK produced", "target": lock["target"], "original_sha256": hashlib.sha256(original).hexdigest(), "patched_sha256": hashlib.sha256(data).hexdigest(), "changed_method_count": 1, "server_entitlements_modified": False, "runtime_verified": False, "scope": "Return an empty mutable list from the reviewed native ad-placement accessor; other ad paths are not covered"}, indent=2))
    print("Experimental patch produced; runtime behavior remains unverified.")


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--dex", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument("--lock", type=Path, required=True)
    parser.add_argument("--report", type=Path, required=True)
    parser.add_argument("--experimental-ad-placement", action="store_true")
    args = parser.parse_args()
    if not args.experimental_ad_placement:
        parser.error("Explicit --experimental-ad-placement is required")
    patch(args.dex, args.output, args.lock, args.report)
