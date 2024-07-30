package com.hoangkotlin.chatappsocial.core.offline.state.messages

import android.util.Log
import com.hoangkotlin.chatappsocial.core.chat_client.extension.isMedia
import com.hoangkotlin.chatappsocial.core.chat_client.extension.uploadId
import com.hoangkotlin.chatappsocial.core.common.utils.StateFlowConfig
import com.hoangkotlin.chatappsocial.core.model.SocialChannelRead
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.model.SocialChatMember
import com.hoangkotlin.chatappsocial.core.model.SocialChatMessage
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import com.hoangkotlin.chatappsocial.core.model.attachment.SocialChatAttachment
import com.hoangkotlin.chatappsocial.core.offline.state.channel_list.ListStateData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


private const val TAG = "ChatChannelMutableState"

class ChatChannelMutableState(
    override val channelId: String,
    override val channelType: String = "",
    private val scope: CoroutineScope,
    private val userFlow: StateFlow<SocialChatUser?>,
    initialChannel: SocialChatChannel?
) : ChatChannelState {


    private val _messages = MutableStateFlow(
        initialChannel?.messages?.associateBy(SocialChatMessage::id) ?: emptyMap()
    )

    private val _rawReads =
        MutableStateFlow(initialChannel?.reads?.associateBy {
            it.user.id
        } ?: emptyMap())

    private val _quotedMessage = MutableStateFlow<SocialChatMessage?>(null)

    // TODO: remove
    private val _members = MutableStateFlow<Map<String, SocialChatMember>>(emptyMap())

    // TODO: remove
    private val _oldMessages = MutableStateFlow<Map<String, SocialChatMessage>>(emptyMap())

    private val _endOfNewerMessages = MutableStateFlow(false)

    private val _endOfOlderMessages = MutableStateFlow(false)

    private val _channelData = MutableStateFlow<SocialChatChannel?>(initialChannel)

    // TODO: remove
    private val _loadingOlderMessages = MutableStateFlow(false)

    // TODO: remove
    private val _loadingNewerMessages = MutableStateFlow(false)

    private val _loading = MutableStateFlow(false)

    private val _typing = MutableStateFlow<Map<String, SocialChatUser>>(emptyMap())


    val sortedVisibleMessages: StateFlow<List<SocialChatMessage>> =
        messagesTransformation(_messages.map { it.values }).stateIn(
            scope,
            SharingStarted.Eagerly,
            emptyList()
        )

    override val messagesState: StateFlow<ListStateData<SocialChatMessage>>
        get() = combine(
            _loading,
            sortedVisibleMessages,
        ) { loading: Boolean, messages: List<SocialChatMessage> ->
            when {
                loading -> ListStateData.Loading
                messages.isEmpty() -> ListStateData.OfflineNoData
                else -> ListStateData.Result(messages)
            }
        }.stateIn(scope, SharingStarted.Eagerly, ListStateData.NoQueryActive)

    // TODO: Remove
    override val quotedMessage: StateFlow<SocialChatMessage?>
        get() = _quotedMessage

    override val messages: StateFlow<List<SocialChatMessage>>
        get() = sortedVisibleMessages

    override val reads: StateFlow<List<SocialChannelRead>>
        get() = _rawReads
            .map { it.values.sortedBy(SocialChannelRead::lastReadAt) }
            .stateIn(scope, SharingStarted.Eagerly, emptyList())

    override val oldMessages: StateFlow<List<SocialChatMessage>>
        get() = TODO("Not yet implemented")
    override val members: StateFlow<List<SocialChatMember>>
        get() = TODO("Not yet implemented")
    override val channelData: StateFlow<SocialChatChannel>
        get() = _channelData.filterNotNull().stateIn(
            scope, SharingStarted.Eagerly,
            SocialChatChannel(id = channelId, type = channelType)
        )
    override val loading: StateFlow<Boolean>
        get() = _loading

    override val loadingOlderMessages: StateFlow<Boolean>
        get() = _loadingOlderMessages

    override val loadingNewerMessages: StateFlow<Boolean>
        get() = _loadingNewerMessages

    override val endOfOlderMessages: StateFlow<Boolean>
        get() = _endOfOlderMessages

    override val endOfNewerMessages: StateFlow<Boolean>
        get() = _endOfNewerMessages


    private val _messageAttachments: StateFlow<Map<String, SocialChatAttachment>> =
        _messages.map { messageMap ->
            messageMap.values.flatMap(SocialChatMessage::attachments).also {
                Log.d(TAG, "_messageAttachments: $it")
            }.associateByFilter(
                keySelector = { it.uploadId!! },
                predicate = { it.uploadId != null }
            )
        }.stateIn(scope, SharingStarted.Eagerly, emptyMap())

    private val _newAddedAttachments =
        MutableStateFlow<Map<String, SocialChatAttachment>>(emptyMap())

    private val _attachments: Flow<Map<String, SocialChatAttachment>> =
        combine(
            _messageAttachments,
            _newAddedAttachments
        ) { messageAttachments, a ->
            messageAttachments.toMutableMap().apply {
                this += a
            }
        }


    private fun upsertAttachments(attachment: List<SocialChatAttachment>) {
        _newAddedAttachments.value += attachment.associateByFilter(
            keySelector = { it.uploadId!! },
            predicate = { it.uploadId != null }
        )
    }

    override val mediaAttachments: StateFlow<List<SocialChatAttachment>>
        get() = _attachments.map { attachments ->
            attachments.values.asSequence()
                .filter(SocialChatAttachment::isMedia)
                .sortedByDescending(SocialChatAttachment::createdAt)
                .toList()
        }
            .stateIn(
                scope,
                SharingStarted.WhileSubscribed(StateFlowConfig.StateFlowSubscribeTimeout),
                emptyList()
            )


    override val otherAttachments: StateFlow<List<SocialChatAttachment>>
        get() = _attachments.map { attachments ->
            attachments.values.asSequence()
                .filterNot(SocialChatAttachment::isMedia)
                .sortedByDescending(SocialChatAttachment::createdAt)
                .toList()
        }
            .stateIn(
                scope,
                SharingStarted.WhileSubscribed(StateFlowConfig.StateFlowSubscribeTimeout),
                emptyList()
            )

    override val typing: StateFlow<List<SocialChatUser>>
        get() = _typing.map { it.values.toList() }.stateIn(
            scope,
            SharingStarted.Eagerly,
            emptyList()
        )

    override fun toChatChannel(): SocialChatChannel {
        return _channelData.value ?: SocialChatChannel()
    }

    private fun messagesTransformation(messages: Flow<Collection<SocialChatMessage>>): StateFlow<List<SocialChatMessage>> {
        return messages.combine(userFlow) { messageCollection, _ ->
            messageCollection.asSequence()
//                .filter { it.user.id == user?.id }
                .sortedBy { it.createdAt ?: it.createdLocallyAt }
                .toList()
        }.stateIn(scope, SharingStarted.Eagerly, emptyList())
    }

//    private val channelEventHandler = ChatChannelEventHandler(this@ChatChannelMutableState)

    fun upsertMessages(newMessages: Collection<SocialChatMessage>) {
        _messages.value += newMessages.associateBy(SocialChatMessage::id)
    }

    fun upsertMessage(newMessage: SocialChatMessage) {
        upsertMessages(listOf(newMessage))
    }

    fun setChannelData(channelData: SocialChatChannel) {
        _channelData.value = channelData
    }

    fun setEndOfOlderMessages(isEnd: Boolean) {
        _endOfOlderMessages.value = isEnd
    }

    fun upsertReads(reads: List<SocialChannelRead>) {
        for (read in reads) {
            val currentReadOfUser = _rawReads.value[read.user.id]
            if (currentReadOfUser == null) {
                _rawReads.value += (read.user.id to read)
                continue
            }

            // only update read if the new read state is after the current read state
            if (currentReadOfUser.lastReadAt != null &&
                read.lastReadAt?.after(currentReadOfUser.lastReadAt) == true
            ) {
                _rawReads.value += Pair(read.user.id, read)
            }
        }
    }

    fun addTyping(user: SocialChatUser) {
        _typing.value += user.id to user
    }

    fun removeTyping(user: SocialChatUser) {
        _typing.value -= user.id
    }

    fun upsertRead(read: SocialChannelRead) {
        upsertReads(listOf(read))
    }

    override fun clearState() {
        _messages.value = emptyMap()
        _endOfNewerMessages.value = false
        _endOfOlderMessages.value = false
    }
}

inline fun <T, K> Iterable<T>.associateByFilter(
    keySelector: (T) -> K,
    predicate: (T) -> Boolean
): Map<K, T> {
    val outputMap = mutableMapOf<K, T>()
    this.forEach { item ->
        if (predicate(item)) {
            outputMap[keySelector(item)] = item
        }
    }
    return outputMap
}



