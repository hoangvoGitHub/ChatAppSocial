package com.hoangkotlin.chatappsocial.core.chat_client.client

import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import com.hoangkotlin.chatappsocial.core.common.model.ConnectionState
import kotlinx.coroutines.flow.StateFlow

interface ClientState {

    val user : StateFlow<SocialChatUser?>

    val initialized: StateFlow<Boolean>

    val initializationState: StateFlow<InitializationState>

    val connectionState: StateFlow<ConnectionState>

    val isConnecting: Boolean

    val isInitialized: Boolean

    val isNetworkAvailable: Boolean

    fun clearState()
}

enum class InitializationState {
    COMPLETE,
    RUNNING,
    NOT_INITIALIZED
}


