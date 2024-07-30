package com.hoangkotlin.chatappsocial.core.chat_client.handler

import com.hoangkotlin.chatappsocial.core.data.model.DataResult

interface ClearConversationManager {

    fun onClearConversationStart(channelId: String)

    fun onClearConversationResult(channelId: String, result: DataResult<Unit>)

}
