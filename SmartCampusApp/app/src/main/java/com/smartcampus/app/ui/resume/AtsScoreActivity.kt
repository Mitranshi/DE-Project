package com.smartcampus.app.ui.resume

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.smartcampus.app.R
import com.smartcampus.app.api.ApiClient
import com.smartcampus.app.utils.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AtsScoreActivity : AppCompatActivity() {
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generic)
        session = SessionManager(this)
        findViewById<TextView>(R.id.tvPageTitle).text = "ATS Score Checker"

        val container = findViewById<LinearLayout>(R.id.layoutContent)

        val jdLayout = TextInputLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        val etJd = TextInputEditText(this).apply {
            hint = "Paste Job Description here..."
            minLines = 5
            setTextColor(resources.getColor(R.color.text_primary, null))
        }
        jdLayout.addView(etJd)
        container.addView(jdLayout)

        val btnCheck = MaterialButton(this).apply {
            text = "Check ATS Score"
            setBackgroundColor(resources.getColor(R.color.primary, null))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 140
            ).apply { topMargin = 16 }
        }
        container.addView(btnCheck)

        val tvResult = TextView(this).apply {
            textSize = 18f
            setTextColor(resources.getColor(R.color.accent, null))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 24 }
        }
        container.addView(tvResult)

        btnCheck.setOnClickListener {
            val jd = etJd.text.toString().trim()
            if (jd.isEmpty()) {
                Toast.makeText(this, "Please enter a job description", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            ApiClient.getApi().getAtsScore(session.authToken, mapOf("jobDescription" to jd)).enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful && response.body() != null) {
                        val score = response.body()!!.get("score")?.asInt ?: 0
                        tvResult.text = "Your ATS Score: $score / 100"
                    }
                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(this@AtsScoreActivity, "Error", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
