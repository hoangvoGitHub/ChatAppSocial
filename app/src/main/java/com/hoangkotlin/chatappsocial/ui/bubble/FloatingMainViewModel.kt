package com.hoangkotlin.chatappsocial.ui.bubble

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hoangkotlin.chatappsocial.SocialApplication
import com.hoangkotlin.chatappsocial.core.chat_client.ChatClient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val TAG = "FloatingMainViewModel"

@HiltViewModel
class FloatingMainViewModel @Inject constructor(
    chatClient: ChatClient
) : ViewModel() {

    val currentUser = chatClient.clientState.user

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val chatClient = (this[APPLICATION_KEY] as SocialApplication).chatClient
                FloatingMainViewModel(
                    chatClient = chatClient,
                )
            }
        }
    }
}

sealed interface BubbleState {
    data object Loading : BubbleState
    data class Success(val id: String) : BubbleState
    data object Failed : BubbleState

}