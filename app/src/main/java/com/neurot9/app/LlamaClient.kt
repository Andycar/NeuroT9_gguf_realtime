package com.neurot9.app

import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Client for llama-server /completion running neurocorrect.gguf.
 * Request contract (see docs/model-notes.md): raw text + [SEP] token id, no BOS, greedy.
 */
class LlamaClient(private val baseUrl: String) {

    fun correct(text: String): String {
        val body = JSONObject()
            .put("prompt", JSONArray().put(text).put(SEP_TOKEN_ID))
            .put("temperature", 0)
            .put("n_predict", N_PREDICT)
            .put("repeat_penalty", 1.0)
            .put("add_bos_token", false)
            .put("cache_prompt", false)
            .toString()

        val conn = (URL("${baseUrl.trimEnd('/')}/completion").openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 3_000
            readTimeout = 30_000
            doOutput = true
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
        }
        try {
            conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            if (conn.responseCode != 200) error("HTTP ${conn.responseCode}")
            val resp = conn.inputStream.use { it.readBytes().toString(Charsets.UTF_8) }
            return JSONObject(resp).getString("content").trim()
        } finally {
            conn.disconnect()
        }
    }

    companion object {
        const val SEP_TOKEN_ID = 4
        const val N_PREDICT = 256
        const val DEFAULT_URL = "http://127.0.0.1:8099"
    }
}
