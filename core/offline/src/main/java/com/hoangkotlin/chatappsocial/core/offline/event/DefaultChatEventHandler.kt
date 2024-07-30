package com.hoangkotlin.chatappsocial.core.offline.event

import android.util.Log
import com.hoangkotlin.chatappsocial.core.chat_client.ChatClient
import com.hoangkotlin.chatappsocial.core.chat_client.handler.ChatEventHandler
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.model.events.ChannelUpdateEvent
import com.hoangkotlin.chatappsocial.core.model.events.ChatEvent
import com.hoangkotlin.chatappsocial.core.model.events.CidEvent
import com.hoangkotlin.chatappsocial.core.model.events.HasChannel
import com.hoangkotlin.chatappsocial.core.model.events.HasMessage
import com.hoangkotlin.chatappsocial.core.model.events.HasUser
import com.hoangkotlin.chatappsocial.core.model.events.MessageReadEvent
import com.hoangkotlin.chatappsocial.core.model.events.NewMessageEvent
import com.hoangkotlin.chatappsocial.core.model.events.TypingStartEvent
import com.hoangkotlin.chatappsocial.core.model.events.TypingStopEvent
import com.hoangkotlin.chatappsocial.core.offline.state.ChatStateHolder
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * [ChatEventHandler] for a single channel.
 */
class DefaultChatEventHandler(
    private val stateHolder: ChatStateHolder
) : ChatEventHandler {

    // TODO: Rewrite for concern separation
    override fun onChatEvent(event: ChatEvent) {
//        stateHolder.handleEvent(event)
        Log.d(TAG, "onChatEvent: $event")
//        when (event) {
//            is HasChannel -> handleHasChannelEvent(event)
//            is HasMessage -> handleHasMessageEvent(event)
//            is HasUser -> handleHasUser(event)
//        }
    }

    private fun handleHasChannelEvent(event: HasChannel) {
        when (event) {
            is ChannelUpdateEvent -> {}
        }
    }

    private fun handleHasMessageEvent(event: HasMessage) {
        when (event) {
            is MessageReadEvent -> stateHolder.mutableChannel(event.cid)
                .upsertRead(event.asSocialChannelRead())

            is NewMessageEvent -> {
                stateHolder.mutableChannel(event.cid).upsertMessage(event.message)
            }
        }
    }

    private fun handleHasUser(event: HasUser) {
        when (event) {
            is TypingStartEvent -> TODO()
            is TypingStopEvent -> TODO()

        }
    }

    companion object {
        private const val TAG = "DefaultChatEventHandler"
    }
}

class WrapperChatEventHandler(
    private val eventHandler: ChatEventHandler
) : ChatEventHandler {
    override fun onChatEvent(event: ChatEvent) {
        eventHandler.onChatEvent(event)
    }
}

internal class ChatChannelListEventHandler(
    private val channelsState: MutableStateFlow<Map<String, SocialChatChannel>>,
    private val chatClient: ChatClient,
) : ChatEventHandler {

    private val jobsMap = mutableMapOf<String, Job>()

    override fun onChatEvent(event: ChatEvent) {
        if (event is CidEvent) {
            handleChannelListEvent(event)
        }
    }

    private fun handleChannelListEvent(
        event: CidEvent,
    ) {
        channelsState.update { channelsMap ->
            when (event) {
                is ChannelUpdateEvent -> handleChannelUpdateEvent(channelsMap, event)
                is NewMessageEvent -> handleNewMessageEvent(channelsMap, event)
                is TypingStartEvent -> channelsMap
                is TypingStopEvent -> channelsMap
                is MessageReadEvent -> handleMessageReadEvent(channelsMap, event)
            }
        }

    }

    private fun handleChannelUpdateEvent(
        channelsMap: Map<String, SocialChatChannel>,
        event: ChannelUpdateEvent,
    ): Map<String, SocialChatChannel> {
        return channelsMap;
    }

    private fun handleNewMessageEvent(
        channelsMap: Map<String, SocialChatChannel>,
        event: NewMessageEvent,
    ): Map<String, SocialChatChannel> {
        return if (channelsMap.containsKey(event.cid)) {
            val toUpdateChannel = channelsMap[event.cid]!!

            chatClient.showNotification(toUpdateChannel, event.message)

            val messages = toUpdateChannel.messages.toMutableSet()
            messages += event.message

            channelsMap.toMutableMap().apply {
                put(
                    toUpdateChannel.id, toUpdateChannel.copy(
                        messages = messages.toList(),
                        lastMessage = event.message,
                        unreadCount = event.unreadCount
                    )
                )
            }
            //add message
        } else {
//             send a signal to query channel in chat client
            jobsMap[event.cid]?.cancel()
            jobsMap[event.cid] = chatClient.launch {
                chatClient.queryChannel(event.cid)
            }
            channelsMap
        }
    }


    private fun handleMessageReadEvent(
        channelsMap: Map<String, SocialChatChannel>,
        event: MessageReadEvent,
    ): Map<String, SocialChatChannel> {
        return if (channelsMap.containsKey(event.cid)) {
            if (event.message.user.id == chatClient.clientState.user.value?.id) {
                val toUpdateChannel = channelsMap[event.cid]!!
                channelsMap.toMutableMap().apply {
                    put(
                        toUpdateChannel.id, toUpdateChannel.copy(
                            unreadCount = 0
                        )
                    )
                }

            } else
                channelsMap

            //add message
        } else {
            channelsMap
        }
    }

    companion object {
        private const val TAG = "ChatChannelListEventHandler"
    }
}
