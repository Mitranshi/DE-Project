package com.smartcampus.app.ui.recruiter

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.smartcampus.app.R
import com.smartcampus.app.ui.auth.LoginActivity
import com.smartcampus.app.utils.SessionManager
import com.google.android.material.button.MaterialButton

class RecruiterDashboardActivity : AppCompatActivity() {
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generic)
        session = SessionManager(this)
        findViewById<TextView>(R.id.tvPageTitle).text = "Recruiter Dashboard"

        val container = findViewById<LinearLayout>(R.id.layoutContent)

        container.addView(TextView(this).apply {
            text = "Welcome, ${session.userName}"
            textSize = 18f
            setTextColor(resources.getColor(R.color.text_primary, null))
            setPadding(0, 0, 0, 16)
        })

        container.addView(TextView(this).apply {
            text = "• Search candidates by skills\n• View student resumes\n• Track resume views (FR-36)\n• Manage job postings"
            textSize = 14f
            setTextColor(resources.getColor(R.color.text_secondary, null))
            setLineSpacing(8f, 1f)
            setPadding(0, 0, 0, 24)
        })

        val btnLogout = MaterialButton(this).apply {
            text = "Logout"
            setBackgroundColor(resources.getColor(R.color.error, null))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 140
            ).apply { topMargin = 16 }
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
