package com.dev.carfinder

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("/api/Ubicacion")
    fun postLocation(@Body requestBody: RequestBody): Call<SimpleResponse>
}