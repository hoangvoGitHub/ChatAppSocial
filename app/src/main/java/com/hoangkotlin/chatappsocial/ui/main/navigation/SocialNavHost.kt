package com.hoangkotlin.chatappsocial.ui.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.ui.utils.LocalRunningForBubble
import com.hoangkotlin.chatappsocial.feature.auth.navigation.AUTH_GRAPH_ROUTE_PATTERN
import com.hoangkotlin.chatappsocial.feature.auth.navigation.navigateToAuthGraph
import com.hoangkotlin.chatappsocial.feature.friend.navigation.friendsGraph
import com.hoangkotlin.chatappsocial.feature.home.navigation.CHANNELS_GRAPH_ROUTE_PATTERN
import com.hoangkotlin.chatappsocial.feature.home.navigation.channelsGraph
import com.hoangkotlin.chatappsocial.feature.media_viewer.navigation.mediaViewerScreen
import com.hoangkotlin.chatappsocial.feature.media_viewer.navigation.navigateToMediaViewerScreen
import com.hoangkotlin.chatappsocial.feature.profile.navigation.profileGraph
import com.hoangkotlin.chatappsocial.ui.main.SocialAppState
import com.hoangkotlin.feature.channel_detail.navigation.channelDetailGraph
import com.hoangkotlin.feature.channel_detail.navigation.navigateToChannelDetailGraph
import com.hoangkotlin.feature.channel_detail.nested.attachment.attachmentsScreen
import com.hoangkotlin.feature.channel_detail.nested.attachment.navigateToAttachments
import com.hoangkotlin.feature.chat.navigation.chatGraph
import com.hoangkotlin.feature.chat.navigation.navigateToChatGraph
import com.hoangkotlin.feature.search.navigation.navigateToSearch
import com.hoangkotlin.feature.search.navigation.searchScreen

private const val TAG = "SocialNavHost"

@Composable
fun SocialNavHost(
    appState: SocialAppState,
    modifier: Modifier = Modifier,
    startDestination: String = CHANNELS_GRAPH_ROUTE_PATTERN,
    onChannelClicked: (SocialChatChannel) -> Unit = {},
    onNavigateBackFromChannel: () -> Unit = {},
    onToggleBubbleClicked: (channelId: String) -> Unit
) {
    val navController = appState.navController
//    val viewModelStoreOwner = LocalViewModelStoreOwner.current
//    Log.d(TAG, "SocialNavHost: viewModelStoreOwner: ${viewModelStoreOwner.hashCode()} ")

    val isCalledFromBubble = LocalRunningForBubble.current

    val onNavigateBackFromChannelTrigger = {
        navController.popBackStack()
        if (isCalledFromBubble) {
            onNavigateBackFromChannel()
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        channelsGraph(
            onChannelClicked = {
                onChannelClicked(it)
                navController.navigateToChatGraph(it.id)
            },
            onNavigateToSearch = navController::navigateToSearch,
            onUserTagClick = {},
            onSettingClick = {},
            nestedGraphs = {
                mediaViewerScreen(onBackPress = navController::popBackStack)

                chatGraph(
                    onBackPressed = onNavigateBackFromChannelTrigger,
                    onNavigateToChannelDetail = {
                        navController.navigateToChannelDetailGraph(it)
                    },
                    onNavigateToMediaViewer = navController::navigateToMediaViewerScreen
                ) {}

                channelDetailGraph(
                    onBackPressed = navController::popBackStack,
                    onShowBubbleClicked = onToggleBubbleClicked,
                    onHideBubbleClicked = {},
                    onNavigateToAttachments = navController::navigateToAttachments
                ) {
                    attachmentsScreen(
                        onBackPressed = navController::popBackStack,
                        onNavigateToMediaViewer = navController::navigateToMediaViewerScreen
                    )
                }
            })

        profileGraph {

        }
        friendsGraph(
            onNavigateToSearch = {
                navController.navigateToSearch()
            },
            onNavigateToChannel = {
                navController.navigateToChatGraph(channelId = it)
            }
        ) {

        }
        searchScreen(
            onAuthError = {
                navController.navigateToAuthGraph(navOptions {
                    popUpTo(AUTH_GRAPH_ROUTE_PATTERN)
                })
            },
            onBackClick = navController::popBackStack,
            onUserClick = navController::navigateToChatGraph
        )


    }

}