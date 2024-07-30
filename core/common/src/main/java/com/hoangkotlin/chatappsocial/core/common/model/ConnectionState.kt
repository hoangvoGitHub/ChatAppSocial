package com.hoangkotlin.chatappsocial.core.common.model

enum class ConnectionState(
    val displayName: String
) {
    CONNECTED("Connected"),
    CONNECTING("Connecting"),
    OFFLINE("Offline")
}
