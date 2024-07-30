package com.hoangkotlin.chatappsocial.core.common

sealed class ChatGraphNavigationOption {
    data class WithChannel(val channelId: String) : ChatGraphNavigationOption()
    data class WithUser(val userId: String) : ChatGraphNavigationOption()
}