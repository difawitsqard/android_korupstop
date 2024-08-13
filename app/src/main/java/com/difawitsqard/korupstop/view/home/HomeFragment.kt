package com.difawitsqard.korupstop.view.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.difawitsqard.korupstop.UpdateEducationActivity
import com.difawitsqard.korupstop.UpdateReportActivity
import com.difawitsqard.korupstop.databinding.FragmentHomeBinding
import com.difawitsqard.korupstop.utils.FirebaseAuthInstance
import com.difawitsqard.korupstop.view.home.adapter.EducationAdapter
import com.difawitsqard.korupstop.view.home.adapter.ReportAdapter
import com.difawitsqard.korupstop.viewmodel.EducationViewModel
import com.difawitsqard.korupstop.viewmodel.ReportViewModel

class HomeFragment : Fragment() {

    private lateinit var eduViewModel: EducationViewModel
    private lateinit var reportViewModel: ReportViewModel

    private lateinit var reportAdapter: ReportAdapter
    private lateinit var educationAdapter: EducationAdapter
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val currentUser = FirebaseAuthInstance.getCurrentUser()

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.WelcomeMsg.text = "Selamat datang, ${currentUser?.displayName}!"

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eduViewModel = ViewModelProvider(this)[EducationViewModel::class.java]
        reportViewModel = ViewModelProvider(this)[ReportViewModel::class.java]

        reportAdapter = ReportAdapter(
            reports = mutableListOf(),
            onDeleteClicked = { report ->
                reportViewModel.deleteReport(report.id)
            },
            onUpdateClicked = { report ->
                Log.d("EducationAdapter", "Deleting item with ID: ${report.id}")
                val intent = Intent(context, UpdateReportActivity::class.java)
                intent.putExtra("REPORT_ID", report.id)
                intent.putExtra("USER_ID", report.userId)
                context?.startActivity(intent)
            }
        )

        binding.recyclerViewReports.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = reportAdapter
        }

        educationAdapter = EducationAdapter(
            educationList = emptyList(),
            onDeleteClick = { education ->
                eduViewModel.deleteEducation(education.id)
                Log.d("EducationAdapter", "Deleting item with ID: ${education.id}")
            },
            onUpdateClick = { education ->
                val intent = Intent(context, UpdateEducationActivity::class.java)
                intent.putExtra("EDUCATION_ID", education.id)
                intent.putExtra("USER_ID", education.userId)
                context?.startActivity(intent)
            }
        )

        binding.recyclerViewEducations.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = educationAdapter
        }

        reportViewModel.reports.observe(viewLifecycleOwner) { reports ->
            reports?.let {
                reportAdapter.updateReports(it)
            }
        }

        reportViewModel.deletionResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(context, "Report deleted successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to delete report", Toast.LENGTH_SHORT).show()
            }
        }

        eduViewModel.educationContent.observe(viewLifecycleOwner) { educations ->
            educations?.let {
                educationAdapter.submitList(it)
            }
        }

        eduViewModel.submissionEdu.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(context, "Education item deleted successfully", Toast.LENGTH_SHORT)
                    .show()
                eduViewModel.fetchEducationContent() // Refresh the education content after deletion
            } else {
                Toast.makeText(context, "Failed to delete education item", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        eduViewModel.fetchEducationContent()
    }
}
