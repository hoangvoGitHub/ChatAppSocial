package com.hoangkotlin.chatappsocial.core.websocket_krossbow

import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import okhttp3.Response

interface RawSocketListener {
    fun onConnecting()

    fun onOpen(user: SocialChatUser)

    fun onClosed(code: Int, reason: String)

    fun onFailure(t: Throwable, response: Response?)

    fun onEvent(event: String)
}