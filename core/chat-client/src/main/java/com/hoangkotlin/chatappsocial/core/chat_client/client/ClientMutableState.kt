package com.hoangkotlin.chatappsocial.core.chat_client.client

import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import com.hoangkotlin.chatappsocial.core.common.model.ConnectionState

interface ClientMutableState : ClientState {

    fun setUser(user: SocialChatUser)

    fun setConnectionState(connectionState: ConnectionState)

    fun setInitializationState(initializationState: InitializationState)
}