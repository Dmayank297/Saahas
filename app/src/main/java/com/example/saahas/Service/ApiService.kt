package com.example.saahas.Service

import com.example.saahas.Models.LoginRequest
import com.example.saahas.Models.LoginResponse
import com.example.saahas.Models.NotificationResponse
import com.example.saahas.Models.SignupRequest
import com.example.saahas.Models.SignupResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/api/auth/signup")
    suspend fun signup(@Body request: SignupRequest): Response<SignupResponse>

    @POST("/api/message/send")
    suspend fun sendNotification(
        @Header("Authorization") token: String
    ): Response<NotificationResponse>

}
