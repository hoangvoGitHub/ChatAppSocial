package com.hoangkotlin.chatappsocial.core.network.socket

import com.hoangkotlin.chatappsocial.core.network.model.dto.ChatEventDto

interface ChatSocket {

    fun connect()

    fun onEvent(event: ChatEventDto)

    fun releaseConnection()



}