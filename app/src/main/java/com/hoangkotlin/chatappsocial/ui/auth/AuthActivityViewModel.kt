package com.hoangkotlin.chatappsocial.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hoangkotlin.chatappsocial.core.data.repository.app.AppDataRepository
import com.hoangkotlin.chatappsocial.ui.main.MainActivityUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AuthActivityViewModel @Inject constructor(
    dataSource: AppDataRepository
) : ViewModel() {

    private val _navigationChannel = Channel<NavigationEvent>()
    val navigationEvent = _navigationChannel.consumeAsFlow()

    val uiState: StateFlow<MainActivityUiState> =
        dataSource.appUserData.distinctUntilChanged { old, new -> old.currentUser?.token == new.currentUser?.token }
            .map {
                if (it.currentUser == null) {
//            chatClient.releaseConnection()
                    MainActivityUiState.AuthFailed
                } else {
//            chatClient.setUpUser(it.currentUser!!)
                    _navigationChannel.send(NavigationEvent.NavigateToHome)
                    MainActivityUiState.AuthSuccess(it.currentUser!!)
                }
            }.stateIn(
            scope = viewModelScope,
            initialValue = MainActivityUiState.Loading,
            started = SharingStarted.Eagerly,
        )
}

sealed interface NavigationEvent {
    data object NavigateToHome : NavigationEvent
}