package com.hoangkotlin.chatappsocial.core.network.utils

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

/**
 *  Acknowledgment from [https://getstream.io/blog/android-upload-progress].
 */
class ProgressInterceptor @Inject constructor(): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val progressCallback = request.tag(ProgressCallback::class.java)
        if (progressCallback != null) {
            return chain.proceed(wrapRequest(request, progressCallback))
        }

        return chain.proceed(request)
    }

    private fun wrapRequest(request: Request, progressCallback: ProgressCallback): Request {
        return request.newBuilder()
            // Assume that any request tagged with a ProgressCallback is a POST
            // request and has a non-null body            
            .post(ProgressRequestBody(request.body!!, progressCallback))
            .build()
    }
}