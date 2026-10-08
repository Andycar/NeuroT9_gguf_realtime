# NeuroT9 GGUF Realtime

Goal: Android text normalizer ("neuro T9") running a local GGUF model. Not an IME: the model works on a
finished chunk of text, so integration is `android.intent.action.PROCESS_TEXT`, not a keyboard.
User communicates in Russian; docs/notes may be Russian, code and identifiers in English.

## Model contract (verified experimentally, see docs/model-notes.md)
- `neurocorrect.gguf`: llama arch, 6 layers, d=768, vocab 32000 (Russian SentencePiece), Q8_0, ctx 4000. Origin unknown; do NOT claim it is from Yandex.
- Input = raw text + token id 4 (`[SEP]`), **no BOS** (`add_bos_token: false`), temperature 0, `repeat_penalty` 1.0.
  Output = formatted text (case, punctuation) then EOS. No chat template: use `/completion`, never chat endpoints.
- With BOS the model degenerates (truncates or loops). GGUF metadata wrongly says add_bos_token=true.
- Unverified: typo correction, yo-restoration, glued words, long multi-sentence input (split by sentence if needed).

## Layout
- `app/` – Android app (package `com.neurot9.app`): `CorrectActivity` (PROCESS_TEXT, Translucent theme because NoDisplay must finish before onResume returns), `LlamaClient`, `MainActivity`. No Android SDK in the cloud sandbox, so it cannot be built there.
- `scripts/correct.py` – stdlib-only client for llama-server `/completion`.
- `scripts/probe.py` – runs the open-question test set against a running server (user is on Windows, PowerShell 7).
- `docs/model-notes.md` – findings and open questions.

## Gotchas
- Windows: PowerShell `"$x[SEP]"` indexes the variable; use `"${x}[SEP]"`. `ConvertTo-Json` needs `-Depth 5` for nested arrays. Write scripts as UTF-8 BOM.
- New llama.cpp: `llama-cli` has no `-no-cnv`; use `llama-completion` or `llama-server`.
- Never commit `*.gguf`.
