package com.hoangkotlin.chatappsocial.core.chat_client.client

import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import com.hoangkotlin.chatappsocial.core.common.model.ConnectionState
import com.hoangkotlin.chatappsocial.core.chat_client.utils.NetworkStateProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class SocialClientState @Inject constructor(
    private val networkStateProvider: NetworkStateProvider
) : ClientMutableState {

    private val _initializationState = MutableStateFlow(InitializationState.NOT_INITIALIZED)
    private val _initialized = MutableStateFlow(false)
    private val _connectionState = MutableStateFlow(ConnectionState.OFFLINE)
    private val _user = MutableStateFlow<SocialChatUser?>(null)

    override val user: StateFlow<SocialChatUser?> = _user

    override val initialized: StateFlow<Boolean>
        get() = _initialized

    override val initializationState: StateFlow<InitializationState>
        get() = _initializationState

    override val connectionState: StateFlow<ConnectionState>
        get() = _connectionState

    override val isConnecting: Boolean
        get() = _connectionState.value == ConnectionState.CONNECTING

    override val isInitialized: Boolean
        get() = _initializationState.value == InitializationState.COMPLETE

    override val isNetworkAvailable: Boolean
        get() = networkStateProvider.isConnected()

    override fun setUser(user: SocialChatUser) {
        _user.value = user
    }

    override fun setConnectionState(connectionState: ConnectionState) {
        _connectionState.value = connectionState
    }

    override fun setInitializationState(initializationState: InitializationState) {
        _initializationState.value = initializationState
        _initialized.value = initializationState == InitializationState.COMPLETE
    }


    override fun clearState() {
        _initializationState.value = InitializationState.NOT_INITIALIZED
        _connectionState.value = ConnectionState.OFFLINE
        _user.value = null
    }
}
fun ClientState.toMutableState(): ClientMutableState? = this as? ClientMutableState