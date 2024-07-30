package com.hoangkotlin.chatappsocial.core.data.repository

import com.hoangkotlin.chatappsocial.core.data.repository.channel.ChatChannelRepository
import com.hoangkotlin.chatappsocial.core.data.repository.message.ChatMessageRepository
import com.hoangkotlin.chatappsocial.core.data.repository.user.ChatUserRepository
import javax.inject.Inject

class RepositoryFacade @Inject constructor(
    private val userRepository: ChatUserRepository,
    private val messageRepository: ChatMessageRepository,
    private val channelRepository: ChatChannelRepository,
    private val searchRepository: SearchRepository
) : ChatUserRepository by userRepository,
    ChatMessageRepository by messageRepository,
    ChatChannelRepository by channelRepository,
    SearchRepository by searchRepository {

}