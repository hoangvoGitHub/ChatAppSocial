package com.hoangkotlin.chatappsocial.feature.home

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import com.hoangkotlin.chatappsocial.core.network.retrofit.NetworkConfig
import com.hoangkotlin.chatappsocial.core.offline.state.channel_list.ChatChannelListState
import com.hoangkotlin.chatappsocial.core.ui.components.DefaultSearchBar
import com.hoangkotlin.chatappsocial.core.ui.components.dialog.ClearConversationConfirmationDialog
import com.hoangkotlin.chatappsocial.core.ui.components.image.SocialUserAvatar
import com.hoangkotlin.chatappsocial.core.ui.previewdatas.PreviewChannelData
import com.hoangkotlin.chatappsocial.core.ui.previewdatas.PreviewUserData
import com.hoangkotlin.chatappsocial.core.ui.theme.SocialChatAppTheme
import com.hoangkotlin.chatappsocial.feature.home.components.Channels
import com.hoangkotlin.chatappsocial.feature.home.model.BehindMotionAction
import kotlinx.coroutines.flow.collectLatest
import kotlin.random.Random

private const val TAG = "HomeScreen"

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    onNavigateToSearch: () -> Unit,
    onChannelClick: (SocialChatChannel) -> Unit,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val state by viewModel.channelsState.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    var channelDialog by remember {
        mutableStateOf<SocialChatChannel?>(null)
    }
    var openAlertDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(viewModel.toastMessage) {
        viewModel.toastMessage.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    HomeScreen(
        modifier = modifier,
        onSearchBarClick = onNavigateToSearch,
        chatChannelsState = state,
        onChannelClick = {
            onChannelClick(it)
        },
        currentUser = currentUser,
        onBehindMotionActionClick = { channel, action ->
            when (action) {
                BehindMotionAction.NotificationOn -> viewModel.toggleNotification(channel)
                BehindMotionAction.Trash -> {
                    channelDialog = channel
                    openAlertDialog = true
                }

                else -> Unit
            }
        },
        onChannelBottomReached = viewModel::loadMore

    )
    if (openAlertDialog && channelDialog != null) {
        ClearConversationConfirmationDialog(
            channel = channelDialog!!,
            currentUser = currentUser,
            onDismissRequest = { openAlertDialog = false },
            onConfirmation = viewModel::clearChannelConversation
        )
    }

}

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onSearchBarClick: () -> Unit,
    currentUser: SocialChatUser?,
    chatChannelsState: ChatChannelListState,
    onChannelClick: (SocialChatChannel) -> Unit,
    onBehindMotionActionClick: (SocialChatChannel, BehindMotionAction) -> Unit,
    onChannelBottomReached: () -> Unit,
) {

    Box(modifier = modifier) {
        if (chatChannelsState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
        ) {
            LazyRow(modifier = Modifier.padding(8.dp)) {
                items(userImages, key = { item -> item }) { item ->
                    SocialUserAvatar(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(64.dp),
                        user = SocialChatUser(
                            image = item,
                            isOnline = Random(System.currentTimeMillis()).nextBoolean()
                        ),
                    )
                }
            }
            DefaultSearchBar(
                onSearchBarClick = onSearchBarClick
            )

            Channels(
                channelsState = chatChannelsState,
                currentUser = currentUser,
                onChannelClick = onChannelClick,
                onChannelLongClick = {},
                onBehindMotionActionClick = onBehindMotionActionClick,
                verticalSpace = 4.dp,
                onChannelBottomReach = onChannelBottomReached
            )
        }
    }
}


@Preview
@Composable
fun ClearConversationConfirmationDialogPreview() {
    SocialChatAppTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(text = "Helloooooooooo", color = MaterialTheme.colorScheme.onBackground)
            ClearConversationConfirmationDialog(
                channel = PreviewChannelData.channelWithThreeUsers,
                currentUser = PreviewUserData.me,
                onConfirmation = {},
                onDismissRequest = {}
            )
        }
    }
}


@Composable
fun HomeScreenPreview(darkTheme: Boolean) {
    SocialChatAppTheme(darkTheme = darkTheme) {
        Scaffold {
            HomeScreen(
                modifier = Modifier.padding(it),
                onSearchBarClick = {},
                chatChannelsState = ChatChannelListState(
                    isLoading = false,
                    channelItems = listOf(
                        PreviewChannelData.channelWithImage,
                        PreviewChannelData.channelWithTwoUsers.copy(
                            unreadCount = 5
                        ),
                        PreviewChannelData.channelWithThreeUsers,
                    )
                ),
                onChannelClick = {},
                currentUser = PreviewUserData.user1,
                onBehindMotionActionClick = { _, _ -> },
                onChannelBottomReached = {}
            )
        }

    }

}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_MASK)
@Composable
fun HomeScreenPreviewDark() {
    HomeScreenPreview(darkTheme = true)
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun HomeScreenPreviewLight() {
    HomeScreenPreview(darkTheme = false)
}

@Preview(showBackground = true, device = Devices.PIXEL_TABLET)
@Composable
fun HomeScreenPreviewTablet() {
    HomeScreenPreview(darkTheme = true)
}


val userImages = listOf(
    "https://plus.unsplash.com/premium_photo-1683121366070-5ceb7e007a97?q=80&w=1770&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    "https://images.unsplash.com/photo-1633332755192-727a05c4013d?q=80&w=1780&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?q=80&w=1780&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    "https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91?q=80&w=1770&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    "https://plus.unsplash.com/premium_photo-1675626492183-865d6d8e2e8a?q=80&w=1887&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?q=80&w=1887&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    "https://plus.unsplash.com/premium_photo-1671656349322-41de944d259b?q=80&w=1887&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
)