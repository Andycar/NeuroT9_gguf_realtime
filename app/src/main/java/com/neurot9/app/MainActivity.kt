package com.neurot9.app

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView

/** Settings + manual test screen. Programmatic UI to keep the scaffold free of layout files. */
class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences(CorrectActivity.PREFS, MODE_PRIVATE)
        val pad = (16 * resources.displayMetrics.density).toInt()

        val url = EditText(this).apply {
            hint = "llama-server URL"
            setText(prefs.getString(CorrectActivity.KEY_URL, LlamaClient.DEFAULT_URL))
        }
        val input = EditText(this).apply { hint = "текст без заглавных и знаков препинания" }
        val output = TextView(this).apply { textSize = 18f }
        val run = Button(this).apply {
            text = "Исправить"
            setOnClickListener {
                val u = url.text.toString().trim()
                prefs.edit().putString(CorrectActivity.KEY_URL, u).apply()
                output.text = "…"
                Thread {
                    val r = runCatching { LlamaClient(u).correct(input.text.toString()) }
                    runOnUiThread { output.text = r.getOrElse { "Ошибка: ${it.message}" } }
                }.start()
            }
        }

        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad, pad, pad)
            listOf(url, input, run, output).forEach { addView(it, LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)) }
        })
    }
}
