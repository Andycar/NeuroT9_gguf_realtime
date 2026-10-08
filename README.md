# NeuroT9 GGUF Realtime

Offline "neuro-T9" for Android: select text in any app, tap an action, and the text is rewritten with
capitalization, punctuation and spacing restored by a small local GGUF model (`neurocorrect.gguf`, ~92M params).

Status: **model research done, client scripts ready, Android app scaffolded (not yet built or run on a device).**
See [docs/model-notes.md](docs/model-notes.md) for everything learned about the model.

## Quick start (desktop test)

```
llama-server -m neurocorrect.gguf -c 4000 --host 127.0.0.1 --port 8099
python scripts/correct.py "вчера я был на работе и очень устал"
```

`neurocorrect.gguf` is not committed (see `.gitignore`); put it in the repo root or `models/`.

## Android app (`app/`)
Kotlin, minSdk 26, no third-party dependencies, Gradle wrapper 8.9 (AGP 8.7.3). Open in Android Studio or `./gradlew assembleDebug`.
- `CorrectActivity`: `PROCESS_TEXT` handler ("Исправить" in the selection toolbar). Calls `LlamaClient`, returns the text (or shows a Toast if the field is read-only).
- `LlamaClient`: POST `/completion` with `[text, 4]`, no BOS, temperature 0.
- `MainActivity`: server URL setting and a manual test box. Default URL `http://127.0.0.1:8099`.
- Cleartext HTTP is allowed for loopback only (`network_security_config.xml`).

To try it: run `llama-server -m neurocorrect.gguf -c 4000 --host 127.0.0.1 --port 8099` in Termux, install the app, select text in any app.

## Plan
1. Desktop validation of the model with `scripts/correct.py` (settle the open questions in model-notes).
2. Build and test the Android app on a device (scaffolded).
3. Backend: first `llama-server` in Termux on `127.0.0.1`, later JNI/llama.cpp embedded in the app.
