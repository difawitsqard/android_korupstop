package com.difawitsqard.korupstop.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.difawitsqard.korupstop.model.ReportModel
import com.google.firebase.firestore.FirebaseFirestore

class ReportRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun getReports(): LiveData<List<ReportModel>> {
        val reportsLiveData = MutableLiveData<List<ReportModel>>()

        firestore.collection("reports")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("ReportRepository", "Error fetching reports", e)
                    reportsLiveData.value = emptyList()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val reports = snapshot.documents.mapNotNull { document ->
                        document.toObject(ReportModel::class.java)
                    }
                    Log.d("ReportRepository", "Fetched reports: $reports")
                    reportsLiveData.value = reports
                }
            }

        return reportsLiveData
    }

    fun submitReport(report: ReportModel): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()

        val documentReference = firestore.collection("reports").add(report)

        documentReference.addOnSuccessListener { documentRef ->
            val updatedReport = report.copy(id = documentRef.id)

            documentRef.set(updatedReport)
                .addOnSuccessListener {
                    result.value = true
                }
                .addOnFailureListener {
                    result.value = false
                }
        }
            .addOnFailureListener {
                result.value = false
            }

        return result
    }

    fun deleteReport(reportId: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()

        firestore.collection("reports").document(reportId)
            .delete()
            .addOnSuccessListener { result.value = true }
            .addOnFailureListener { result.value = false }

        return result
    }
}
