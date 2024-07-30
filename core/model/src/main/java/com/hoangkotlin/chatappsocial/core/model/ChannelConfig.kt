package com.hoangkotlin.chatappsocial.core.model

import java.util.Date

data class ChannelConfig(
    val createdAt: Date? = null,
    val updatedAt: Date? = null,
    val theme: ChannelThemeData
)

data class ChannelThemeData(
    val name: String,
    val thumbnail: String,
    val background: Long,
    val onBackGround: Long,
)
