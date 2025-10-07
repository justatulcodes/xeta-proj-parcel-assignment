package com.xeta.mws.care.data

data class ParcelInfo(
    val id: String,
    val deliveryAddress: String,
    val zone: String,
    val sector: String,
    val availableRiders: List<Rider> = emptyList(),
    val assignedRider: Rider? = null
)