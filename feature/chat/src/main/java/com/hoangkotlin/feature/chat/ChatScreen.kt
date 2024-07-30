package com.hoangkotlin.feature.chat

import android.content.res.Configuration
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoangkotlin.chatappsocial.core.chat_client.extension.isMedia
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.model.SocialChatMessage
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import com.hoangkotlin.chatappsocial.core.model.attachment.SocialChatAttachment
import com.hoangkotlin.chatappsocial.core.model.attachment.UploadState
import com.hoangkotlin.chatappsocial.core.model.getDisplayName
import com.hoangkotlin.chatappsocial.core.model.isGroup
import com.hoangkotlin.chatappsocial.core.offline.state.messages.ChatMessageItemState
import com.hoangkotlin.chatappsocial.core.offline.state.messages.DateSeparatorState
import com.hoangkotlin.chatappsocial.core.offline.state.messages.MessagesState
import com.hoangkotlin.chatappsocial.core.ui.components.BackButton
import com.hoangkotlin.chatappsocial.core.ui.components.LoadingIndicator
import com.hoangkotlin.chatappsocial.core.ui.components.Timestamp
import com.hoangkotlin.chatappsocial.core.ui.components.image.SocialChannelAvatar
import com.hoangkotlin.chatappsocial.core.ui.components.image.mirrorRtl
import com.hoangkotlin.chatappsocial.core.ui.extensions.drawShadow
import com.hoangkotlin.chatappsocial.core.ui.extensions.shimmerEffect
import com.hoangkotlin.chatappsocial.core.ui.previewdatas.PreviewChannelData
import com.hoangkotlin.chatappsocial.core.ui.previewdatas.PreviewUserData
import com.hoangkotlin.chatappsocial.core.ui.theme.SocialChatAppTheme
import com.hoangkotlin.feature.chat.components.composer.MessageComposer
import com.hoangkotlin.feature.chat.components.message.MessageItemFromMe
import com.hoangkotlin.feature.chat.components.message.MessageItemFromOther
import com.hoangkotlin.feature.chat.model.ComposerUtility
import com.hoangkotlin.feature.chat.utils.AttachmentsPickerDelegate
import java.util.Date
import com.hoangkotlin.chatappsocial.core.ui.R as uiR


private const val TAG = "ChatScreen"

