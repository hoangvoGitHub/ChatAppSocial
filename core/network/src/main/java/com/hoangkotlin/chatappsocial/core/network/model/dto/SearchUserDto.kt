package com.hoangkotlin.chatappsocial.core.network.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchUserDto(
    val id: String,
    val image: String? =null,
    val name: String,
    val channelId: String? = null,
    val friendStatus: String
) {

}
