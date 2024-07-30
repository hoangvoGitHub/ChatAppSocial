package com.hoangkotlin.chatappsocial.core.network.model.request

data class MuteChannelRequest(
    val channelId: String,
    val expiration: Int = -1
)
