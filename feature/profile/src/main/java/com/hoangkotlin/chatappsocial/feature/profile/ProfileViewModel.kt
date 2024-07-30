package com.hoangkotlin.chatappsocial.feature.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hoangkotlin.chatappsocial.core.chat_client.ChatClient
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import com.hoangvo.core.worker.upload.UploadProfileImageWorker
import com.hoangvo.core.worker.upload.WorkerUploadState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    chatClient: ChatClient,
) : ViewModel() {

    val currentUser: StateFlow<SocialChatUser?> = chatClient.clientState.user

    private val _uploadProfileImageState =
        MutableStateFlow(
            UploadProfileImageState(
                imageToDisplay = ToDisplayImage.FromUser(
                    user = currentUser.value ?: SocialChatUser()
                )
            )
        )
    val uploadProfileImageState = _uploadProfileImageState.asStateFlow()

    fun updateProfileImage(uri: Uri, filePath: String) {
        val userId = currentUser.value?.id ?: return
        if (_uploadProfileImageState.value.isUpdating) return

        _uploadProfileImageState.update { currentState ->
            currentState.copy(
                imageToDisplay = ToDisplayImage.FromLocal(uri = uri),
                isUpdating = true
            )
        }
        val requestId = UploadProfileImageWorker.start(
            context = context,
            filePath = filePath,
            userId
        )

        _uploadProfileImageState.update { currentState ->
            currentState.copy(isUpdating = true)
        }

        viewModelScope.launch {
            UploadProfileImageWorker.uploadWorkProgress(
                context, requestId
            ).collectLatest { uploadState ->
                when (uploadState) {
                    WorkerUploadState.Failure -> _uploadProfileImageState.update { currentState ->
                        currentState.copy(
                            isUpdating = false,
                            isFailed = true
                        )
                    }

                    is WorkerUploadState.InProgress -> _uploadProfileImageState.update { currentState ->
                        currentState.copy(progress = uploadState.progress)
                    }

                    WorkerUploadState.Success -> _uploadProfileImageState.update { currentState ->
                        currentState.copy(
                            isUpdating = false,
                            isFailed = false
                        )
                    }
                }
            }
        }

    }
}

// dark mode, online status

sealed class ToDisplayImage {
    data class FromLocal(val uri: Uri) : ToDisplayImage()
    data class FromUser(val user: SocialChatUser) : ToDisplayImage()
}

data class UploadProfileImageState(
    val imageToDisplay: ToDisplayImage = ToDisplayImage.FromUser(SocialChatUser()),
    val isUpdating: Boolean = false,
    val isFailed: Boolean = false,
    val progress: Float = 0f,
)

enum class ThemeType {
    Dark,
    Light,
    System
}

data class UserConfigState(
    val themeType: ThemeType,
    val isVisible: Boolean,

    )

