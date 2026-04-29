package com.smartcampus.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.smartcampus.app.R
import com.smartcampus.app.api.ApiClient
import com.smartcampus.app.models.User
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var etName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etEnrollmentId: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var chipGroupRole: ChipGroup
    private lateinit var btnRegister: MaterialButton
    private lateinit var progressBar: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etEnrollmentId = findViewById(R.id.etEnrollmentId)
        etPassword = findViewById(R.id.etPassword)
        chipGroupRole = findViewById(R.id.chipGroupRole)
        btnRegister = findViewById(R.id.btnRegister)
        progressBar = findViewById(R.id.progressBar)

        btnRegister.setOnClickListener { performRegister() }

        findViewById<View>(R.id.tvLogin).setOnClickListener {
            finish()
        }
    }

    private fun getSelectedRole(): String {
        val chipId = chipGroupRole.checkedChipId
        return when (chipId) {
            R.id.chipRecruiter -> "RECRUITER"
            R.id.chipOfficer -> "PLACEMENT_OFFICER"
            else -> "STUDENT"
        }
    }

    private fun performRegister() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val enrollmentId = etEnrollmentId.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val role = getSelectedRole()

        if (name.isEmpty() || email.isEmpty() || enrollmentId.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }

        btnRegister.isEnabled = false
        progressBar.visibility = View.VISIBLE

        val body = mapOf(
            "name" to name,
            "email" to email,
            "enrollmentId" to enrollmentId,
            "password" to password,
            "role" to role
        )

        ApiClient.getApi().register(body).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                progressBar.visibility = View.GONE
                btnRegister.isEnabled = true

                if (response.isSuccessful) {
                    val msg = if (role == "RECRUITER")
                        "Registration successful! Awaiting admin approval."
                    else
                        "Registration successful! Please login."
                    Toast.makeText(this@RegisterActivity, msg, Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@RegisterActivity, "Registration failed. Email may already exist.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                progressBar.visibility = View.GONE
                btnRegister.isEnabled = true
                Toast.makeText(this@RegisterActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
