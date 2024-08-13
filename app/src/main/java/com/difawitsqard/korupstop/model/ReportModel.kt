package com.difawitsqard.korupstop.model

data class ReportModel(
    var id: String = "",
    val userId: String? = null,
    val title: String = "",
    val reportedPerson: String = "",
    val description: String = "",
    val category: String = "",
    val location: String = ""
)
