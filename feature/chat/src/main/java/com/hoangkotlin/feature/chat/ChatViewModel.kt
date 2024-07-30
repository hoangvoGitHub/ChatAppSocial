package com.hoangkotlin.feature.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hoangkotlin.chatappsocial.core.chat_client.ChatClient
import com.hoangkotlin.chatappsocial.core.chat_client.extension.hasAttachments
import com.hoangkotlin.chatappsocial.core.common.utils.SyncStatus
import com.hoangkotlin.chatappsocial.core.model.SocialChannelRead
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.model.SocialChatMessage
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import com.hoangkotlin.chatappsocial.core.model.getCreatedAtOrNull
import com.hoangkotlin.chatappsocial.core.model.getCreatedAtOrThrow
import com.hoangkotlin.chatappsocial.core.offline.StateRegistry
import com.hoangkotlin.chatappsocial.core.offline.event.ChannelEventHandler
import com.hoangkotlin.chatappsocial.core.offline.extension.loadOlderMessages
import com.hoangkotlin.chatappsocial.core.offline.extension.watchChannelAsState
import com.hoangkotlin.chatappsocial.core.offline.state.channel_list.ListStateData
import com.hoangkotlin.chatappsocial.core.offline.state.messages.ChatChannelState
import com.hoangkotlin.chatappsocial.core.offline.state.messages.ChatMessageItemState
import com.hoangkotlin.chatappsocial.core.offline.state.messages.DateSeparatorState
import com.hoangkotlin.chatappsocial.core.offline.state.messages.MessageItemGroupPosition
import com.hoangkotlin.chatappsocial.core.offline.state.messages.MessageListItemState
import com.hoangkotlin.chatappsocial.core.offline.state.messages.MessagesState
import com.hoangkotlin.chatappsocial.core.offline.state.messages.asChatChannelMutableState
import com.hoangkotlin.feature.chat.navigation.channelArg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val TAG = "ChatViewModel"

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatClient: ChatClient,
    private val stateRegistry: StateRegistry,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val channelId: String =
        requireNotNull(savedStateHandle[channelArg])

    val currentUser: StateFlow<SocialChatUser?> = chatClient.clientState.user

    private val _messagesState = MutableStateFlow(MessagesState())
    val messagesState = _messagesState.asStateFlow()

    private val channelState: StateFlow<ChatChannelState?> = chatClient.watchChannelAsState(
        cid = channelId,
        messageLimit = DefaultMessageLimit,
        coroutineScope = viewModelScope,
        stateRegistry = stateRegistry
    )

    private val _channel = MutableStateFlow(SocialChatChannel())
    val channel = _channel.asStateFlow()

    private val _lastVisibleMessage = MutableStateFlow<SocialChatMessage?>(null)

    private val jobMapsTest = mutableMapOf<String, Job>()

    init {
        subscribeChannel()
        observeAndMarkReadLatestMessage()
        observeMessages()
        observeChannel()

    }

    private fun subscribeChannel() {
        chatClient.subscribeChannel(
            channelId,
            ChannelEventHandler(
                stateRegistry
                    .mutableChannel(channelId)
                    .asChatChannelMutableState()!!
            )
        )
    }

    private fun observeMessages() {
        viewModelScope.launch {
            channelState.filterNotNull().collectLatest { channelState ->
                combine(
                    channelState.messagesState,
                    currentUser,
                    channelState.reads,
                    // add this to make the combine flow emit new data when endOfOlderMessages changes
                    channelState.endOfOlderMessages
                ) { state, user, reads, endOfMessages ->

                    when (state) {
                        ListStateData.NoQueryActive,
                        ListStateData.Loading -> _messagesState.value.copy(
                            isLoading = true
                        )

                        ListStateData.OfflineNoData -> _messagesState.value.copy(
                            isLoading = false,
                            messageItems = emptyList(),
                        )

                        is ListStateData.Result -> _messagesState.value.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            messageItems = groupMessage(
                                state.items,
                                currentUser.value,
                                reads
                            ),
                            currentUser = user,
                            endOfMessages = endOfMessages
                        )
                    }
                }.collect {
                    _messagesState.value = it
                }
            }
        }
    }


    private fun observeAndMarkReadLatestMessage() {
        viewModelScope.launch {
            combine(
                _messagesState,
                _lastVisibleMessage.filterNotNull()
            ) { state, lastVisibleMessage ->
                val lastMessage =
                    state.messageItems.firstOrNull { it is ChatMessageItemState } as? ChatMessageItemState?
                lastMessage?.let { lastItem ->

                    if (lastItem.message.syncStatus != SyncStatus.COMPLETED) return@let

                    // if the last message is not visible then do nothing
                    if (lastItem.message.id != lastVisibleMessage.id) return@let

                    // Do nothing if the message is already marked as read by current user
                    if (lastItem.lastReadBy.any { it.user.id == currentUser.value?.id }) return@let

                    // Do nothing if mark read job for the last message is already launch without being cancelled
                    if (jobMapsTest[lastItem.message.id] != null &&
                        (jobMapsTest[lastItem.message.id]!!.isActive || jobMapsTest[lastItem.message.id]!!.isCompleted)
                    ) {
                        return@let
                    }

                    // Start the mark read job
                    jobMapsTest[lastItem.message.id] = viewModelScope.launch {
                        chatClient.markRead(channelId, lastItem.message.id)
                    }
                    // Wait the job until it is completed before cleaning up
                    jobMapsTest[lastItem.message.id]?.join()
                    jobMapsTest.entries.removeIf { it.key != lastMessage.message.id }
                }
            }.collect {}
        }
    }


    private val markReadJobMap = mutableMapOf<Date, Job>()

    private fun observeChannel() {
        viewModelScope.launch {
            channelState.filterNotNull().flatMapLatest {
                it.channelData
            }.collect {
                _channel.value = it
            }
        }
    }

    fun loadMore() {
        viewModelScope.launch {
            if (_messagesState.value.isLoadingMore || _messagesState.value.endOfMessages) return@launch
            _messagesState.update { currentState ->
                currentState.copy(isLoadingMore = true)
            }
            chatClient.loadOlderMessages(channelId, DefaultMessageLimit, stateRegistry)
        }
    }

    fun onLastVisibleMessageChanged(socialChatMessage: SocialChatMessage) {
        _lastVisibleMessage.value = socialChatMessage
    }

    override fun onCleared() {
        super.onCleared()
        chatClient.unsubscribeChannel(
            channelId,
            ChannelEventHandler(
                stateRegistry
                    .mutableChannel(channelId)
                    .asChatChannelMutableState()!!
            )
        )
    }


    internal companion object {
        /**
         * This constant defines the default time threshold, in hours, for displaying date separators.
         * If the time difference between messages is equal to this threshold, a separator is shown, provided it's enabled in the list.
         */
        private const val DateSeparatorDefaultHourThreshold: Long = 1

        /**
         * This constant defines the default time threshold, in hours, for displaying date separators.
         * If the time difference between messages is equal to this threshold, a separator is shown, provided it's enabled in the list.
         */
        private const val ConsecutiveDefaultMinuteThreshold: Long = 1

        /**
         * This constant sets the default maximum number of messages per request.
         */
        internal const val DefaultMessageLimit: Int = 30

        /**
         * Specifies the time, in milliseconds, after which message focus is removed.
         */
        private const val RemoveMessageFocusDelay: Long = 2000

        /**
         * Sets the default time difference, in milliseconds, for updating footer information.
         */
        private const val DEFAULT_FOOTER_TIME_DIFF_MILLIS: Long = 60 * 1000L

        fun groupMessage(
            messages: List<SocialChatMessage>,
            currentUser: SocialChatUser?,
            reads: List<SocialChannelRead>
        ): List<MessageListItemState> {


            val groupedMessages =
                mutableListOf<MessageListItemState>()

            messages.forEachIndexed { index, message ->

                val lastReadBy =
                    reads.filter { it.lastReadMessageId == message.id && it.user.id != currentUser?.id }

                val user = message.user
                val previousMessage = messages.getOrNull(index - 1)
                val nextMessage = messages.getOrNull(index + 1)

                val previousUser = previousMessage?.user
                val nextUser = nextMessage?.user

                // Separate the previous message with the current message or not
                val willSeparatePreviousAndCurrent =
                    previousMessage?.let { isNotConsecutiveMessages(it, message) } ?: false

                // Separate the current message with the next message or not
                val willSeparateCurrentAndNext =
                    nextMessage?.let { isNotConsecutiveMessages(message, it) } ?: true

                val position = when {
                    message.hasAttachments -> MessageItemGroupPosition.Bottom
                    previousUser != user && nextUser == user && !willSeparateCurrentAndNext -> MessageItemGroupPosition.Top
                    previousUser == user && nextUser == user && willSeparatePreviousAndCurrent && !willSeparateCurrentAndNext -> MessageItemGroupPosition.Top
                    previousUser == user && nextUser == user && !willSeparatePreviousAndCurrent && !willSeparateCurrentAndNext -> MessageItemGroupPosition.Middle
                    previousUser == user && !willSeparatePreviousAndCurrent && willSeparateCurrentAndNext -> MessageItemGroupPosition.Bottom
                    (nextUser != user && previousUser == user && !willSeparatePreviousAndCurrent && !willSeparateCurrentAndNext) -> MessageItemGroupPosition.Bottom
                    else -> MessageItemGroupPosition.None
                }

                if (shouldAddDateSeparator(previousMessage, message)) {
                    groupedMessages.add(
                        DateSeparatorState(
                            message.getCreatedAtOrThrow()
                        )
                    )
                }
                val isLastMessageInGroup =
                    position == MessageItemGroupPosition.Bottom || position == MessageItemGroupPosition.None

                val shouldShowMessageFooter = isLastMessageInGroup ||
                        message.user != nextMessage?.user ||
                        (nextMessage.getCreatedAtOrNull()?.time
                            ?: 0) - (message.getCreatedAtOrNull()?.time
                    ?: 0) > DEFAULT_FOOTER_TIME_DIFF_MILLIS

                val shouldShowSyncStatus = user == currentUser &&
                        (message.syncStatus != SyncStatus.COMPLETED
                                || nextMessage == null
                                || (
                                nextUser != user && nextMessage.syncStatus != SyncStatus.COMPLETED)
                                )
                groupedMessages.add(
                    ChatMessageItemState(
                        message = message,
                        currentUser = currentUser,
                        groupPosition = position,
                        isMine = user.id == currentUser?.id,
                        shouldShowFooter = shouldShowMessageFooter,
                        shouldShowSyncStatus = shouldShowSyncStatus,
                        lastReadBy = lastReadBy,
                        isMessageRead = lastReadBy.isNotEmpty()
                    )
                )
            }

            return groupedMessages.reversed()
        }

        private fun shouldAddDateSeparator(
            previousMessage: SocialChatMessage?, currentMessage: SocialChatMessage
        ): Boolean {
            return if (previousMessage == null) {
                true
            } else {
                val timeDifference =
                    currentMessage.getCreatedAtOrThrow().time - previousMessage.getCreatedAtOrThrow().time

                return timeDifference > TimeUnit.HOURS.toMillis(
                    DateSeparatorDefaultHourThreshold
                )
            }
        }

        private fun isNotConsecutiveMessages(
            firstMessage: SocialChatMessage?, secondMessage: SocialChatMessage
        ): Boolean {
            return if (firstMessage == null) {
                true
            } else if (secondMessage.replyTo != null) {
                true
            } else {
                val timeDifference =
                    secondMessage.getCreatedAtOrThrow().time - firstMessage.getCreatedAtOrThrow().time

                return timeDifference > TimeUnit.MINUTES.toMillis(
                    ConsecutiveDefaultMinuteThreshold
                )
            }
        }

    }

}

