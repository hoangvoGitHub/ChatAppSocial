package com.hoangkotlin.chatappsocial.core.network.api

import com.hoangkotlin.chatappsocial.core.network.model.request.AddDeviceRequest
import com.hoangkotlin.chatappsocial.core.network.model.response.CompletableResponse
import com.hoangkotlin.chatappsocial.core.network.model.response.DevicesResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface DeviceApi {

    @GET("/api/v1/devices")
    suspend fun getDevices(): Response<DevicesResponse>

    @POST("/api/v1/devices")
    suspend fun addDevice(@Body request: AddDeviceRequest): Response<CompletableResponse>

    @DELETE("/api/v1/devices")
    suspend fun deleteDevice(@Query("id") deviceId: String): Response<CompletableResponse>
}