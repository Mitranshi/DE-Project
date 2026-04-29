package com.smartcampus.app.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.smartcampus.app.R
import com.smartcampus.app.api.ApiClient
import com.smartcampus.app.ui.auth.LoginActivity
import com.smartcampus.app.utils.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminDashboardActivity : AppCompatActivity() {
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generic)
        session = SessionManager(this)
        findViewById<TextView>(R.id.tvPageTitle).text = "Admin Dashboard"

        val container = findViewById<LinearLayout>(R.id.layoutContent)

        // System stats
        ApiClient.getApi().getSystemStats(session.authToken).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful && response.body() != null) {
                    val stats = response.body()!!
                    val statsText = buildString {
                        appendLine("📊 System Statistics")
                        appendLine("━━━━━━━━━━━━━━━━")
                        appendLine("Total Users: ${stats.get("totalUsers")?.asInt ?: 0}")
                        appendLine("Students: ${stats.get("totalStudents")?.asInt ?: 0}")
                        appendLine("Recruiters: ${stats.get("totalRecruiters")?.asInt ?: 0}")
                        appendLine("Active Jobs: ${stats.get("totalJobs")?.asInt ?: 0}")
                        appendLine("Applications: ${stats.get("totalApplications")?.asInt ?: 0}")
                    }
                    container.addView(TextView(this@AdminDashboardActivity).apply {
                        text = statsText
                        textSize = 15f
                        setTextColor(resources.getColor(R.color.text_primary, null))
                        setLineSpacing(8f, 1f)
                        setPadding(0, 0, 0, 24)
                    })
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {}
        })

        // Pending recruiters
        ApiClient.getApi().getPendingRecruiters(session.authToken).enqueue(object : Callback<List<JsonObject>> {
            override fun onResponse(call: Call<List<JsonObject>>, response: Response<List<JsonObject>>) {
                if (response.isSuccessful && response.body() != null) {
                    container.addView(TextView(this@AdminDashboardActivity).apply {
                        text = "Pending Recruiter Approvals: ${response.body()!!.size}"
                        textSize = 16f
                        setTextColor(resources.getColor(R.color.warning, null))
                        setPadding(0, 0, 0, 16)
                        setTypeface(typeface, android.graphics.Typeface.BOLD)
                    })
                    response.body()!!.forEach { rec ->
                        container.addView(TextView(this@AdminDashboardActivity).apply {
                            text = "• ${rec.get("name")?.asString ?: ""} (${rec.get("email")?.asString ?: ""})"
                            textSize = 14f
                            setTextColor(resources.getColor(R.color.text_secondary, null))
                            setPadding(0, 4, 0, 4)
                        })
                    }
                }
            }
            override fun onFailure(call: Call<List<JsonObject>>, t: Throwable) {}
        })

        val btnLogout = MaterialButton(this).apply {
            text = "Logout"
            setBackgroundColor(resources.getColor(R.color.error, null))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 140
            ).apply { topMargin = 32 }
        }
        btnLogout.setOnClickListener {
            session.logout()
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
        container.addView(btnLogout)
    }
}
