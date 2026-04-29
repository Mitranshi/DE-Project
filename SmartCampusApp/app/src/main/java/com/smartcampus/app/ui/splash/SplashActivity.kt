package com.smartcampus.app.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.smartcampus.app.R
import com.smartcampus.app.ui.auth.LoginActivity
import com.smartcampus.app.ui.student.StudentDashboardActivity
import com.smartcampus.app.ui.officer.OfficerDashboardActivity
import com.smartcampus.app.ui.recruiter.RecruiterDashboardActivity
import com.smartcampus.app.ui.admin.AdminDashboardActivity
import com.smartcampus.app.utils.SessionManager

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val session = SessionManager(this)
            if (session.isLoggedIn) {
                navigateToDashboard(session.userRole)
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, 2000)
    }

    private fun navigateToDashboard(role: String) {
        val intent = when (role) {
            "PLACEMENT_OFFICER" -> Intent(this, OfficerDashboardActivity::class.java)
            "RECRUITER" -> Intent(this, RecruiterDashboardActivity::class.java)
            "ADMIN" -> Intent(this, AdminDashboardActivity::class.java)
            else -> Intent(this, StudentDashboardActivity::class.java)
        }
        startActivity(intent)
    }
}
