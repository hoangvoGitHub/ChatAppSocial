package com.hoangkotlin.chatappsocial.core.websocket_naiksoftware

import okhttp3.OkHttpClient

class SocialStompClientFactory {

    companion object {
        fun create(
            uri: String,
            connectHeaders: Map<String, String>? = null,
            client: OkHttpClient = OkHttpClient()
        ): SocialStompClient {
            return SocialStompClient(
                SocialHttpConnectionProvider(
                    uri = uri,
                    connectHeaders = connectHeaders,
                    client
                )
            )
        }
    }
}