@Composable
fun ChatRoute(
    onBackPressed: () -> Unit,
    onHeaderActionClick: (SocialChatChannel) -> Unit = {},
    onNavigateToMediaViewer: (channelId: String, attachmentUri: String, createdAt: Date) -> Unit,
) {
    val chatViewModel: ChatViewModel = hiltViewModel()
    val composerViewModel: MessageInputViewModel = hiltViewModel()
    val imagePickerLauncher =
        AttachmentsPickerDelegate.rememberLauncherForImagePickerActivityResult(
            onUrisResult = composerViewModel::addSelectedAttachments
        )

    val channel by chatViewModel.channel.collectAsStateWithLifecycle()
    val messagesState by chatViewModel.messagesState.collectAsStateWithLifecycle()
    val currentUser by chatViewModel.currentUser.collectAsStateWithLifecycle()
    val inputState by composerViewModel.messageInputState.collectAsStateWithLifecycle()


    ChatScreen(
        channel = channel,
        messagesState = messagesState,
        onMessageChange = composerViewModel::onMessageChange,
        onSendMessage = composerViewModel::sendMessage,
        onBackPressed = onBackPressed,
        currentUser = currentUser,
        onSwipeAction = { quotedMessage ->
            composerViewModel.setInputAction(Reply(message = quotedMessage))
        },
        inputState = inputState,
        onRemoveQuotedMessage = { composerViewModel.setInputAction(null) },
        onHeaderActionClick = onHeaderActionClick,
        onMessagesTopReached = chatViewModel::loadMore,
        onUtilityClick = { utility ->
            when (utility) {
                ComposerUtility.Camera -> {}
                ComposerUtility.Gallery -> {
                    imagePickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }

                ComposerUtility.Voice -> {}
                ComposerUtility.File -> {
                }

                else -> {

                }
            }
        },
        onRemoveAttachment = composerViewModel::removeAttachment,
        onLastVisibleMessageChanged = chatViewModel::onLastVisibleMessageChanged,
        onAttachmentClick = { attachment ->
            if (attachment.isMedia
                && attachment.uploadState == UploadState.Success
            ) {
                attachment.createdAt?.let { createdAt ->
                    Log.d(TAG, "ChatRoute: ${attachment.imageUrl}")
                    onNavigateToMediaViewer(
                        channel.id,
                        attachment.upload?.path
                            ?: attachment.url
                            ?: attachment.imageUrl
                            ?: "",
                        createdAt
                    )
                }

            }
        }

    )
}

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    channel: SocialChatChannel,
    messagesState: MessagesState,
    currentUser: SocialChatUser?,
    onBackPressed: () -> Unit,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onSwipeAction: (SocialChatMessage) -> Unit,
    inputState: MessageInputState,
    onRemoveQuotedMessage: () -> Unit,
    onHeaderActionClick: (SocialChatChannel) -> Unit = {},
    onMessagesTopReached: () -> Unit,
    onUtilityClick: (ComposerUtility) -> Unit,
    onRemoveAttachment: (SocialChatAttachment) -> Unit,
    onLastVisibleMessageChanged: (SocialChatMessage) -> Unit,
    onAttachmentClick: (SocialChatAttachment) -> Unit,
) {
    val localFocusManager = LocalFocusManager.current

    Scaffold(modifier = modifier.fillMaxSize(), topBar = {
        if (channel.id.isBlank()) {
            ShimmerChatHeader(onBackPressed = onBackPressed)
        } else {
            ChatHeader(
                onBackPressed = onBackPressed,
                channel = channel,
                currentUser = currentUser,
                onHeaderActionClick = onHeaderActionClick
            )
        }

    }, bottomBar = {
        if (channel.id.isBlank()) {
            ShimmerMessageComposer()
        } else {
            DefaultMessageComposer(
                onMessageChange = onMessageChange,
                onSendMessage = onSendMessage,
                inputState = inputState,
                onRemoveQuotedMessage = onRemoveQuotedMessage,
                onUtilityClick = onUtilityClick,
                onRemoveAttachment = onRemoveAttachment
            )
        }
    }) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Messages(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
                    .pointerInput(Unit) {
                        detectTapGestures {
                            localFocusManager.clearFocus()
                        }
                    },
                messagesState = messagesState,
                currentUser = currentUser,
                isMessageInGroup = channel.isGroup,
                onSwipeAction = onSwipeAction,
                onLastVisibleMessageChanged = onLastVisibleMessageChanged,
                onMessagesTopReached = onMessagesTopReached,
                onAttachmentClick = onAttachmentClick
            )

            if (messagesState.isLoading) {
                DefaultMessagesLoadingMoreIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

    }
}

