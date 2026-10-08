#!/usr/bin/env python3
"""Send text to a local llama-server running neurocorrect.gguf and print the result.

Usage: correct.py [--port 8099] [--host 127.0.0.1] TEXT...
       echo text | correct.py
"""
import argparse
import json
import sys
import urllib.request

SEP_ID = 4  # [SEP]


def correct(text, host="127.0.0.1", port=8099, n_predict=256):
    body = {
        "prompt": [text, SEP_ID],  # raw text, then [SEP] token id (bypasses special-token parsing)
        "temperature": 0,
        "n_predict": n_predict,
        "repeat_penalty": 1.0,
        "add_bos_token": False,  # model was trained without BOS
        "cache_prompt": False,
    }
    req = urllib.request.Request(
        f"http://{host}:{port}/completion",
        data=json.dumps(body).encode("utf-8"),
        headers={"Content-Type": "application/json; charset=utf-8"},
    )
    with urllib.request.urlopen(req, timeout=60) as r:
        resp = json.load(r)
    return resp["content"].strip(), resp.get("stop_type"), resp.get("tokens_predicted")


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--host", default="127.0.0.1")
    ap.add_argument("--port", type=int, default=8099)
    ap.add_argument("text", nargs="*")
    a = ap.parse_args()
    text = " ".join(a.text) if a.text else sys.stdin.read().strip()
    out, stop, n = correct(text, a.host, a.port)
    print(out)
    print(f"[{stop}, {n} tok]", file=sys.stderr)


if __name__ == "__main__":
    main()
