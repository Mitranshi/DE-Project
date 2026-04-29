package com.smartcampus.app.ui.jobs

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smartcampus.app.R
import com.smartcampus.app.models.ExternalJob

class ExternalJobAdapter(private var jobs: List<ExternalJob>) : RecyclerView.Adapter<ExternalJobAdapter.JobViewHolder>() {

    class JobViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvJobTitle: TextView = view.findViewById(R.id.tvJobTitle)
        val tvCompany: TextView = view.findViewById(R.id.tvCompany)
        val tvLocation: TextView = view.findViewById(R.id.tvLocation)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val tvJobType: TextView = view.findViewById(R.id.tvJobType)
        val tvPostedAt: TextView = view.findViewById(R.id.tvPostedAt)
        val tvSource: TextView = view.findViewById(R.id.tvSource)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_external_job, parent, false)
        return JobViewHolder(view)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = jobs[position]
        holder.tvJobTitle.text = job.title
        holder.tvCompany.text = job.companyName
        holder.tvLocation.text = job.location
        holder.tvDescription.text = job.description
        holder.tvJobType.text = job.jobType ?: "Full Time"
        holder.tvPostedAt.text = formatPostedDate(job.postedAt)
        holder.tvSource.text = "via ${job.apiSource}"

        holder.itemView.setOnClickListener {
            job.applyLink?.let { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                holder.itemView.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount() = jobs.size

    fun updateJobs(newJobs: List<ExternalJob>) {
        jobs = newJobs
        notifyDataSetChanged()
    }

    private fun formatPostedDate(date: String?): String {
        if (date == null) return "Recently"
        // Simple formatting or return as is for now
        return try {
            date.substring(0, 10) // Return YYYY-MM-DD
        } catch (e: Exception) {
            "Recently"
        }
    }
}
