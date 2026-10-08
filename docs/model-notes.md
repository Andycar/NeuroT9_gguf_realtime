# neurocorrect.gguf – findings

Source: downloaded from an unverified domain (labs.owenewans.org); no evidence it is a Yandex model.
Hash it and inspect before trusting. Avoid first run on a main phone (past llama.cpp GGUF parser CVEs).

## Metadata
- arch `llama`, name `90M_rc_v3_punct Space` (rc = restore case, punct, space), size label 92M
- 6 blocks, embd 768, ffn 2048, 12 heads (kv 12), ctx 4000, rope base 10000, Q8_0 (file_type 7), 57 tensors
- ~42M params in layers, ~49M in untied embeddings/output head
- vocab 32000, SentencePiece 'llama' type, 16319 Cyrillic tokens; specials: 0 `<unk>`, 1 `<s>`, 2 `</s>`, 3 `\n`, 4 `[SEP]`, 5 `[MASK]`; byte fallback starts at 7; real vocab from 263
- `add_bos_token=True` in metadata (wrong for this model), `add_space_prefix=False`

## Behaviour
- `text` + `[SEP]` -> formatted text. Server tokenizes `[SEP]` as the single id 4.
- Speed on GTX 1660 SUPER: ~666 t/s generation.
- With BOS: output truncated to first words or looping ("Вчера." x256). Without BOS: `вчера я был на работе и очень устал` -> `Вчера я был на работе и очень устал.`
- Last observed regression: four test sentences returned 1 token (EOS) in a PowerShell loop. Suspected causes: `ConvertTo-Json` default depth 2 mangling the `prompt` array, or KV-cache reuse between requests (try `cache_prompt: false`). `scripts/correct.py` avoids both.

## Open questions
1. Does it fix typos (`дила`->`дела`) or only case/punctuation/spaces?
2. Yo-restoration (`еще`->`ещё`)? Glued words (`вчеряябылнаработе`)?
3. Behaviour on long multi-sentence input; sentence splitting needed?
4. Is `[MASK]` / `\n` a usable alternate mode?

## Integration decisions
- Not usable in FUTO Keyboard (tested, failed) or TT9 (dictionary-based, no GGUF loader).
- Plan: `PROCESS_TEXT` activity -> llama-server at 127.0.0.1 (Termux) -> later embedded llama.cpp via JNI. Build without Vulkan; CPU is enough for 92M Q8.
- Fallback if the model is poor: ai-forever/sage-fredt5-distilled-95m (MIT, T5; run via ONNX Runtime).
