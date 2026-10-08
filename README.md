# NeuroT9 GGUF Realtime

Offline "neuro-T9" for Android: select text in any app, tap an action, and the text is rewritten with
capitalization, punctuation and spacing restored by a small local GGUF model (`neurocorrect.gguf`, ~92M params).

Status: **model research done, client scripts ready, Android app not started.**
See [docs/model-notes.md](docs/model-notes.md) for everything learned about the model.

## Quick start (desktop test)

```
llama-server -m neurocorrect.gguf -c 4000 --host 127.0.0.1 --port 8099
python scripts/correct.py "вчера я был на работе и очень устал"
```

`neurocorrect.gguf` is not committed (see `.gitignore`); put it in the repo root or `models/`.

## Plan
1. Desktop validation of the model with `scripts/correct.py` (settle the open questions in model-notes).
2. Android app with an `ACTION_PROCESS_TEXT` activity (selection toolbar entry "Исправить").
3. Backend: first `llama-server` in Termux on `127.0.0.1`, later JNI/llama.cpp embedded in the app.
