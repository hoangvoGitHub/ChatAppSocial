package com.hoangkotlin.chatappsocial.core.offline.extension

import com.hoangkotlin.chatappsocial.core.chat_client.ChatClient
import com.hoangkotlin.chatappsocial.core.data.model.DataResult
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.model.attachment.SocialChatAttachment
import com.hoangkotlin.chatappsocial.core.network.model.request.QueryManyChannelRequest
import com.hoangkotlin.chatappsocial.core.offline.StateRegistry
import com.hoangkotlin.chatappsocial.core.offline.state.GeneralState
import com.hoangkotlin.chatappsocial.core.offline.state.MutableGeneralState
import com.hoangkotlin.chatappsocial.core.offline.state.channel_list.QueryChannelListState
import com.hoangkotlin.chatappsocial.core.offline.state.messages.ChatChannelState
import com.hoangkotlin.chatappsocial.core.offline.state.messages.asChatChannelMutableState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "ChatClient"

fun ChatClient.queryChannelsAsState(
    request: QueryManyChannelRequest,
    coroutineScope: CoroutineScope,
    stateRegistry: StateRegistry,
): StateFlow<QueryChannelListState?> {
    return getStateOrNull(coroutineScope) {
        this.queryChannels(request)
        stateRegistry.queryChannelsState()
    }
}


fun ChatClient.watchChannelAsState(
    cid: String,
    messageLimit: Int = 50,
    coroutineScope: CoroutineScope,
    stateRegistry: StateRegistry,
): StateFlow<ChatChannelState?> {
    return getStateOrNull(coroutineScope) {
        coroutineScope.launch {
            queryChannel(cid, messageLimit)
        }
        stateRegistry.mutableChannel(cid)
    }
}

fun ChatClient.watchMedias(
    cid: String,
    stateRegistry: StateRegistry,
    coroutineScope: CoroutineScope,
): StateFlow<List<SocialChatAttachment>> {
    return stateRegistry.mutableChannel(cid)
        .asChatChannelMutableState()!!.mediaAttachments
        .stateIn(coroutineScope, SharingStarted.Eagerly, emptyList())
}

fun ChatClient.watchOtherAttachments(
    cid: String,
    stateRegistry: StateRegistry,
    coroutineScope: CoroutineScope,
): StateFlow<List<SocialChatAttachment>> {
    return stateRegistry.mutableChannel(cid)
        .asChatChannelMutableState()!!.otherAttachments
        .stateIn(coroutineScope, SharingStarted.Eagerly, emptyList())
}

fun <T> ChatClient.getStateOrNull(
    coroutineScope: CoroutineScope,
    producer: suspend () -> T,
): StateFlow<T?> {
    return clientState.user.map { it?.id }.distinctUntilChanged().map { userId ->
        if (userId == null) {
            null
        } else {
            producer()
        }
    }.stateIn(coroutineScope, SharingStarted.Eagerly, null)
}

suspend fun ChatClient.loadOlderMessages(
    cid: String, messageLimit: Int, stateRegistry: StateRegistry
): DataResult<SocialChatChannel> {
    return stateRegistry.loadOlderMessage(
        cid, messageLimit,
        queryChannelCall = { channelId, request ->
            queryChannel(channelId, request)
        }
    )

}

val ChatClient.generalState: GeneralState
    get() = MutableGeneralState(clientState)
