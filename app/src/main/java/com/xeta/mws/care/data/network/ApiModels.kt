package com.xeta.mws.care.data.network

import com.google.gson.annotations.SerializedName

// OCR Address API models
data class OcrAddressRequest(
    @SerializedName("ocr_text") val ocrText: String,
    @SerializedName("raw_image_meta") val rawImageMeta: RawImageMeta
)

data class RawImageMeta(
    val device: String,
    val timestamp: String
)

data class OcrAddressResponse(
    @SerializedName("ocr_text") val ocrText: String,
    val candidates: List<AddressCandidate>
)

data class AddressCandidate(
    @SerializedName("mapbox_feature") val mapboxFeature: MapboxFeature,
    val coordinates: List<Double>,
    @SerializedName("candidate_employees") val candidateEmployees: List<String>
)

data class MapboxFeature(
    val id: String,
    val type: String,
    @SerializedName("place_type") val placeType: List<String>,
    val relevance: Double,
    val properties: Properties,
    val text: String,
    @SerializedName("place_name") val placeName: String,
    val center: List<Double>,
    val geometry: Geometry,
    val address: String? = null,
    val context: List<MapboxContext>? = null
)

data class Properties(
    val accuracy: String? = null,
    @SerializedName("mapbox_id") val mapboxId: String? = null,
    val wikidata: String? = null,
    @SerializedName("short_code") val shortCode: String? = null,
    @SerializedName("override:postcode") val overridePostcode: String? = null
)

data class Geometry(
    val type: String,
    val coordinates: List<Double>
)

data class MapboxContext(
    val id: String,
    @SerializedName("mapbox_id") val mapboxId: String? = null,
    val wikidata: String? = null,
    @SerializedName("short_code") val shortCode: String? = null,
    val text: String
)

data class ConfirmAssignmentRequest(
    @SerializedName("employee_id") val employeeId: String,
    val address: String,
    val coordinates: List<Double>,
    @SerializedName("mapbox_feature") val mapboxFeature: MapboxFeature,
    @SerializedName("parcel_id") val parcelId: String,
    val metadata: Metadata
)

data class Metadata(
    val notes: String
)

data class GenericResponse(
    val success: Boolean,
    val message: String? = null,
    @SerializedName("error_code") val errorCode: Int? = null
)
