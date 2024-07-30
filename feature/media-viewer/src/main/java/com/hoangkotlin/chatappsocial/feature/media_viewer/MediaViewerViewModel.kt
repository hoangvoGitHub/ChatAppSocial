package com.hoangkotlin.chatappsocial.feature.media_viewer

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hoangkotlin.chatappsocial.core.chat_client.ChatClient
import com.hoangkotlin.chatappsocial.core.chat_client.extension.uploadId
import com.hoangkotlin.chatappsocial.core.model.attachment.SocialChatAttachment
import com.hoangkotlin.chatappsocial.core.offline.StateRegistry
import com.hoangkotlin.chatappsocial.core.offline.extension.watchMedias
import com.hoangkotlin.chatappsocial.feature.media_viewer.navigation.AttachmentArg
import com.hoangkotlin.chatappsocial.feature.media_viewer.navigation.channelArg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MediaViewerViewModel @Inject constructor(
    private val chatClient: ChatClient,
    savedStateHandle: SavedStateHandle,
    stateRegistry: StateRegistry,
) : ViewModel() {

    private val channelId: String =
        requireNotNull(savedStateHandle.get<String>(channelArg))

    private val attachmentArg: AttachmentArg = AttachmentArg(savedStateHandle)
    private val attachmentUri = attachmentArg.uri
    private val attachmentCreatedAt = attachmentArg.createdAt
    private val initialAttachment = SocialChatAttachment(
        createdAt = attachmentCreatedAt,
        url = attachmentUri,
        imageUrl = attachmentUri,
        upload = File(attachmentUri)
    )

    private val _mediaViewerState = MutableStateFlow<MediaViewerState>(MediaViewerState.Loading)
    val mediaViewerState = _mediaViewerState.asStateFlow()

    private val mediasState = chatClient.watchMedias(
        channelId,
        stateRegistry = stateRegistry,
        coroutineScope = viewModelScope,
    )

    init {
        observeMedias()
    }

    private fun observeMedias() {
        viewModelScope.launch {
            mediasState.collect { attachments ->
                when {
                    attachments.isEmpty() -> {
                        _mediaViewerState.value = MediaViewerState.Loading
                    }

                    _mediaViewerState.value is MediaViewerState.Success -> {
                        _mediaViewerState.value =
                            (_mediaViewerState.value as MediaViewerState.Success).copy(attachments = attachments)
                    }

                    else -> {
                        val initialIndex = searchForIndex(attachments, initialAttachment)
                        if (initialIndex == INVALID_INDEX) {
                            _mediaViewerState.value = MediaViewerState.Failed
                        } else {
                            _mediaViewerState.value = MediaViewerState.Success(
                                initialIndex = initialIndex,
                                attachments = attachments
                            )
                        }
                    }
                }
            }
        }
    }

    private val downloadJobs = mutableMapOf<String, Job>()

    fun downloadAttachment(attachment: SocialChatAttachment) {
        downloadJobs[attachment.uploadId!!]?.cancel()
        downloadJobs[attachment.uploadId!!] =
            viewModelScope.launch(SupervisorJob(downloadJobs[attachment.uploadId!!])) {
                chatClient.downloadAttachment(attachment)
            }.apply {
                invokeOnCompletion {
                    downloadJobs.remove(attachment.uploadId!!)
                }
            }
    }

    // list attachment
// initial index
    companion object {
        private const val TAG = "MediaViewerViewModel"
    }
}

private const val INVALID_INDEX = -1

sealed class MediaViewerState {
    data object Loading : MediaViewerState()

    data object Failed : MediaViewerState()

    data class Success(
        val initialIndex: Int,
        val attachments: List<SocialChatAttachment>
    ) : MediaViewerState()
}

@VisibleForTesting
internal fun searchForIndex(
    attachments: List<SocialChatAttachment>,
    attachmentToSearch: SocialChatAttachment
): Int {
    val pivotIndex =
        attachments.binarySearch(comparison = { 0 - it.createdAt!!.compareTo(attachmentToSearch.createdAt) })
    val range = findRangeDate(pivotIndex, attachments)
    Log.d("MediaViewerViewModel", "searchForIndex: $range")
    for (i in range.first..range.second) {
        if (attachments[i].equalsInternally(attachmentToSearch)) {
            return i
        }
    }
    return INVALID_INDEX
}

@VisibleForTesting
fun findRangeDate(
    pivotIndex: Int,
    attachments: List<SocialChatAttachment>,
): Pair<Int, Int> {
    var start = pivotIndex
    var end = pivotIndex
    while (start > 0) {
        if (!attachments[pivotIndex].equalsInternally(attachments[start])) {
            break
        }
        start--
    }
    while (end <= attachments.lastIndex) {
        if (!attachments[pivotIndex].equalsInternally(attachments[end])) {
            break
        }
        end++
    }
    return Pair(start, end)
}

private fun SocialChatAttachment.equalsInternally(other: SocialChatAttachment): Boolean {
    return this.createdAt == other.createdAt &&
            (this.url == other.url ||
                    this.imageUrl == other.imageUrl ||
                    this.upload == other.upload)
}