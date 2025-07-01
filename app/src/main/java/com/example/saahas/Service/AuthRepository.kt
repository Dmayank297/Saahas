package com.example.saahas.Service

import com.example.saahas.Models.LoginRequest
import com.example.saahas.Models.LoginResponse
import com.example.saahas.Models.SignupRequest
import com.example.saahas.Models.SignupResponse
import retrofit2.Response

class AuthRepository {

    private val apiService = RetrofitClient.apiService

    suspend fun login(request: LoginRequest): Response<LoginResponse> {
        return apiService.login(request)
    }

    suspend fun signup(request: SignupRequest): Response<SignupResponse> {
        return apiService.signup(request)
    }
}