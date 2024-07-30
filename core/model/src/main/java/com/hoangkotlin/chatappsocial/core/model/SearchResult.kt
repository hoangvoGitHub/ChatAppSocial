package com.hoangkotlin.chatappsocial.core.model

import com.hoangkotlin.chatappsocial.core.model.search.SearchGroup
import com.hoangkotlin.chatappsocial.core.model.search.SearchUser

data class SearchResult(
    val searchUsers: List<SearchUser> = emptyList(),
    val searchGroups: List<SearchGroup> = emptyList(),
) {
    override fun toString(): String {
        return "SearchResult(searchUsers=$searchUsers, searchGroups=$searchGroups)"
    }

    fun isEmpty(): Boolean = searchGroups.isEmpty() && searchUsers.isEmpty()
}




