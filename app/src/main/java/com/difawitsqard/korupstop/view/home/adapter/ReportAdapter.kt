package com.difawitsqard.korupstop.view.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.difawitsqard.korupstop.databinding.ItemReportBinding
import com.difawitsqard.korupstop.utils.FirebaseAuthInstance
import com.difawitsqard.korupstop.model.ReportModel

class ReportAdapter(
    private val reports: MutableList<ReportModel>,
    private val onDeleteClicked: (ReportModel) -> Unit,
    private val onUpdateClicked: (ReportModel) -> Unit
) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    val currentUser = FirebaseAuthInstance.getCurrentUser()

    inner class ReportViewHolder(private val binding: ItemReportBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(report: ReportModel) {
            binding.textViewTitle.text = report.title
            binding.textViewReportedPerson.text = report.reportedPerson
            binding.textViewDescription.text = report.description
            binding.textViewCategory.text = report.category
            binding.textViewLocation.text = report.location

            // Cek jika currentUserId sama dengan userId di report item
            if (report.userId == currentUser?.uid) {
                binding.imageViewDelete.visibility = View.VISIBLE
                binding.imageViewUpdate.visibility = View.VISIBLE

                binding.imageViewDelete.setOnClickListener {
                    onDeleteClicked(report)
                }

                binding.imageViewUpdate.setOnClickListener {
                    onUpdateClicked(report)
                }
            } else {
                binding.imageViewDelete.visibility = View.GONE
                binding.imageViewUpdate.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ItemReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reports[position]
        holder.bind(report)
    }

    override fun getItemCount(): Int = reports.size

    fun updateReports(newReports: List<ReportModel>) {
        reports.clear()
        reports.addAll(newReports)
        notifyDataSetChanged()
    }
}

