package com.hoangkotlin.chatappsocial.core.network.model.request

import com.hoangkotlin.chatappsocial.core.network.utils.PageableRequestSerializer
import com.hoangkotlin.chatappsocial.core.network.utils.ResourceSerializer
import kotlinx.serialization.Serializable

private const val TAG = "SearchResourceRequest"

@Serializable
enum class SearchResource {
    Channels,
    Users,
    Messages,
}

/**
 * Represents a request for pagination with default values for limit and offset.
 *
 * @property limit The maximum number of items to return per page.
 * @property offset The offset from the beginning of the dataset.
 */
@Serializable(with = PageableRequestSerializer::class)
data class PageableRequest(
    val limit: Int = 20,
    val offset: Int = 0
)

@Serializable
data class SearchResourceRequest(
    val searchQuery: String,
    @Serializable(with = ResourceSerializer::class)
    val resource: Map<SearchResource, PageableRequest>
)