@Composable
fun Messages(
    modifier: Modifier = Modifier,
    messagesState: MessagesState,
    currentUser: SocialChatUser?,
    isMessageInGroup: Boolean = false,
    onSwipeAction: (SocialChatMessage) -> Unit = {},
    onMessagesTopReached: () -> Unit,
    onAttachmentClick: (SocialChatAttachment) -> Unit,
    onLastVisibleMessageChanged: (SocialChatMessage) -> Unit,
) {
    val (_, isLoadingMore, endOfMessages, messages) = messagesState
    val lazyColumnListState = rememberLazyListState()
    OnLastVisibleItemChanged(lazyColumnListState) { messageIndex ->
        val message = messagesState.messageItems.getOrNull(messageIndex)

        if (message is ChatMessageItemState) {
            onLastVisibleMessageChanged(message.message)
        }
    }

    LaunchedEffect(messages.size) {
        if (!lazyColumnListState.isScrollInProgress &&
            messages.isNotEmpty() &&
            !messagesState.isLoadingMore
        ) {
            lazyColumnListState.animateScrollToItem(0)
        }
    }

    LazyColumn(
        state = lazyColumnListState,
        modifier = modifier
            .fillMaxSize(),
        reverseLayout = true
    ) {
        itemsIndexed(messages, key = { _, item ->
            if (item is ChatMessageItemState) item.message.id else item.toString()
        }) { index, item ->
            if (!endOfMessages && index == messages.lastIndex &&
                messages.isNotEmpty() &&
                lazyColumnListState.isScrollInProgress
            ) {
                onMessagesTopReached()
            }
            when (item) {
                is DateSeparatorState -> DateSeparatorItemContainer(
                    dateSeparator = item
                )

                is ChatMessageItemState -> MessageItemContainer(
                    modifier = Modifier,
                    messageItemState = item,
                    onSwipeAction = onSwipeAction,
                    currentUser = currentUser,
                    isMessageInGroup = isMessageInGroup,
                    onAttachmentClick = onAttachmentClick
                )
            }

        }

        if (isLoadingMore) {
            item {
                DefaultMessagesLoadingMoreIndicator()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHeader(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
    channel: SocialChatChannel,
    currentUser: SocialChatUser?,
    onCallClicked: (SocialChatChannel) -> Unit = {},
    onVideoCallClicked: (SocialChatChannel) -> Unit = {},
    onHeaderActionClick: (SocialChatChannel) -> Unit = {},
) {
    Surface(
        modifier = modifier, shadowElevation = 5.dp,
        color = MaterialTheme.colorScheme.background

    ) {
        TopAppBar(
            modifier = Modifier.padding(8.dp),
            navigationIcon = {
                DefaultChatScreenBackButton(onBackPressed = onBackPressed)
            },
            title = {
                DefaultChatHeaderMainContent(
                    channel = channel,
                    currentUser = currentUser,
                    onHeaderActionClick = onHeaderActionClick
                )
            },
            actions = {
                VoiceCallActionButton(onCallClicked = {
                    onCallClicked(channel)
                })
                VideoCallActionButton(
                    onVideoCallClick = {
                        onVideoCallClicked(channel)
                    }
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
        )
    }
}

@Composable
fun VoiceCallActionButton(
    modifier: Modifier = Modifier,
    onCallClicked: () -> Unit,
) {
    IconButton(
        modifier = modifier,
        onClick = onCallClicked
    ) {
        Icon(
            painter = painterResource(id = uiR.drawable.phone_svgrepo_com), contentDescription = "",
        )
    }
}

@Composable
fun VideoCallActionButton(
    modifier: Modifier = Modifier,
    onVideoCallClick: () -> Unit,
) {
    IconButton(
        modifier = modifier,
        onClick = onVideoCallClick
    ) {
        Icon(
            painter = painterResource(id = uiR.drawable.videocamera_record_svgrepo_com),
            contentDescription = "",
        )
    }
}


@Composable
fun DateSeparatorItemContainer(
    modifier: Modifier = Modifier,
    dateSeparator: DateSeparatorState
) {
    Box(
        modifier = modifier
            .alpha(0.5f)
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Timestamp(date = dateSeparator.date)
    }
}

@Composable
fun MessageItemContainer(
    modifier: Modifier = Modifier,
    messageItemState: ChatMessageItemState,
    currentUser: SocialChatUser?,
    isMessageInGroup: Boolean = false,
    onSwipeAction: (SocialChatMessage) -> Unit = {},
    onAttachmentClick: (SocialChatAttachment) -> Unit,
) {

    if (messageItemState.isMine) {
        MessageItemFromMe(
            modifier = modifier,
            messageItem = messageItemState,
            currentUser = currentUser,
            isMessageInGroup = isMessageInGroup,
            onSwipeAction = onSwipeAction,
            onAttachmentClick = onAttachmentClick,
        )
    } else {
        MessageItemFromOther(
            modifier = modifier,
            onSwipeAction = onSwipeAction,
            messageItem = messageItemState,
            onAttachmentClick = onAttachmentClick,
        )
    }

}

@Composable
fun DefaultChatScreenBackButton(onBackPressed: () -> Unit) {
    val layoutDirection = LocalLayoutDirection.current

    BackButton(
        modifier = Modifier.mirrorRtl(layoutDirection = layoutDirection),
        vector = Icons.AutoMirrored.Filled.ArrowBack,
        onBackPressed = onBackPressed,
    )
}

@Composable
fun DefaultChatHeaderMainContent(
    modifier: Modifier = Modifier,
    channel: SocialChatChannel,
    currentUser: SocialChatUser?,
    onHeaderActionClick: (SocialChatChannel) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onHeaderActionClick(channel)
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SocialChannelAvatar(
            modifier = Modifier
                .padding(8.dp)
                .size(56.dp),
            channel = channel,
            currentUser = currentUser
        )
        Column(
            modifier = Modifier
                .height(IntrinsicSize.Max),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                channel.getDisplayName(
                    LocalContext.current,
                    currentUser = currentUser,
                ),
                maxLines = 1,
                style = MaterialTheme.typography.labelLarge,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(3f)
            )
            Text(
                text = "Online",
                style = MaterialTheme.typography.labelMedium.copy(color = Color.LightGray),
            )

        }

    }
}

@Composable
fun DefaultMessageComposer(
    modifier: Modifier = Modifier,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    inputState: MessageInputState,
    onUtilityClick: (ComposerUtility) -> Unit,
    onRemoveQuotedMessage: () -> Unit,
    onRemoveAttachment: (SocialChatAttachment) -> Unit,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background
    ) {
        MessageComposer(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            onMessageChange = onMessageChange,
            onSendMessage = onSendMessage,
            inputState = inputState,
            onRemoveQuotedMessage = onRemoveQuotedMessage,
            onRemoveAttachment = onRemoveAttachment,
            onUtilityClick = onUtilityClick
        )
    }
}

@Composable
fun DefaultMessagesLoadingMoreIndicator(modifier: Modifier = Modifier) {
    LoadingIndicator(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShimmerChatHeader(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
) {
    Surface(
        modifier = modifier.shimmerEffect(), shadowElevation = 5.dp,
        color = MaterialTheme.colorScheme.background
    ) {
        TopAppBar(modifier = Modifier.padding(8.dp),
            title = { },
            navigationIcon = {
                DefaultChatScreenBackButton(onBackPressed = onBackPressed)
            }
        )
    }
}

@Composable
fun ShimmerMessageComposer(
    modifier: Modifier = Modifier
) {

    Surface(

        modifier = modifier
            .drawShadow(
                offsetY = (-5).dp, blurRadius = 25.dp, color = Color.LightGray
            )
            .shimmerEffect(), shadowElevation = 8.dp, color = MaterialTheme.colorScheme.background

    ) {
        BottomAppBar { }
    }
}


@Composable
private fun OnLastVisibleItemChanged(
    lazyListState: LazyListState,
    onChanged: (firstVisibleItemIndex: Int) -> Unit
) {
    onChanged(remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }.value)
}


@Preview(showBackground = true)
@Composable
fun ChatScreenPreviewLoading() {
    SocialChatAppTheme {
        ChatScreen(
            channel = SocialChatChannel(),
            currentUser = PreviewUserData.user1,
            onSendMessage = {},
            onMessageChange = {},
            onBackPressed = {},
            onSwipeAction = {},
            inputState = MessageInputState(),
            onRemoveQuotedMessage = {},
            messagesState = MessagesState(
                isLoading = true
            ),
            onMessagesTopReached = {},
            onUtilityClick = {},
            onRemoveAttachment = {},
            onLastVisibleMessageChanged = {},
            onAttachmentClick = {}
        )
    }
}

@Composable
fun ChatScreenPreviewWithMessages() {
    SocialChatAppTheme {
        ChatScreen(
            channel = PreviewChannelData.channelWithImage,
            currentUser = PreviewUserData.user1,
            onSendMessage = {},
            onMessageChange = {},
            onBackPressed = {},
            onSwipeAction = {},
            inputState = MessageInputState(
                inputValue = "Xin chao moi nguoi"
            ),
            onRemoveQuotedMessage = {},
            messagesState = MessagesState(
                messageItems = ChatViewModel.groupMessage(
                    messages = PreviewChannelData.messages,
                    currentUser = PreviewUserData.user1,
                    reads = emptyList()
                ),
            ),
            onMessagesTopReached = {},
            onUtilityClick = {},
            onRemoveAttachment = {},
            onLastVisibleMessageChanged = {},
            onAttachmentClick = {}
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ChatScreenPreviewWithMessagesDark() {
    ChatScreenPreviewWithMessages()
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun ChatScreenPreviewWithMessagesLight() {
    ChatScreenPreviewWithMessages()
}

