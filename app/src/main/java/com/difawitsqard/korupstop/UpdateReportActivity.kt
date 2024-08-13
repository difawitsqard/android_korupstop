package com.difawitsqard.korupstop

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.difawitsqard.korupstop.model.ReportModel
import com.difawitsqard.korupstop.viewmodel.UpdateReportViewModel

class UpdateReportActivity : AppCompatActivity() {

    private lateinit var updateReportViewModel: UpdateReportViewModel
    private lateinit var categorySpinner: Spinner
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_report)

        updateReportViewModel = ViewModelProvider(this)[UpdateReportViewModel::class.java]

        val reportId = intent.getStringExtra("REPORT_ID") ?: ""
        val userId = intent.getStringExtra("USER_ID") ?: ""

        if (reportId.isNotEmpty()) {
            updateReportViewModel.fetchReportDetails(reportId)
        }

        val editTextTitle: EditText = findViewById(R.id.editTextTitle)
        val editTextDescription: EditText = findViewById(R.id.editTextDescription)
        val editTextReportedPerson: EditText = findViewById(R.id.editTextReportedPerson)
        val editTextLocation: EditText = findViewById(R.id.editTextLocation)
        val submitButton: Button = findViewById(R.id.buttonSubmitReport)
        categorySpinner = findViewById(R.id.spinnerCategory)
        progressBar = findViewById(R.id.progressBar)

        val categories = resources.getStringArray(R.array.categories_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        submitButton.setOnClickListener {
            val title = editTextTitle.text.toString().trim()
            val reportedPerson = editTextReportedPerson.text.toString().trim()
            val description = editTextDescription.text.toString().trim()
            val location = editTextLocation.text.toString().trim()
            val category = categorySpinner.selectedItem.toString()

            if (title.isEmpty() || description.isEmpty() || location.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedReport = ReportModel(
                id = reportId,
                userId = userId,
                reportedPerson = reportedPerson,
                title = title,
                description = description,
                category = category,
                location = location
            )

            progressBar.visibility = View.VISIBLE
            submitButton.isEnabled = false

            updateReportViewModel.updateReport(updatedReport)
        }

        updateReportViewModel.report.observe(this) { report ->
            if (report != null) {
                editTextTitle.setText(report.title)
                editTextReportedPerson.setText(report.reportedPerson)
                editTextDescription.setText(report.description)
                editTextLocation.setText(report.location)

                val categoryPosition = categories.indexOf(report.category)
                if (categoryPosition >= 0) {
                    categorySpinner.setSelection(categoryPosition)
                }
            }
        }

        updateReportViewModel.updateResult.observe(this) { success ->
            progressBar.visibility = View.GONE
            submitButton.isEnabled = true
            if (success) {
                Toast.makeText(this, "Report updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to update report", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
