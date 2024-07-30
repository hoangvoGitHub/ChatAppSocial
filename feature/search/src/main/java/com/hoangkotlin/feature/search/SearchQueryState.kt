package com.hoangkotlin.feature.search

data class SearchQueryState(
    val query: String = "",
    val nextOffset: Int = 0,
    val limit: Int = 20
) {
    override fun toString(): String {
        return "SearchQueryState(query='$query', nextOffset=$nextOffset, limit=$limit)"
    }
}