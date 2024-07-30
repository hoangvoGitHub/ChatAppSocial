package com.hoangkotlin.chatappsocial.feature.friend.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.hoangkotlin.chatappsocial.feature.friend.FriendRoute

const val friendsRoute = "friend_route"
private const val FRIENDS_GRAPH_ROUTE_PATTERN = "friend_graph"

fun NavController.navigateToFriendsGraph(navOptions: NavOptions? = null) {
    this.navigate(FRIENDS_GRAPH_ROUTE_PATTERN, navOptions)
}

fun NavGraphBuilder.friendsGraph(
    onNavigateToSearch: () -> Unit,
    onNavigateToChannel: (String) -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit,
) {
    navigation(
        route = FRIENDS_GRAPH_ROUTE_PATTERN,
        startDestination = friendsRoute
    ) {
        composable(route = friendsRoute) {
            FriendRoute(
                onSearchBarClick = onNavigateToSearch,
                onFriendWithChannelClick = onNavigateToChannel
            )
        }
        nestedGraphs()
    }
}
