package com.hoangkotlin.chatappsocial.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hoangkotlin.chatappsocial.core.chat_client.ChatClient
import com.hoangkotlin.chatappsocial.core.common.model.ConnectionState
import com.hoangkotlin.chatappsocial.core.data.model.DataResult
import com.hoangkotlin.chatappsocial.core.data.repository.app.AppDataRepository
import com.hoangkotlin.chatappsocial.core.model.ChatAppUser
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import com.hoangkotlin.chatappsocial.core.offline.StateRegistry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MainActivityViewModel"

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val dataSource: AppDataRepository,
    private val pluginsProvider: SocialPluginsProvider,
    private val chatClient: ChatClient,
    private val sateRegistry: StateRegistry
) : ViewModel() {

    private val uiState: StateFlow<MainActivityUiState> =
        dataSource.appUserData.distinctUntilChanged().mapLatest {
            if (it.currentUser == null) {
                chatClient.releaseConnection()
                MainActivityUiState.AuthFailed
            } else {
                chatClient.setUpUser(it.currentUser!!)
                Log.d(TAG, "setup user in main: ${it.currentUser}")
                onUserConnected()
                MainActivityUiState.AuthSuccess(it.currentUser!!)
            }
        }.stateIn(
            scope = viewModelScope,
            initialValue = MainActivityUiState.Loading,
            started = SharingStarted.Eagerly,
        )

    private fun onUserConnected() {
        pluginsProvider.eventHandlers.forEach(chatClient::addEventHandler)
        pluginsProvider.interceptors.forEach(chatClient::addInterceptor)
        pluginsProvider.queryChannelsManagers.forEach(chatClient::addQueryChannelsHandler)
        pluginsProvider.queryChannelManagers.forEach(chatClient::addQueryChannelManager)
        pluginsProvider.clearConversationManagers.forEach(chatClient::addClearConversationManager)
    }

    fun logOut() {
        viewModelScope.launch(SupervisorJob()) {
            sateRegistry.clearState()
            dataSource.signOut()
            chatClient.releaseConnection()
        }
    }

    val socketState: StateFlow<ConnectionState> =
        chatClient.connectionState.consumeAsFlow()
            .onEach {
                Log.d(TAG, "connectionState :$it")
            }
            .stateIn(viewModelScope, SharingStarted.Eagerly, ConnectionState.CONNECTING)


    val currentUser = uiState.flatMapLatest {
        if (it is MainActivityUiState.AuthSuccess) {
            when (val result = chatClient.queryChatUserByUsername(it.userData.username)) {
                is DataResult.Error -> flowOf(null)
                is DataResult.Success -> {
                    flowOf(result.data)
                }
            }
        } else {
            flowOf(null)
        }
    }.filterNotNull()
        .stateIn(viewModelScope, SharingStarted.Eagerly, SocialChatUser())

}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class AuthSuccess(val userData: ChatAppUser) : MainActivityUiState
    data object AuthFailed : MainActivityUiState
}