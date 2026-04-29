package com.smartcampus.app.ui.student

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.smartcampus.app.R
import com.smartcampus.app.api.ApiClient
import com.smartcampus.app.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SkillRoadmapActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var session: SessionManager
    private lateinit var skillName: String

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_skill_roadmap)

        session = SessionManager(this)
        skillName = intent.getStringExtra("SKILL_NAME") ?: "React"

        webView = findViewById(R.id.webViewRoadmap)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.addJavascriptInterface(RoadmapInterface(), "Android")
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                loadRoadmapData()
            }
        }
        webView.loadUrl("file:///android_asset/skill_roadmap.html")
    }

    private fun loadRoadmapData() {
        ApiClient.getApi().getRoadmap(session.authToken, skillName).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful && response.body() != null) {
                    val roadmapJson = response.body().toString()
                    // Now load progress
                    loadProgress(roadmapJson)
                } else {
                    runOnUiThread {
                        webView.evaluateJavascript("showNoRoadmap('${escapeJs(skillName)}')", null)
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                runOnUiThread {
                    webView.evaluateJavascript("showNoRoadmap('${escapeJs(skillName)}')", null)
                    Toast.makeText(this@SkillRoadmapActivity, "Network error", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun loadProgress(roadmapJson: String) {
        ApiClient.getApi().getRoadmapProgress(session.authToken, skillName).enqueue(object : Callback<JsonArray> {
            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                val progressJson = if (response.isSuccessful && response.body() != null) {
                    response.body().toString()
                } else {
                    "[]"
                }
                runOnUiThread {
                    webView.evaluateJavascript(
                        "loadRoadmap('${escapeJs(roadmapJson)}', '${escapeJs(progressJson)}')", null
                    )
                }
            }

            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                runOnUiThread {
                    webView.evaluateJavascript(
                        "loadRoadmap('${escapeJs(roadmapJson)}', '[]')", null
                    )
                }
            }
        })
    }

    private fun escapeJs(s: String): String {
        return s.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n").replace("\r", "")
    }

    inner class RoadmapInterface {
        @JavascriptInterface
        fun onProgressChanged(completedStepsJson: String) {
            // Save progress to backend
            val body = JsonObject().apply {
                add("completedSteps", com.google.gson.JsonParser().parse(completedStepsJson).asJsonArray)
            }
            ApiClient.getApi().saveRoadmapProgress(session.authToken, skillName, body)
                .enqueue(object : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        // Saved silently
                    }
                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        // Ignore save failures silently
                    }
                })
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
