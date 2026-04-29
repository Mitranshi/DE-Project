package com.smartcampus.app.ui.student

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.smartcampus.app.R
import com.smartcampus.app.api.ApiClient
import com.smartcampus.app.models.SkillRecommendation
import com.smartcampus.app.utils.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SkillRecommendationActivity : AppCompatActivity() {

    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_skill_recommendation)
        session = SessionManager(this)

        // Map button
        findViewById<MaterialButton>(R.id.btnViewMap).setOnClickListener {
            startActivity(Intent(this, SkillMapActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.btnExternalJobs).setOnClickListener {
            startActivity(Intent(this, com.smartcampus.app.ui.jobs.ExternalJobsActivity::class.java))
        }

        loadCompletedRoadmaps()
    }

    private fun loadCompletedRoadmaps() {
        ApiClient.getApi().getCompletedRoadmaps(session.authToken).enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                val completed = if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
                loadRecommendations(completed)
                loadTrending(completed)
            }
            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                loadRecommendations(emptyList())
                loadTrending(emptyList())
            }
        })
    }

    private fun loadRecommendations(completedSkills: List<String>) {
        ApiClient.getApi().getPersonalizedRecommendations(session.authToken).enqueue(object : Callback<List<SkillRecommendation>> {
            override fun onResponse(call: Call<List<SkillRecommendation>>, response: Response<List<SkillRecommendation>>) {
                if (response.isSuccessful && response.body() != null) {
                    displaySkills(response.body()!!, findViewById(R.id.layoutRecommended), completedSkills)
                }
            }
            override fun onFailure(call: Call<List<SkillRecommendation>>, t: Throwable) {
                Toast.makeText(this@SkillRecommendationActivity, "Error loading recommendations", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadTrending(completedSkills: List<String>) {
        ApiClient.getApi().getTrendingSkills(session.authToken).enqueue(object : Callback<List<SkillRecommendation>> {
            override fun onResponse(call: Call<List<SkillRecommendation>>, response: Response<List<SkillRecommendation>>) {
                if (response.isSuccessful && response.body() != null) {
                    displaySkills(response.body()!!, findViewById(R.id.layoutTrending), completedSkills)
                }
            }
            override fun onFailure(call: Call<List<SkillRecommendation>>, t: Throwable) {}
        })
    }

    private fun displaySkills(skills: List<SkillRecommendation>, container: LinearLayout, completedSkills: List<String>) {
        container.removeAllViews()
        skills.forEach { skill ->
            val card = LayoutInflater.from(this).inflate(R.layout.item_skill_recommendation, container, false)
            
            val isCompleted = completedSkills.any { it.equals(skill.skillName, ignoreCase = true) }
            val tvSkillName = card.findViewById<TextView>(R.id.tvSkillName)
            tvSkillName.text = skill.skillName
            
            if (isCompleted) {
                tvSkillName.paintFlags = tvSkillName.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                card.findViewById<TextView>(R.id.tvDemandScore).text = "${skill.demandScore.toInt()}% demand (Completed ✔)"
                val progress = card.findViewById<LinearProgressIndicator>(R.id.progressDemand)
                progress.progress = skill.demandScore.toInt()
                progress.setIndicatorColor(resources.getColor(R.color.success, null))
            } else {
                card.findViewById<TextView>(R.id.tvDemandScore).text = "${skill.demandScore.toInt()}% demand"
                card.findViewById<LinearProgressIndicator>(R.id.progressDemand).progress = skill.demandScore.toInt()
            }
            
            card.findViewById<TextView>(R.id.tvReason).text = skill.reason ?: skill.region ?: ""
            
            card.setOnClickListener {
                startActivity(Intent(this, SkillRoadmapActivity::class.java).apply {
                    putExtra("SKILL_NAME", skill.skillName)
                })
            }
            container.addView(card)
        }
    }
}
