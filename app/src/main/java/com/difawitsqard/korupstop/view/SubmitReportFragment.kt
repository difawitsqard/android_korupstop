package com.difawitsqard.korupstop.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.difawitsqard.korupstop.R
import com.difawitsqard.korupstop.utils.FirebaseAuthInstance
import com.difawitsqard.korupstop.model.ReportModel
import com.difawitsqard.korupstop.view.home.HomeFragment
import com.difawitsqard.korupstop.viewmodel.ReportViewModel

class SubmitReportFragment : Fragment() {

    private lateinit var viewModel: ReportViewModel
    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var editTextReportedPerson: EditText
    private lateinit var editTextLocation: EditText
    private lateinit var buttonSubmitReport: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_submit_report, container, false)
        val currentUser = FirebaseAuthInstance.getCurrentUser()

        viewModel = ViewModelProvider(this).get(ReportViewModel::class.java)
        editTextTitle = view.findViewById(R.id.editTextTitle)
        editTextDescription = view.findViewById(R.id.editTextDescription)
        spinnerCategory = view.findViewById(R.id.spinnerCategory)
        editTextReportedPerson = view.findViewById(R.id.editTextReportedPerson)
        editTextLocation = view.findViewById(R.id.editTextLocation)
        buttonSubmitReport = view.findViewById(R.id.buttonSubmitReport)
        progressBar = view.findViewById(R.id.progressBar)

        val categories = resources.getStringArray(R.array.categories_array)
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        buttonSubmitReport.setOnClickListener {
            val id = ""
            val title = editTextTitle.text.toString().trim()
            val description = editTextDescription.text.toString().trim()
            val category = spinnerCategory.selectedItem.toString()
            val reportedPerson = editTextReportedPerson.text.toString().trim()
            val location = editTextLocation.text.toString().trim()

            if (title.isEmpty() || description.isEmpty() || category.isEmpty() || location.isEmpty()) {
                Toast.makeText(context, "Please fill all required fields.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val report = ReportModel(id, currentUser?.uid, title, reportedPerson, description, category, location)

            progressBar.visibility = View.VISIBLE

            viewModel.submitReport(report)
        }

        viewModel.submissionResult.observe(viewLifecycleOwner) { result ->
            progressBar.visibility = View.GONE

            result?.let {
                if (it) {
                    Toast.makeText(context, "Report submitted successfully!", Toast.LENGTH_SHORT)
                        .show()
                    (activity as? AppCompatActivity)?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.fragment_container, HomeFragment())
                        ?.addToBackStack(null) // Optional
                        ?.commit()
                } else {
                    Toast.makeText(context, "Failed to submit report.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }
}
