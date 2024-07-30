package com.hoangkotlin.chatappsocial.core.chat_client.handler

import com.hoangkotlin.chatappsocial.core.model.events.ChatEvent

interface ChatEventHandler {

    fun onChatEvent(event: ChatEvent)

}