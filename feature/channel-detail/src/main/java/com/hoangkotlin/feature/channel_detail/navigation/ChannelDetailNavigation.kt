package com.hoangkotlin.feature.channel_detail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.hoangkotlin.feature.channel_detail.ChannelDetailRoute

const val channelArg = "channelId"
const val channelRoute = "channel"
const val CHANNEL_DETAIL_GRAPH_ROUTE_PATTERN = "channels_detail"
private const val TAG = "ChannelDetailNavigation"
fun NavController.navigateToChannelDetailGraph(channelId: String, navOptions: NavOptions? = null) {
    this.navigate("channels/$channelId/detail", navOptions)
}

fun NavGraphBuilder.channelDetailGraph(
    onBackPressed: () -> Unit,
    onShowBubbleClicked: (channelId: String) -> Unit,
    onHideBubbleClicked: () -> Unit,
    onNavigateToAttachments: (channelId: String) -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit,
) {
    navigation(
        route = CHANNEL_DETAIL_GRAPH_ROUTE_PATTERN,
        arguments = listOf(),
        startDestination = "channels/{channelId}/detail"
    ) {
        composable(
            route = "channels/{channelId}/detail",
            arguments = listOf(
                navArgument(channelArg) { type = NavType.StringType },
            ),
        ) {
            ChannelDetailRoute(
                onBackPressed = onBackPressed,
                onShowBubbleClicked = onShowBubbleClicked,
                onHideBubbleClicked = onHideBubbleClicked,
                onNavigateToAttachments = onNavigateToAttachments
            )
        }
        nestedGraphs()
    }
}