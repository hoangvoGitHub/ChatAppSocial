package com.hoangkotlin.chatappsocial.feature.home.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import com.hoangkotlin.chatappsocial.core.model.getDisplayName
import com.hoangkotlin.chatappsocial.core.model.getLastMessageText
import com.hoangkotlin.chatappsocial.core.ui.components.DragAnchors
import com.hoangkotlin.chatappsocial.core.ui.components.LoadingIndicator
import com.hoangkotlin.chatappsocial.core.ui.components.SwipeForActions
import com.hoangkotlin.chatappsocial.core.ui.components.Timestamp
import com.hoangkotlin.chatappsocial.core.ui.components.image.SocialChannelAvatar
import com.hoangkotlin.chatappsocial.core.ui.previewdatas.PreviewChannelData
import com.hoangkotlin.chatappsocial.core.ui.previewdatas.PreviewUserData
import com.hoangkotlin.chatappsocial.core.ui.theme.SocialChatAppTheme
import com.hoangkotlin.chatappsocial.feature.home.model.BehindMotionAction
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val DefaultBehindMotionActions = listOf<BehindMotionAction>(
    BehindMotionAction.NotificationOn,
    BehindMotionAction.Trash
)

@Composable
fun Channels(
    modifier: Modifier = Modifier,
    channelsState: com.hoangkotlin.chatappsocial.core.offline.state.channel_list.ChatChannelListState,
    currentUser: SocialChatUser?,
    onChannelClick: (SocialChatChannel) -> Unit,
    onChannelLongClick: (SocialChatChannel) -> Unit,
    onBehindMotionActionClick: (SocialChatChannel, BehindMotionAction) -> Unit,
    onChannelBottomReach: () -> Unit,
    verticalSpace: Dp = 8.dp
) {

    val (_, isLoadingMore, endOfChannels, channels) = channelsState


    var focusedChannelId by remember {
        mutableStateOf(channels.firstOrNull()?.id)
    }

    val lazyColumnListState = rememberLazyListState()


    LazyColumn(
        modifier = modifier,
        state = lazyColumnListState,
        verticalArrangement = Arrangement.spacedBy(verticalSpace)
    ) {

        itemsIndexed(channels.toMutableList(), key = { _, item -> item.id }) { index, channel ->

            if (!endOfChannels && index == channels.lastIndex &&
                channels.isNotEmpty() &&
                lazyColumnListState.isScrollInProgress
            ) {
                onChannelBottomReach()
            }

            ChatChannelItem(
                modifier = Modifier.padding(bottom = 8.dp),
                currentUser = currentUser,
                channel = channel,
                onChannelClick = onChannelClick,
                onChannelLongClick = onChannelLongClick,
                isFocused = focusedChannelId == channel.id,
                onBehindMotionActionClick = onBehindMotionActionClick,
                onDrag = { id ->
                    focusedChannelId = id
                }
            )
        }

        if (isLoadingMore) {
            item {
                DefaultChannelsLoadingMoreIndicator()
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatChannelItem(
    modifier: Modifier = Modifier,
    currentUser: SocialChatUser?,
    channel: SocialChatChannel,
    isFocused: Boolean = false,
    onChannelClick: (SocialChatChannel) -> Unit,
    onChannelLongClick: (SocialChatChannel) -> Unit,
    onBehindMotionActionClick: (SocialChatChannel, BehindMotionAction) -> Unit,
    onDrag: (String) -> Unit = {},
) {

    val fontWeight = FontWeight.Bold.takeIf { channel.unreadCount > 0 }
    val coroutineScope = rememberCoroutineScope()
    val endContentWidth = calculateEndContentWidthInt().dp
    val unreadCount = channel.unreadCount

    SwipeForActions(
        modifier = modifier,
        onDrag = {
            onDrag(channel.id)
        },
        endContentWidth = endContentWidth,
        endContent = { anchoredDraggableState, _ ->
            DefaultBehindMotionActions.forEach { action ->
                BehindMotionActionIcon(
                    action = action,
                    onClick = {
                        onBehindMotionActionClick(channel, it)
                        coroutineScope.launch {
                            delay(300L)
                            anchoredDraggableState.animateTo(DragAnchors.Center)
                        }
                    },
                    isMute = channel.isMuted
                )
            }
        }
    ) { anchoredDraggableState, _, _ ->
        LaunchedEffect(isFocused) {
            if (!isFocused) {
                anchoredDraggableState.animateTo(DragAnchors.Center)
            }
        }
        Row(
            modifier = Modifier
                .combinedClickable(
                    onClick = {
                        onChannelClick(channel)
                    },
                    onLongClick = {
                        onChannelLongClick(channel)
                    }
                )
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DefaultChannelAvatar(
                channel = channel,
                currentUser = currentUser
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                DefaultChannelMainContent(
                    channel = channel,
                    currentUser = currentUser,
                    fontWeight = fontWeight
                )

                DefaultChannelLastMessageContent(
                    channel = channel,
                    currentUser = currentUser,
                    fontWeight = fontWeight
                )

            }
            if (unreadCount > 0) {
                DefaultUnreadCountIndicatorContainer(unreadCount = unreadCount)
            }

        }

    }

}

@Composable
fun DefaultChannelMainContent(
    modifier: Modifier = Modifier,
    channel: SocialChatChannel,
    currentUser: SocialChatUser?,
    fontWeight: FontWeight?
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DefaultChannelName(
            modifier = Modifier,
            channel = channel,
            currentUser = currentUser,
            fontWeight = fontWeight
        )
        if (channel.isMuted) {
            ChannelMuteIcon()
        }
    }
}

@Composable
fun ChannelMuteIcon(modifier: Modifier = Modifier) {
    Icon(
        modifier = modifier
            .padding(start = 8.dp)
            .size(16.dp),
        imageVector = Icons.AutoMirrored.Filled.VolumeOff,
        contentDescription = null,
    )

}

@Composable
fun DefaultChannelLastMessageContent(
    modifier: Modifier = Modifier,
    channel: SocialChatChannel,
    currentUser: SocialChatUser?,
    fontWeight: FontWeight?
) {
    val lastMessageText = channel.getLastMessageText(currentUser)
    if (lastMessageText != null) {
        Row(modifier = modifier) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(align = Alignment.Start)
                    .padding(end = 8.dp),
                text = lastMessageText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelMedium.merge(fontWeight = fontWeight),
            )

            Timestamp(
                date = channel.lastMessage!!.createdAt,
                fontWeight = fontWeight,
            )

        }
    }
}

@Composable
fun DefaultUnreadCountIndicatorContainer(
    modifier: Modifier = Modifier,
    unreadCount: Int
) {
    UnreadCountIndicator(
        modifier = modifier.padding(8.dp),
        unreadCount = unreadCount,
        color = if (unreadCount > 0) Color.Red else Color.Transparent
    )
}

@Composable
fun DefaultChannelName(
    modifier: Modifier = Modifier,
    channel: SocialChatChannel,
    currentUser: SocialChatUser?,
    fontWeight: FontWeight?
) {
    Text(
        modifier = modifier,
        text = channel.getDisplayName(
            LocalContext.current, currentUser
        ),
        maxLines = 1,
        style = MaterialTheme.typography.labelLarge.merge(fontWeight = fontWeight),
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
fun DefaultChannelAvatar(
    modifier: Modifier = Modifier,
    channel: SocialChatChannel,
    currentUser: SocialChatUser?
) {
    SocialChannelAvatar(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .size(56.dp),
        channel = channel,
        currentUser = currentUser
    )
}

@Composable
fun DefaultChannelsLoadingMoreIndicator() {
    LoadingIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
    )
}

private fun calculateEndContentWidthInt(): Int {
    return DefaultBehindMotionActions.size *
            (BehindMotionActionIconSize + BehindMotionActionIconPadding * 2)

}

@Preview
@Composable
fun ChatChannelItemPreview() {
    SocialChatAppTheme {
        ChatChannelItem(currentUser = PreviewUserData.user4,
            channel = PreviewChannelData.channelWithImage,
            onChannelLongClick = {},
            onChannelClick = {},
            onBehindMotionActionClick = { _, _ -> }
        )
    }
}

@Preview(showBackground = true, name = "ChannelItem Preview (Channel with unread)")
@Composable
private fun ChannelItemForChannelWithUnreadMessagesPreview() {
    SocialChatAppTheme {
        ChatChannelItem(currentUser = PreviewUserData.user4,
            channel = PreviewChannelData.channelWithTwoUsers.copy(
                unreadCount = 3, lastMessage = PreviewChannelData.longMessage
            ),
            onChannelLongClick = {},
            onChannelClick = {},
            onBehindMotionActionClick = { _, _ -> })

    }

}

@Preview(showBackground = true, device = Devices.PIXEL_TABLET)
@Composable
fun ChannelItemForChannelWithUnreadMessagesPreviewTablet() {
    ChannelItemForChannelWithUnreadMessagesPreview()
}

@Preview(showBackground = true, name = "ChannelItem Preview (Channel with last message)")
@Composable
private fun ChannelItemForChannelLastMessagesPreview() {
    SocialChatAppTheme {
        ChatChannelItem(currentUser = PreviewUserData.user4,
            channel = PreviewChannelData.channelWithTwoUsers.copy(
                lastMessage = PreviewChannelData.longMessage.copy(user = PreviewUserData.user4)
            ),
            onChannelLongClick = {},
            onChannelClick = {},
            onBehindMotionActionClick = { _, _ -> })

    }

}
