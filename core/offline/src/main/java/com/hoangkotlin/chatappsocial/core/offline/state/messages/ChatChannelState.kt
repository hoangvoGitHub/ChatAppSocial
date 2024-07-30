package com.hoangkotlin.chatappsocial.core.offline.state.messages

import com.hoangkotlin.chatappsocial.core.model.SocialChannelRead
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.model.SocialChatMember
import com.hoangkotlin.chatappsocial.core.model.SocialChatMessage
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import com.hoangkotlin.chatappsocial.core.model.attachment.SocialChatAttachment
import com.hoangkotlin.chatappsocial.core.offline.state.channel_list.ListStateData
import kotlinx.coroutines.flow.StateFlow


/** State container of a channel for the chat screen.*/
interface ChatChannelState {

    val channelId: String

    val channelType: String

    val quotedMessage: StateFlow<SocialChatMessage?>

    val messages: StateFlow<List<SocialChatMessage>>

    val messagesState: StateFlow<ListStateData<SocialChatMessage>>

    /** The collection of messages from previous pages of data.*/
    val oldMessages: StateFlow<List<SocialChatMessage>>

    val members: StateFlow<List<SocialChatMember>>

    val reads: StateFlow<List<SocialChannelRead>>

    /** StateFlow object with the channel data. (Does not change a lot) */
    val channelData: StateFlow<SocialChatChannel>

    val loading: StateFlow<Boolean>

    /** If we are currently loading older messages. */
    val loadingOlderMessages: StateFlow<Boolean>

    /** If we are currently loading newer messages. */
    val loadingNewerMessages: StateFlow<Boolean>

    /** If there are no more older messages to load. */
    val endOfOlderMessages: StateFlow<Boolean>

    /** If there are no more newer messages to load. */
    val endOfNewerMessages: StateFlow<Boolean>

    val mediaAttachments: StateFlow<List<SocialChatAttachment>>

    val otherAttachments: StateFlow<List<SocialChatAttachment>>

    /** Who is currently typing. Current user is excluded from this. */
    val typing: StateFlow<List<SocialChatUser>>

    /** Function that builds a channel based on data from StateFlows. */
    fun toChatChannel(): SocialChatChannel

    fun clearState()
}

fun ChatChannelState.asChatChannelMutableState(): ChatChannelMutableState? {
    return this as? ChatChannelMutableState
}


// new message? -> from an existing channel -> update this channel
//

