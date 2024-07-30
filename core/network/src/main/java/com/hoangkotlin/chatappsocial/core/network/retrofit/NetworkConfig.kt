package com.hoangkotlin.chatappsocial.core.network.retrofit

class NetworkConfig {


    companion object {

        val BASE_URL = "http://10.0.2.2:8080"

        val WS_URL
            get() = "ws://$10.0.2.2:8080/ws/websocket"

        val TOPIC_PREFIX = "/topic"
        val SENT_DESTINATION_PREFIX = "/app/chat"
    }
}