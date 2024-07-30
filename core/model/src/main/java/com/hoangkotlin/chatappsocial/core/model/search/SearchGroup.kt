package com.hoangkotlin.chatappsocial.core.model.search

/**
 * A data holder for group channel search result
 */
data class SearchGroup(
    val id : String,
    val image: String?,
    val members: List<SearchUser> = emptyList()
) {
    override fun toString(): String {
        return "SearchGroup(id='$id', image=$image, members=$members)"
    }
}
