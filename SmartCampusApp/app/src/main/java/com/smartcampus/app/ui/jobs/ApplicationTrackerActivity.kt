package com.smartcampus.app.ui.jobs

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.smartcampus.app.R
import com.smartcampus.app.api.ApiClient
import com.smartcampus.app.utils.SessionManager
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApplicationTrackerActivity : AppCompatActivity() {
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generic)
        session = SessionManager(this)
        findViewById<TextView>(R.id.tvPageTitle).text = "My Applications"
        loadApplications()
    }

    private fun loadApplications() {
        ApiClient.getApi().getMyApplications(session.authToken).enqueue(object : Callback<List<JsonObject>> {
            override fun onResponse(call: Call<List<JsonObject>>, response: Response<List<JsonObject>>) {
                if (response.isSuccessful && response.body() != null) {
                    val container = findViewById<LinearLayout>(R.id.layoutContent)
                    response.body()!!.forEach { app ->
                        val tv = TextView(this@ApplicationTrackerActivity).apply {
                            text = "📋 ${app.get("jobTitle")?.asString ?: "Job"} — ${app.get("status")?.asString ?: "PENDING"}"
                            textSize = 15f
                            setTextColor(resources.getColor(R.color.text_primary, null))
                            setPadding(0, 12, 0, 12)
                        }
                        container.addView(tv)
                    }
                }
            }
            override fun onFailure(call: Call<List<JsonObject>>, t: Throwable) {
                Toast.makeText(this@ApplicationTrackerActivity, "Error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
