package com.hoangkotlin.chatappsocial.core.offline.event

import com.hoangkotlin.chatappsocial.core.chat_client.handler.ClearConversationManager
import com.hoangkotlin.chatappsocial.core.data.model.DataResult
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.offline.state.ChatStateHolder
import com.hoangkotlin.chatappsocial.core.offline.state.asMutableState

class DefaultClearConversationManager(
    private val stateHolder: ChatStateHolder
) : ClearConversationManager {

    private val rollback = mutableMapOf<String, SocialChatChannel?>()

    override fun onClearConversationStart(channelId: String) {
        val channelToDelete = stateHolder.queryChannelsState()
            .asMutableState()
            .clearConversationHistory(channelId)
        rollback[channelId] = channelToDelete
    }

    override fun onClearConversationResult(channelId: String, result: DataResult<Unit>) {
        if (result is DataResult.Error) {
            rollback[channelId]?.let { channel ->
                stateHolder.queryChannelsState()
                    .asMutableState().upsertChannel(channel)
            }
        }else{
            stateHolder.mutableChannel(channelId).clearState()
        }
        rollback.remove(channelId)
    }

    companion object {
        private const val TAG = "DefaultClearConversatio"
    }
}