package com.hoangkotlin.feature.chat.navigation

import android.content.Intent
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.hoangkotlin.feature.chat.ChatRoute
import java.util.Date

private const val channelsRoute = "channels"
const val channelArg = "channelId"
const val userArg = "userId"

val uri = "https://com.hoangkotlin.socialchatapp"

fun NavController.navigateToChatGraph(
    channelId: String,
    navOptions: NavOptions? = null
) {
    this.navigate("$channelsRoute/$channelId", navOptions)
}

fun NavController.navigateWithUser(userId: String, navOptions: NavOptions? = null) {
    this.navigate("chat_graph?$userArg=$userId", navOptions)
}

fun NavController.navigateWithChannel(channelId: String, navOptions: NavOptions? = null) {
    this.navigate("channels/$channelId", navOptions)
}


fun NavGraphBuilder.chatGraph(
    onBackPressed: () -> Unit,
    onNavigateToChannelDetail: (String) -> Unit,
    onNavigateToMediaViewer: (channelId: String, attachmentUri: String, createdAt: Date) -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit,
) {

    navigation(
        route = channelsRoute,
        startDestination = "$channelsRoute/{$channelArg}"
    ) {
        composable(
            route = "$channelsRoute/{$channelArg}",
            arguments = listOf(
                navArgument(channelArg) {
                    type = NavType.StringType
                    this.defaultValue = ""
                },
            ),
            deepLinks = listOf(navDeepLink {
                uriPattern = "$uri/{$channelArg}"
                action = Intent.ACTION_MAIN
            })
        ) {
            ChatRoute(
                onBackPressed = onBackPressed,
                onHeaderActionClick = { channel ->
                    onNavigateToChannelDetail(channel.id)
                },
                onNavigateToMediaViewer = onNavigateToMediaViewer
            )
        }
        nestedGraphs()
    }
}

