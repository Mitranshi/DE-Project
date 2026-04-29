package com.smartcampus.app.ui.resume

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.smartcampus.app.R
import com.smartcampus.app.api.ApiClient
import com.smartcampus.app.utils.ResumePdfGenerator
import com.smartcampus.app.utils.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResumeBuilderActivity : AppCompatActivity() {
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resume_builder)
        session = SessionManager(this)

        val etFullName = findViewById<TextInputEditText>(R.id.etFullName)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPhone = findViewById<TextInputEditText>(R.id.etPhone)
        val etSummary = findViewById<TextInputEditText>(R.id.etSummary)
        val etEducation = findViewById<TextInputEditText>(R.id.etEducation)
        val etSkills = findViewById<TextInputEditText>(R.id.etSkills)
        val etExperience = findViewById<TextInputEditText>(R.id.etExperience)
        val etProjects = findViewById<TextInputEditText>(R.id.etProjects)
        
        val rgTemplates = findViewById<RadioGroup>(R.id.rgTemplates)
        val rbClassic = findViewById<RadioButton>(R.id.rbClassic)
        val rbModern = findViewById<RadioButton>(R.id.rbModern)
        val rbCreative = findViewById<RadioButton>(R.id.rbCreative)

        val btnSaveData = findViewById<MaterialButton>(R.id.btnSaveData)
        val btnCheckAts = findViewById<MaterialButton>(R.id.btnCheckAts)
        val btnGeneratePdf = findViewById<MaterialButton>(R.id.btnGeneratePdf)

        // Logic to determining selected template ID
        fun getSelectedTemplateId(): String {
            return when (rgTemplates.checkedRadioButtonId) {
                R.id.rbModern -> "modern"
                R.id.rbCreative -> "creative"
                else -> "classic"
            }
        }

        btnSaveData.setOnClickListener {
            // Save personal info locally
            val prefs = getSharedPreferences("ResumePrefs", MODE_PRIVATE)
            prefs.edit().apply {
                putString("fullName", etFullName.text?.toString())
                putString("email", etEmail.text?.toString())
                putString("phone", etPhone.text?.toString())
                apply()
            }

            val body = mapOf(
                "objective" to (etSummary.text?.toString() ?: ""),
                "education" to (etEducation.text?.toString() ?: ""),
                "skills" to (etSkills.text?.toString() ?: ""),
                "experience" to (etExperience.text?.toString() ?: ""),
                "projects" to (etProjects.text?.toString() ?: ""),
                "templateId" to getSelectedTemplateId()
            )
            ApiClient.getApi().saveResumeData(session.authToken, body).enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    Toast.makeText(this@ResumeBuilderActivity,
                        if (response.isSuccessful) "Resume saved!" else "Error saving",
                        Toast.LENGTH_SHORT).show()
                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(this@ResumeBuilderActivity, "Network error", Toast.LENGTH_SHORT).show()
                }
            })
        }

        btnCheckAts.setOnClickListener {
            startActivity(Intent(this, AtsScoreActivity::class.java))
        }

        btnGeneratePdf.setOnClickListener {
            val file = ResumePdfGenerator.generatePdf(
                this,
                getSelectedTemplateId(),
                etFullName.text?.toString() ?: "John Doe",
                etEmail.text?.toString() ?: "email@example.com",
                etPhone.text?.toString() ?: "",
                etSummary.text?.toString() ?: "",
                etEducation.text?.toString() ?: "",
                etSkills.text?.toString() ?: "",
                etExperience.text?.toString() ?: "",
                etProjects.text?.toString() ?: ""
            )

            if (file != null) {
                Toast.makeText(this, "PDF Generated in Downloads Folder!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Failed to generate PDF", Toast.LENGTH_SHORT).show()
            }
        }

        // Load personal info locally
        val prefs = getSharedPreferences("ResumePrefs", MODE_PRIVATE)
        etFullName.setText(prefs.getString("fullName", ""))
        etEmail.setText(prefs.getString("email", ""))
        etPhone.setText(prefs.getString("phone", ""))

        // Load existing data
        ApiClient.getApi().getResumeData(session.authToken).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    
                    fun safeGetString(key: String): String {
                        val elem = data.get(key)
                        return if (elem != null && !elem.isJsonNull) elem.asString else ""
                    }

                    etSummary.setText(safeGetString("objective"))
                    etEducation.setText(safeGetString("education"))
                    etSkills.setText(safeGetString("skills"))
                    etExperience.setText(safeGetString("experience"))
                    etProjects.setText(safeGetString("projects"))
                    
                    val template = safeGetString("templateId")
                    when (template) {
                        "modern" -> rbModern.isChecked = true
                        "creative" -> rbCreative.isChecked = true
                        else -> rbClassic.isChecked = true
                    }
                }
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {}
        })
    }
}
