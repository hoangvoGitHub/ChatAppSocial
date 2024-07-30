package com.hoangkotlin.feature.search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.hoangkotlin.chatappsocial.core.network.model.request.SearchResource
import com.hoangkotlin.feature.search.SearchRoute

const val resourcesArg = "stringArray"
const val searchRoute =
    "search_route?$resourcesArg={$resourcesArg}"
private const val SEARCH_GRAPH_ROUTE_PATTERN = "search_graph?"
private val defaultSearchResources = SearchResource.entries.map(SearchResource::name).toTypedArray()
fun NavController.navigateToSearch(
    navOptions: NavOptions? = null
) {
        return this.navigate(searchRoute, navOptions)

}

fun NavGraphBuilder.searchScreen(
    onBackClick: () -> Unit,
    onUserClick: (String) -> Unit,
    onAuthError: () -> Unit,
) {
    // TODO: Handle back stack for each top-level destination. At the moment each top-level
    // destination may have own search screen's back stack.
    composable(
        route = searchRoute,
    ) {
        SearchRoute(
            onBackClick = onBackClick,
            onNavigateToChannel = onUserClick,
            onAuthError = onAuthError
        )
    }
}

fun Collection<SearchResource>.toNavArgument(): String {
    return this.joinToString(separator = "&") { resource ->
        "$resourcesArg=${resource.name}"
    }
}