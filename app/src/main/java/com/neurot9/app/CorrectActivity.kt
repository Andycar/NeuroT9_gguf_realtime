package com.neurot9.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast

/** Handles ACTION_PROCESS_TEXT: sends the selection to the model and returns the result. */
class CorrectActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString()
        if (text.isNullOrBlank()) {
            finish()
            return
        }
        val readOnly = intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false)
        val url = getSharedPreferences(PREFS, MODE_PRIVATE).getString(KEY_URL, LlamaClient.DEFAULT_URL)!!

        Thread {
            val result = runCatching { LlamaClient(url).correct(text) }
            runOnUiThread {
                result.onSuccess { corrected ->
                    if (readOnly) {
                        Toast.makeText(this, corrected, Toast.LENGTH_LONG).show()
                    } else {
                        setResult(RESULT_OK, Intent().putExtra(Intent.EXTRA_PROCESS_TEXT, corrected))
                    }
                }.onFailure {
                    Toast.makeText(this, "NeuroT9: ${it.message ?: it.javaClass.simpleName}", Toast.LENGTH_LONG).show()
                }
                finish()
            }
        }.start()
    }

    companion object {
        const val PREFS = "neurot9"
        const val KEY_URL = "server_url"
    }
}
