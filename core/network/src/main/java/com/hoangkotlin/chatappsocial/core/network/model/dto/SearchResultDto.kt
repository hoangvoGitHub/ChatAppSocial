package com.hoangkotlin.chatappsocial.core.network.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResultDto(
    @SerialName("searchUsers")
    val searchUserDtos: List<SearchUserDto>,
    @SerialName("searchGroups")
    val searchGroupDtos: List<SearchGroupDto>,
    val nextOffset: Int
) {
}