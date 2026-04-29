package com.smartcampus.app.ui.officer

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

class OfficerDashboardActivity : AppCompatActivity() {
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generic)
        session = SessionManager(this)
        findViewById<TextView>(R.id.tvPageTitle).text = "Placement Officer Dashboard"

        val container = findViewById<LinearLayout>(R.id.layoutContent)

        // Drives list
        ApiClient.getApi().getDrives(session.authToken).enqueue(object : Callback<List<JsonObject>> {
            override fun onResponse(call: Call<List<JsonObject>>, response: Response<List<JsonObject>>) {
                if (response.isSuccessful && response.body() != null) {
                    container.addView(TextView(this@OfficerDashboardActivity).apply {
                        text = "Active Drives: ${response.body()!!.size}"
                        textSize = 18f
                        setTextColor(resources.getColor(R.color.accent, null))
                        setPadding(0, 0, 0, 24)
                    })
                    response.body()!!.forEach { drive ->
                        val card = MaterialCardView(this@OfficerDashboardActivity).apply {
                            setCardBackgroundColor(resources.getColor(R.color.bg_card, null))
                            radius = 16f
                            setContentPadding(24, 24, 24, 24)
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            ).apply { bottomMargin = 12 }
                        }
                        val inner = LinearLayout(this@OfficerDashboardActivity).apply { orientation = LinearLayout.VERTICAL }
                        inner.addView(TextView(this@OfficerDashboardActivity).apply {
                            text = drive.get("companyName")?.asString ?: "Drive"
                            textSize = 16f
                            setTextColor(resources.getColor(R.color.text_primary, null))
                            setTypeface(typeface, android.graphics.Typeface.BOLD)
                        })
                        inner.addView(TextView(this@OfficerDashboardActivity).apply {
                            text = "Date: ${drive.get("driveDate")?.asString ?: "TBD"}"
                            textSize = 13f
                            setTextColor(resources.getColor(R.color.text_secondary, null))
                        })
                        card.addView(inner)
                        container.addView(card)
                    }
                }
            }
            override fun onFailure(call: Call<List<JsonObject>>, t: Throwable) {}
        })

        // Logout button
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
