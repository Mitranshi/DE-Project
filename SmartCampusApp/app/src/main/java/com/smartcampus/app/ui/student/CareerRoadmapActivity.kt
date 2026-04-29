package com.smartcampus.app.ui.student

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.smartcampus.app.R
import com.smartcampus.app.api.ApiClient
import com.smartcampus.app.utils.SessionManager
import com.google.android.material.card.MaterialCardView
import com.google.gson.JsonObject
import com.google.gson.JsonArray
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CareerRoadmapActivity : AppCompatActivity() {
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generic)
        session = SessionManager(this)
        findViewById<TextView>(R.id.tvPageTitle).text = "Career Roadmap"
        loadCompletedRoadmaps()
    }

    private fun loadCompletedRoadmaps() {
        ApiClient.getApi().getCompletedRoadmaps(session.authToken).enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                val completed = if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
                loadRoadmap(completed)
            }
            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                loadRoadmap(emptyList())
            }
        })
    }

    private fun loadRoadmap(completedSkills: List<String>) {
        ApiClient.getApi().getCareerRoadmap(session.authToken).enqueue(object : Callback<List<JsonObject>> {
            override fun onResponse(call: Call<List<JsonObject>>, response: Response<List<JsonObject>>) {
                if (response.isSuccessful && response.body() != null) {
                    val container = findViewById<LinearLayout>(R.id.layoutContent)
                    container.removeAllViews() // Clear existing loading state or items
                    
                    response.body()!!.forEach { milestone ->
                        val view = LayoutInflater.from(this@CareerRoadmapActivity)
                            .inflate(R.layout.item_roadmap_semester, container, false)
                        
                        val tvSemesterTitle = view.findViewById<TextView>(R.id.tvSemesterTitle)
                        val layoutHeader = view.findViewById<LinearLayout>(R.id.layoutHeader)
                        val ivExpandIndicator = view.findViewById<android.widget.ImageView>(R.id.ivExpandIndicator)
                        val layoutExpandableContent = view.findViewById<LinearLayout>(R.id.layoutExpandableContent)

                        var isExpanded = false // Collapsed by default
                        layoutHeader.setOnClickListener {
                            isExpanded = !isExpanded
                            layoutExpandableContent.visibility = if (isExpanded) android.view.View.VISIBLE else android.view.View.GONE
                            ivExpandIndicator.setImageResource(
                                if (isExpanded) android.R.drawable.arrow_up_float else android.R.drawable.arrow_down_float
                            )
                        }
                        val cgSkills = view.findViewById<ChipGroup>(R.id.cgSkills)
                        val tvInternships = view.findViewById<TextView>(R.id.tvInternships)
                        val tvCertifications = view.findViewById<TextView>(R.id.tvCertifications)
                        
                        val semester = milestone.get("semester")?.asInt ?: 1
                        tvSemesterTitle.text = "Semester $semester"

                        val skillsArray = milestone.getAsJsonArray("requiredSkills")
                        if (skillsArray != null && skillsArray.size() > 0) {
                            for (i in 0 until skillsArray.size()) {
                                val skill = skillsArray.get(i).asString
                                val chip = Chip(this@CareerRoadmapActivity).apply {
                                    text = skill
                                    val isCompleted = completedSkills.any { it.equals(skill, ignoreCase = true) }
                                    
                                    if (isCompleted) {
                                        setChipBackgroundColorResource(R.color.success)
                                        setTextColor(resources.getColor(R.color.bg_dark, null))
                                        paintFlags = paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                                    } else {
                                        setChipBackgroundColorResource(R.color.bg_card_light)
                                        setTextColor(resources.getColor(R.color.accent, null))
                                    }
                                    
                                    setEnsureMinTouchTargetSize(false) // Tighter chip spacing
                                    
                                    // Navigate to specific skill roadmap on click
                                    isClickable = true
                                    setOnClickListener {
                                        val intent = android.content.Intent(this@CareerRoadmapActivity, SkillRoadmapActivity::class.java)
                                        intent.putExtra("SKILL_NAME", skill)
                                        startActivity(intent)
                                    }
                                }
                                cgSkills.addView(chip)
                            }
                        } else {
                            cgSkills.visibility = android.view.View.GONE
                        }

                        val internshipsArray = milestone.getAsJsonArray("suggestedInternships")
                        if (internshipsArray != null && internshipsArray.size() > 0) {
                            val sb = java.lang.StringBuilder()
                            for (i in 0 until internshipsArray.size()) {
                                sb.append("• ${internshipsArray.get(i).asString}\n")
                            }
                            tvInternships.text = sb.toString().trim()
                        } else {
                            view.findViewById<LinearLayout>(R.id.layoutInternships).visibility = android.view.View.GONE
                        }
                        
                        val certsArray = milestone.getAsJsonArray("suggestedCertifications")
                        if (certsArray != null && certsArray.size() > 0) {
                            val sb = java.lang.StringBuilder()
                            for (i in 0 until certsArray.size()) {
                                sb.append("• ${certsArray.get(i).asString}\n")
                            }
                            tvCertifications.text = sb.toString().trim()
                        } else {
                            view.findViewById<LinearLayout>(R.id.layoutCertifications).visibility = android.view.View.GONE
                        }

                        container.addView(view)
                    }
                }
            }
            override fun onFailure(call: Call<List<JsonObject>>, t: Throwable) {
                Toast.makeText(this@CareerRoadmapActivity, "Error loading roadmap", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
