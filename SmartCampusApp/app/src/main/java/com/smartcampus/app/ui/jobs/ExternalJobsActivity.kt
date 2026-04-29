package com.smartcampus.app.ui.jobs

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smartcampus.app.R
import com.smartcampus.app.api.ApiClient
import com.smartcampus.app.models.ExternalJob
import com.smartcampus.app.utils.SessionManager
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExternalJobsActivity : AppCompatActivity() {

    private lateinit var session: SessionManager
    private lateinit var adapter: ExternalJobAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmptyState: TextView
    private lateinit var etSearch: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_external_jobs)
        
        session = SessionManager(this)
        
        setupUI()
        searchJobs("internship") // Default search
    }

    private fun setupUI() {
        val rvJobs = findViewById<RecyclerView>(R.id.rvExternalJobs)
        progressBar = findViewById(R.id.progressBar)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        etSearch = findViewById(R.id.etSearch)
        val btnSearch = findViewById<MaterialButton>(R.id.btnSearch)

        adapter = ExternalJobAdapter(emptyList())
        rvJobs.layoutManager = LinearLayoutManager(this)
        rvJobs.adapter = adapter

        btnSearch.setOnClickListener {
            val query = etSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                searchJobs(query)
            }
        }
    }

    private fun searchJobs(query: String) {
        progressBar.visibility = View.VISIBLE
        tvEmptyState.visibility = View.GONE
        
        ApiClient.getApi().getExternalJobs("Bearer ${session.authToken}", query, "1")
            .enqueue(object : Callback<List<ExternalJob>> {
                override fun onResponse(call: Call<List<ExternalJob>>, response: Response<List<ExternalJob>>) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful && response.body() != null) {
                        val jobs = response.body()!!
                        if (jobs.isEmpty()) {
                            tvEmptyState.visibility = View.VISIBLE
                            tvEmptyState.text = "No jobs found for '$query'"
                        } else {
                            adapter.updateJobs(jobs)
                        }
                    } else {
                        Toast.makeText(this@ExternalJobsActivity, "Failed to load jobs", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<ExternalJob>>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@ExternalJobsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
