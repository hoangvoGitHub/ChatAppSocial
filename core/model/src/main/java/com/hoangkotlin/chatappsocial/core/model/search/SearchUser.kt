package com.hoangkotlin.chatappsocial.core.model.search

import com.hoangkotlin.chatappsocial.core.model.FriendStatus


/**
 * A data holder for user search result
 * @param channelId determines the id of the peer to peer channel between current user
 * and the searched user
 */
data class SearchUser(
    val id: String = "",
    val name: String,
    val image: String = "",
    val channelId: String? = null,
    val friendStatus: FriendStatus? = null
) {
    override fun toString(): String {
        return "SearchUser(id='$id', name='$name', image=$image, channelId=$channelId)"
    }
}
