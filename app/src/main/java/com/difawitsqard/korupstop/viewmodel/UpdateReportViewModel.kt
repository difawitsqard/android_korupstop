package com.difawitsqard.korupstop.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.difawitsqard.korupstop.model.ReportModel
import com.google.firebase.firestore.FirebaseFirestore

class UpdateReportViewModel : ViewModel() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _report = MutableLiveData<ReportModel>()
    val report: LiveData<ReportModel> get() = _report

    private val _updateResult = MutableLiveData<Boolean>()
    val updateResult: LiveData<Boolean> get() = _updateResult

    fun fetchReportDetails(id: String) {
        firestore.collection("reports").document(id).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    _report.value = document.toObject(ReportModel::class.java)
                } else {
                    _report.value = null
                }
            }
            .addOnFailureListener { exception ->
                _report.value = null
                Log.e("UpdateReportViewModel", "Failed to fetch report: ${exception.message}")
            }
    }

    fun updateReport(report: ReportModel) {
        firestore.collection("reports").document(report.id).set(report)
            .addOnSuccessListener {
                _updateResult.value = true
            }
            .addOnFailureListener { exception ->
                _updateResult.value = false
                Log.e("UpdateReportViewModel", "Failed to update report: ${exception.message}")
            }
    }
}
