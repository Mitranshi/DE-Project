package com.smartcampus.app.ui.jobs

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.smartcampus.app.R
import com.smartcampus.app.api.ApiClient
import com.smartcampus.app.models.Job
import com.smartcampus.app.utils.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JobDetailActivity : AppCompatActivity() {
    private lateinit var session: SessionManager
    private var jobId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_detail)
        session = SessionManager(this)
        jobId = intent.getIntExtra("jobId", 0)
        loadJobDetail()
    }

    private fun loadJobDetail() {
        ApiClient.getApi().getJobById(session.authToken, jobId).enqueue(object : Callback<Job> {
            override fun onResponse(call: Call<Job>, response: Response<Job>) {
                if (response.isSuccessful && response.body() != null) {
                    val job = response.body()!!
                    findViewById<TextView>(R.id.tvTitle).text = job.title
                    findViewById<TextView>(R.id.tvCompany).text = job.companyName
                    findViewById<TextView>(R.id.tvLocation).text = "📍 ${job.location}"
                    findViewById<TextView>(R.id.tvSalary).text = "💰 ${job.salaryPackage}"
                    findViewById<TextView>(R.id.tvType).text = job.jobType
                    findViewById<TextView>(R.id.tvDescription).text = job.description
                    findViewById<TextView>(R.id.tvSkills).text = job.requiredSkills?.joinToString(", ") ?: ""
                    findViewById<TextView>(R.id.tvDeadline).text = "Deadline: ${job.applicationDeadline ?: "N/A"}"
                }
            }
            override fun onFailure(call: Call<Job>, t: Throwable) {
                Toast.makeText(this@JobDetailActivity, "Error loading job", Toast.LENGTH_SHORT).show()
            }
        })

        findViewById<MaterialButton>(R.id.btnApply).setOnClickListener {
            ApiClient.getApi().applyForJob(session.authToken, jobId, null).enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@JobDetailActivity, "Applied successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@JobDetailActivity, "Already applied or error occurred", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(this@JobDetailActivity, "Network error", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
