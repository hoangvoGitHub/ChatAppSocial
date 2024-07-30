package com.hoangkotlin.chatappsocial.core.chat_client.utils

object TimeProvider {
    private const val MILLIS_TO_SECONDS_FACTOR = 1_000L
    fun provideCurrentTimeInSeconds(): Long =
        System.currentTimeMillis() / MILLIS_TO_SECONDS_FACTOR

    fun provideCurrentTimeInMilliseconds(): Long = System.currentTimeMillis()
}
