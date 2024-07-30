package com.hoangkotlin.chatappsocial.core.chat_client.extension

import com.hoangkotlin.chatappsocial.core.chat_client.ChatClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

//fun ChatClient.queryChatChannels(
//    request: QueryChatChannelRequest,
//    ioDispatcher: CoroutineDispatcher = DispatchersModule.providesIODispatcher(),
//): Flow<> {
//    return clientStat
//}


private fun <T> ChatClient.getClientStateOrNull(
    coroutineScope: CoroutineScope,
    producer: () -> T
): StateFlow<T?> {
    return clientState.user.map {
        it?.id
    }.distinctUntilChanged()
        .map { userId ->
            if (userId == null) {
                null
            } else {
                producer()
            }
        }
        .stateIn(coroutineScope, SharingStarted.Eagerly, null)
}