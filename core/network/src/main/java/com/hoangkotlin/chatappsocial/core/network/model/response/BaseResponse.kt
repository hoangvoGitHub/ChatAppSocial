package com.hoangkotlin.chatappsocial.core.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse < out T> (
    val status: String? = null,
    val message: String? = null,
    val data: T?
){
}