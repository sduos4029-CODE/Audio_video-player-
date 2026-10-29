#!/usr/bin/env python3
"""Locate real const-string instructions relevant to native ad/background playback.

These are investigation candidates, never automatically chosen patch locations.
"""
import argparse
import json
import re
import subprocess
from pathlib import Path

CLASS = re.compile(r"Class descriptor\s*:\s*'([^']+)'")
NAME = re.compile(r"name\s*:\s*'([^']+)'")
TYPE = re.compile(r"type\s*:\s*'([^']+)'")
LITERAL = re.compile(r'const-string(?:/jumbo)?\s+v\d+, "(.*)" // string@')
KEYWORD = re.compile(r"ad.?placement|ad.?playback|ad.?break|player.?ads|ad.?slot|background.?playback|background playback|ad.?serving", re.I)


def scan(root, tool, output):
    rows = []
    for dex in sorted(root.glob("classes*.dex")):
        current_class = current_method = prototype = ""
        with subprocess.Popen([str(tool), "-d", str(dex)], stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True, errors="replace") as process:
            for line in process.stdout:
                match = CLASS.search(line)
                if match:
                    current_class = match.group(1)
                    current_method = prototype = ""
                match = NAME.search(line)
                if match:
                    current_method = match.group(1)
                match = TYPE.search(line)
                if match:
                    prototype = match.group(1)
                match = LITERAL.search(line)
                if match and KEYWORD.search(match.group(1)):
                    rows.append({"dex": dex.name, "class": current_class, "method": current_method, "prototype": prototype, "literal": match.group(1), "instruction": line.strip()})
            error = process.stderr.read()
            if process.wait():
                raise RuntimeError("DEX disassembly failed: " + error[:300])
    output.write_text(json.dumps({"status": "static candidates only; no patch applied or verified", "count": len(rows), "candidates": rows}, indent=2))
    print("Native code candidates:", len(rows))


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--parts", type=Path, required=True)
    parser.add_argument("--dexdump", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    scan(args.parts, args.dexdump, args.output)
