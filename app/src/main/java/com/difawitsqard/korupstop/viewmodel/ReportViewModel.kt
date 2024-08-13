package com.difawitsqard.korupstop.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.difawitsqard.korupstop.repository.ReportRepository
import com.difawitsqard.korupstop.model.ReportModel

class ReportViewModel : ViewModel() {

    private val repository = ReportRepository()

    private val _submissionResult = MutableLiveData<Boolean>()
    val submissionResult: LiveData<Boolean> get() = _submissionResult

    private val _deletionResult = MutableLiveData<Boolean>()
    val deletionResult: LiveData<Boolean> get() = _deletionResult

    private val _reports = MutableLiveData<List<ReportModel>>()
    val reports: LiveData<List<ReportModel>> get() = _reports

    init {
        fetchReports()
    }

    private fun fetchReports() {
        repository.getReports().observeForever { fetchedReports ->
            _reports.value = fetchedReports
        }
    }

    fun submitReport(report: ReportModel) {
        Log.d("ReportViewModel", "Submitting report: $report")
        repository.submitReport(report).observeForever { result ->
            _submissionResult.value = result
        }
    }

    fun deleteReport(reportId: String) {
        repository.deleteReport(reportId).observeForever { result ->
            if (result) {
                fetchReports()
            }
            _deletionResult.value = result
        }
    }
}
