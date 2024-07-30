package com.hoangkotlin.chatappsocial.core.offline.event

import com.hoangkotlin.chatappsocial.core.chat_client.handler.ChatEventHandler
import com.hoangkotlin.chatappsocial.core.model.events.ChannelUpdateEvent
import com.hoangkotlin.chatappsocial.core.model.events.ChatEvent
import com.hoangkotlin.chatappsocial.core.model.events.CidEvent
import com.hoangkotlin.chatappsocial.core.model.events.MessageReadEvent
import com.hoangkotlin.chatappsocial.core.model.events.NewMessageEvent
import com.hoangkotlin.chatappsocial.core.model.events.TypingStartEvent
import com.hoangkotlin.chatappsocial.core.model.events.TypingStopEvent
import com.hoangkotlin.chatappsocial.core.offline.state.messages.ChatChannelMutableState

class ChannelEventHandler(
    private val mutableState: ChatChannelMutableState
) : ChatEventHandler {
    override fun onChatEvent(event: ChatEvent) {
        if (event !is CidEvent || event.cid != mutableState.channelId) return
        when (event) {
            is ChannelUpdateEvent -> Unit
            is MessageReadEvent -> handleMessageReadEvent(event)
            is NewMessageEvent -> handleNewMessageEvent(event)
            is TypingStartEvent -> handleTypingStartEvent(event)
            is TypingStopEvent -> handleTypingStopEvent(event)
        }
    }

    private fun handleMessageReadEvent(event: MessageReadEvent) {
        mutableState.upsertRead(event.asSocialChannelRead())
    }

    private fun handleNewMessageEvent(event: NewMessageEvent) {
        mutableState.upsertMessage(event.message)
    }

    private fun handleTypingStartEvent(event: TypingStartEvent) {
        mutableState.addTyping(event.user)
    }

    private fun handleTypingStopEvent(event: TypingStopEvent) {
        mutableState.removeTyping(event.user)
    }

    override fun equals(other: Any?): Boolean {
        return this.mutableState == (other as? ChannelEventHandler)?.mutableState
    }

    override fun hashCode(): Int {
        return mutableState.hashCode()
    }

    companion object {
        private const val TAG = "ChannelEventHandler"
    }
}