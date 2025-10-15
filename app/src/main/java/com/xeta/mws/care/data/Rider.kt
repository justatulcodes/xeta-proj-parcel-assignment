package com.xeta.mws.care.data

data class Rider(
    val id: String,
    val name: String,
    val zone: String,
    val orders: Int,
    val status: RiderStatus,
    val area: String = ""
)

enum class RiderStatus {
    ONLINE,
    OFFLINE,
    BUSY
}

data class AvailableRider(
    val employeeId: String,
    val name: String,
    val area: String
)