package com.hoangkotlin.chatappsocial.core.network.api

import com.hoangkotlin.chatappsocial.core.network.model.response.AuthenticationResponse
import com.hoangkotlin.chatappsocial.core.network.model.request.SignInRequest
import com.hoangkotlin.chatappsocial.core.network.model.request.SignUpRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthenticationApi {

    @POST("/api/v1/auth/authenticate")
    suspend fun signIn(@Body signInRequest: SignInRequest): Response<AuthenticationResponse>

    @POST("/api/v1/auth/register")
    suspend fun signUp(@Body signUpRequest: SignUpRequest): Response<AuthenticationResponse>


}

