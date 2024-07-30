package com.hoangkotlin.chatappsocial.core.network.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class SearchGroupDto (
    val id : String,
    val image: String?,
    val members: List<SearchUserDto> = emptyList()
){

}
