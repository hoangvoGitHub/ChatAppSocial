package com.hoangkotlin.chatappsocial.ui.main

import com.hoangkotlin.chatappsocial.core.chat_client.handler.ChatEventHandler
import com.hoangkotlin.chatappsocial.core.chat_client.handler.ClearConversationManager
import com.hoangkotlin.chatappsocial.core.chat_client.handler.QueryChannelManager
import com.hoangkotlin.chatappsocial.core.chat_client.handler.QueryChannelsManager
import com.hoangkotlin.chatappsocial.core.chat_client.interceptor.Interceptor
import com.hoangkotlin.chatappsocial.core.offline.event.DefaultChatEventHandler
import com.hoangkotlin.chatappsocial.core.offline.event.DefaultClearConversationManager
import com.hoangkotlin.chatappsocial.core.offline.event.DefaultQueryChannelManager
import com.hoangkotlin.chatappsocial.core.offline.event.DefaultQueryChannelsManager
import com.hoangkotlin.chatappsocial.core.offline.event.WrapperChatEventHandler
import com.hoangkotlin.chatappsocial.core.offline.state.ChatStateHolder

class SocialPluginsProvider(
    private val stateHolder: ChatStateHolder,
    interceptor: Interceptor
) {

    val eventHandlers: List<ChatEventHandler>
        get() = listOf(
            WrapperChatEventHandler(
                stateHolder.queryChannelsState().eventHandler
            )
        )
    val interceptors: List<Interceptor> = listOf(interceptor)

    val queryChannelsManagers: List<QueryChannelsManager> =
        listOf(DefaultQueryChannelsManager(stateHolder))

    val queryChannelManagers: List<QueryChannelManager> =
        listOf(DefaultQueryChannelManager(stateHolder))

    val clearConversationManagers: List<ClearConversationManager> = listOf(
        DefaultClearConversationManager(stateHolder)
    )
}