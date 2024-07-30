package com.hoangkotlin.chatappsocial.core.network.utils

import android.util.Log
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

private const val TAG = "ServiceInterceptor"
class ServiceInterceptor @Inject constructor(
    private val tokenManager: DefaultTokenManager
) : Interceptor {
//    var token: String = ""
//
//    fun Token(token: String) {
//        this.token = token
//    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder =
            originalRequest.newBuilder()
        val token = runBlocking {
            tokenManager.token()
        }
        Log.d(TAG, "intercept: $token")
        if (token != null) {
            val authToken = "Bearer $token"
            requestBuilder.addHeader("Authorization", authToken)
        }

        return chain.proceed(requestBuilder.build())
    }
}