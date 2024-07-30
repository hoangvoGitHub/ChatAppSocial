package com.hoangkotlin.chatappsocial.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.feature.home.HomeRoute

const val CHANNELS_GRAPH_ROUTE_PATTERN = "channels_graph"
const val channelsRoute = "channels_route"

fun NavController.navigateToHomeGraph(navOptions: NavOptions? = null) {
    this.navigate(CHANNELS_GRAPH_ROUTE_PATTERN, navOptions)
}

fun NavGraphBuilder.channelsGraph(
    onChannelClicked: (SocialChatChannel) -> Unit,
    onNavigateToSearch: () -> Unit,
    onUserTagClick: () -> Unit,
    onSettingClick: () -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit,
) {
    navigation(
        route = CHANNELS_GRAPH_ROUTE_PATTERN,
        startDestination = channelsRoute
    ) {
        composable(route = channelsRoute) {
            HomeRoute(
                onNavigateToSearch = onNavigateToSearch,
                onChannelClick = onChannelClicked
            )
        }
        nestedGraphs()
    }
}