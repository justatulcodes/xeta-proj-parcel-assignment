package com.xeta.mws.care.data.network

data class SummaryResponse(
    val overall_summary: OverallSummary,
    val per_employee_summary: List<PerEmployeeSummary>
)

data class OverallSummary(
    val total_parcels: Int,
    val total_riders: Int,
    val average_parcels_per_rider: Double,
    val total_regions: Int
)

data class PerEmployeeSummary(
    val full_name: String,
    val designation: String,
    val parcels_assigned: Int
)

