package com.smartcampus.app.ui.student

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.smartcampus.app.R
import com.smartcampus.app.api.ApiClient
import com.smartcampus.app.models.SkillRecommendation
import com.smartcampus.app.utils.SessionManager
import com.google.gson.Gson
import com.google.gson.JsonArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SkillMapActivity : AppCompatActivity() {

    private lateinit var session: SessionManager
    private lateinit var webView: WebView
    private val gson = Gson()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_skill_map)
        session = SessionManager(this)

        findViewById<android.widget.ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        webView = findViewById(R.id.webViewMap)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true

        webView.addJavascriptInterface(MapInterface(), "Android")

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                loadRegionData()
            }
        }

        webView.loadUrl("file:///android_asset/skill_map.html")
    }

    private fun loadRegionData() {
        // Fetch region data from API
        ApiClient.getApi().getRegions(session.authToken).enqueue(object : Callback<JsonArray> {
            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                if (response.isSuccessful && response.body() != null) {
                    val regionsJson = response.body().toString()
                    runOnUiThread {
                        webView.evaluateJavascript("loadRegions('${escapeJs(regionsJson)}')", null)
                    }
                }
            }

            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                runOnUiThread {
                    Toast.makeText(this@SkillMapActivity, "Failed to load region data", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun loadRegionSkills(regionName: String) {
        ApiClient.getApi().getRegionRecommendations(session.authToken, regionName)
            .enqueue(object : Callback<List<SkillRecommendation>> {
                override fun onResponse(call: Call<List<SkillRecommendation>>, response: Response<List<SkillRecommendation>>) {
                    if (response.isSuccessful && response.body() != null) {
                        val skillsJson = gson.toJson(response.body())
                        runOnUiThread {
                            webView.evaluateJavascript(
                                "displayRegionSkills('${escapeJs(regionName)}', '${escapeJs(skillsJson)}')",
                                null
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<List<SkillRecommendation>>, t: Throwable) {
                    runOnUiThread {
                        Toast.makeText(this@SkillMapActivity, "Failed to load skills", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    private fun escapeJs(str: String): String {
        return str.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n").replace("\r", "")
    }

    inner class MapInterface {
        @JavascriptInterface
        fun onRegionSelected(regionName: String) {
            loadRegionSkills(regionName)
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
