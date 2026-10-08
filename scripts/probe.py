#!/usr/bin/env python3
"""Run the open-question test set against a running llama-server."""
import sys
from correct import correct

TESTS = {
    "typos": "привет как дила я вчира был на рабте",
    "yo": "еще раз все прошло четко и легко",
    "glued": "вчеряябылнаработе и оченьустал",
    "long": "вчера я был на работе и очень устал поэтому лег спать рано а сегодня встал в шесть утра и поехал в офис на метро",
}

port = int(sys.argv[1]) if len(sys.argv) > 1 else 8099
for name, src in TESTS.items():
    out, stop, n = correct(src, port=port)
    print(f"=== {name}\nIN : {src}\nOUT: {out}\n[{stop}, {n} tok]\n")
