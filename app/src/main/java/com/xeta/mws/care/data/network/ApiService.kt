package com.xeta.mws.care.data.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("ocr_address/")
    fun processOcrAddress(
        @Body request: OcrAddressRequest
    ): Call<OcrAddressResponse>

    @POST("confirm_assignment/")
    fun confirmAssignment(
        @Body request: ConfirmAssignmentRequest
    ): Call<GenericResponse>

    @GET("summary/")
    fun getSummary(): Call<SummaryResponse>
}
