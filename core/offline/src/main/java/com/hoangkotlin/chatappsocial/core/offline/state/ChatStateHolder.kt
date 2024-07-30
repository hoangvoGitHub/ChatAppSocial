package com.hoangkotlin.chatappsocial.core.offline.state

import android.util.Log
import com.hoangkotlin.chatappsocial.core.chat_client.ChatClient
import com.hoangkotlin.chatappsocial.core.model.SocialChannelRead
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.model.SocialChatMessage
import com.hoangkotlin.chatappsocial.core.model.events.ChannelUpdateEvent
import com.hoangkotlin.chatappsocial.core.model.events.ChatEvent
import com.hoangkotlin.chatappsocial.core.model.events.MessageReadEvent
import com.hoangkotlin.chatappsocial.core.model.events.NewMessageEvent
import com.hoangkotlin.chatappsocial.core.model.events.TypingStartEvent
import com.hoangkotlin.chatappsocial.core.model.events.TypingStopEvent
import com.hoangkotlin.chatappsocial.core.model.withoutMessages
import com.hoangkotlin.chatappsocial.core.network.model.request.QueryChatChannelRequest
import com.hoangkotlin.chatappsocial.core.offline.state.channel_list.QueryChannelListMutableState
import com.hoangkotlin.chatappsocial.core.offline.state.channel_list.QueryChannelListState
import com.hoangkotlin.chatappsocial.core.offline.state.messages.ChatChannelMutableState
import kotlinx.coroutines.CoroutineScope
import java.util.concurrent.ConcurrentHashMap


/**
 * Holds all chat state
 */
private const val TAG = "ChatStateHolder"

class ChatStateHolder(
    private val chatClient: ChatClient,
    private val userScope: CoroutineScope,
) {

    private val queryChannels: ConcurrentHashMap<String, SocialChatChannel> =
        ConcurrentHashMap()

    private var queryChannelsState: QueryChannelListState? = null

    fun queryChannelsState(): QueryChannelListState {
        return queryChannelsState ?: QueryChannelListMutableState(
            userScope,
            chatClient
        ).also {
            queryChannelsState = it
        }
    }


    private val channels: ConcurrentHashMap<String, ChatChannelMutableState> = ConcurrentHashMap()

    fun mutableChannel(channelId: String): ChatChannelMutableState {
        return channels.getOrPut(channelId) {
            ChatChannelMutableState(
                channelId = channelId,
                scope = userScope,
                userFlow = chatClient.clientState.user,
                initialChannel = queryChannels[channelId]
            )
        }
    }

    internal fun handleEvent(chatEvent: ChatEvent) {
        when (chatEvent) {
            is ChannelUpdateEvent -> TODO()
            is NewMessageEvent -> handleNewMessageEvent(chatEvent)
            is TypingStartEvent -> handleTypingStartEvent(chatEvent)
            is TypingStopEvent -> handleTypingStopEvent(chatEvent)
            is MessageReadEvent -> handleMessageReadEvent(chatEvent)
        }
    }

    private fun handleTypingStartEvent(event: TypingStartEvent) {
        mutableChannel(event.cid).addTyping(event.user)
    }

    private fun handleTypingStopEvent(event: TypingStopEvent) {
        mutableChannel(event.cid).removeTyping(event.user)
    }


    private fun handleMessageReadEvent(chatEvent: MessageReadEvent) {
        channels[chatEvent.cid]?.upsertReads(
            listOf(
                SocialChannelRead(
                    user = chatEvent.user,
                    lastReadMessageId = chatEvent.message.id,
                    lastReadAt = chatEvent.createdAt,
                )
            )
        )
    }

    private fun handleNewMessageEvent(chatEvent: NewMessageEvent) {
        if (queryChannels.containsKey(chatEvent.cid)) {
            val currentChannel = queryChannels[chatEvent.cid]!!
            val currentMessages = currentChannel.messages.associateBy(SocialChatMessage::id)
            queryChannels[chatEvent.cid] = currentChannel.copy(
                messages = currentMessages
                    .toMutableMap()
                    .apply {
                        put(chatEvent.message.id, chatEvent.message)
                    }.values.toList()
            )
        }
        channels[chatEvent.cid]?.upsertMessage(chatEvent.message)
    }

    fun updateChannelDataWithRequest(
        channelData: SocialChatChannel,
        request: QueryChatChannelRequest
    ) {
        channels.getOrPut(channelData.id) {
            ChatChannelMutableState(
                channelId = channelData.id,
                scope = userScope,
                userFlow = chatClient.clientState.user,
                initialChannel = channelData
            )
        }.apply {
            val messages = channelData.messages.toList()
            setChannelData(channelData.withoutMessages())
            if (messages.isNotEmpty()) {
                upsertMessages(messages)
                setEndOfOlderMessages(messages.size < request.messageLimit)
            } else {
                setEndOfOlderMessages(true)
            }

        }
        queryChannelsState?.asMutableState()?.upsertChannel(channelData)
    }

    fun setQueryChannels(channels: List<SocialChatChannel>) {
        queryChannels.putAll(channels.associateBy(SocialChatChannel::id))
    }

    fun clearState() {
        queryChannelsState?.clearState()
        channels.clear()
    }

}

fun QueryChannelListState.asMutableState(): QueryChannelListMutableState {
    return this as QueryChannelListMutableState
}