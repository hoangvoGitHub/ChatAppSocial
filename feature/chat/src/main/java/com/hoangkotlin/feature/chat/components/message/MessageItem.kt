package com.hoangkotlin.feature.chat.components.message

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hoangkotlin.chatappsocial.core.model.SocialChannelRead
import com.hoangkotlin.chatappsocial.core.model.SocialChatMessage
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import com.hoangkotlin.chatappsocial.core.model.attachment.SocialChatAttachment
import com.hoangkotlin.chatappsocial.core.model.attachment.UploadState
import com.hoangkotlin.chatappsocial.core.offline.state.messages.ChatMessageItemState
import com.hoangkotlin.chatappsocial.core.offline.state.messages.MessageItemGroupPosition
import com.hoangkotlin.chatappsocial.core.ui.components.image.InitialsAvatar
import com.hoangkotlin.chatappsocial.core.ui.components.image.SocialUserAvatar
import com.hoangkotlin.chatappsocial.core.ui.extensions.SwipeDirection
import com.hoangkotlin.chatappsocial.core.ui.extensions.swipeForAction
import com.hoangkotlin.chatappsocial.core.ui.previewdatas.PreviewChannelData
import com.hoangkotlin.chatappsocial.core.ui.previewdatas.PreviewUserData
import com.hoangkotlin.chatappsocial.core.ui.theme.SocialChatAppTheme

@Composable
fun MessageItemFromMe(
    modifier: Modifier = Modifier,
    messageItem: ChatMessageItemState,
    currentUser: SocialChatUser? = null,
    isMessageInGroup: Boolean = false,
    onSwipeAction: (SocialChatMessage) -> Unit = {},
    onAttachmentClick: (SocialChatAttachment) -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val shape = messageItem.groupPosition.messageItemShape(fromMe = true)
    val padding = messageItem.groupPosition.messageItemPadding()

    val shouldShowSingleRead = !isMessageInGroup &&
            messageItem.lastReadBy.isNotEmpty()

    val shouldShowSyncStatus = !shouldShowSingleRead && messageItem.shouldShowSyncStatus &&
            messageItem.lastReadBy.isEmpty()

    val shouldShowReads = !shouldShowSingleRead && messageItem.lastReadBy.isNotEmpty()

    val shouldShowUploads = remember(messageItem.message.attachments) {
        messageItem.message.attachments.isNotEmpty() &&
                messageItem.message.attachments.any {
                    it.uploadState is UploadState.InProgress ||
                            it.uploadState is UploadState.Idle
                }
    }

    val shouldShowAttachments = remember(messageItem.message.attachments) {
        !shouldShowUploads && messageItem.message.attachments.isNotEmpty() &&
                messageItem.message.attachments.all {
                    it.uploadState is UploadState.Success
                }
    }

    val quotedMessage = messageItem.message.replyTo
    Box(
        modifier = modifier
            .padding(padding)
            .swipeForAction(
                allowedDirection = SwipeDirection.EndToStart,
                onAction = {
                    onSwipeAction(messageItem.message)
                }
            )
            .padding(end = 4.dp)
    ) {


        Box {
            ReplyIconSwipe(
                screenWidth = screenWidth.value,
                isFromMe = true
            )
            Row(
                modifier = Modifier
                    .padding(end = 4.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.End
                ) {
                    if (quotedMessage != null) {
                        val quotedMessageFrom: String =
                            if (currentUser?.id == quotedMessage.user.id) {
                                "yourself"
                            } else {
                                quotedMessage.user.name
                            }
                        Row(
                            modifier = Modifier.offset(y = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(14.dp),

                                imageVector = Icons
                                    .AutoMirrored.Filled.Reply,
                                contentDescription = null
                            )
                            Text(
                                text = "You're replying $quotedMessageFrom",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                        MessageContainer(
                            modifier = Modifier
                                .offset(y = 20.dp)
                                .alpha(0.5f),
                            paddingValues = PaddingValues(
                                top = 8.dp,
                                bottom = 24.dp,
                                start = 8.dp,
                                end = 8.dp
                            ),
                            backgroundColor = Color.LightGray,
                            minWidth = screenWidth.times(0.1f),
                            maxWidth = screenWidth.times(0.6f),
                            message = quotedMessage,
                            shape = shape,
                            textColor = Color.Black,
                            isForQuotedMessage = true
                        )
                    }

                    AnimatedVisibility(visible = shouldShowUploads) {
                        AttachmentUploads(
                            modifier = Modifier
                                .width(screenWidth.times(0.7f))
                                .padding(bottom = 4.dp),
                            messageItem = messageItem
                        )
                    }

                    AnimatedVisibility(visible = shouldShowAttachments) {
                        Attachments(
                            attachments = messageItem.message.attachments,
                            onAttachmentClick = onAttachmentClick,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    MessageContainer(
                        paddingValues = PaddingValues(8.dp),
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        minWidth = screenWidth.times(0.1f),
                        maxWidth = screenWidth.times(0.7f),
                        message = messageItem.message,
                        shape = shape,
                        textColor = MaterialTheme.colorScheme.onPrimary
                    )
                    if (shouldShowReads) {
                        MessageReadsContainer(
                            modifier = Modifier.padding(top = 4.dp),
                            reads = messageItem.lastReadBy
                        )
                    }
                    if (shouldShowSingleRead) {
                        SocialUserAvatar(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .size(16.dp),
                            user = messageItem.lastReadBy.first().user,
                            showOnlineIndicator = false
                        )
                    }
                    if (shouldShowSyncStatus) {
                        SyncStatusIndicator(
                            modifier = Modifier.padding(top = 4.dp),
                            syncStatus = messageItem.message.syncStatus,
                        )
                    }
                }


            }

        }
    }
}

@Composable
fun MessageItemFromOther(
    modifier: Modifier = Modifier,
    messageItem: ChatMessageItemState,
    needToShowReads: Boolean = false,
    onSwipeAction: (SocialChatMessage) -> Unit = {},
    onAttachmentClick: (SocialChatAttachment) -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val shape = messageItem.groupPosition.messageItemShape(fromMe = false)
    val padding = messageItem.groupPosition.messageItemPadding()

    val shouldShowAvatar = messageItem.groupPosition == MessageItemGroupPosition.Bottom ||
            messageItem.groupPosition == MessageItemGroupPosition.None

    val shouldShowSenderName = messageItem.groupPosition == MessageItemGroupPosition.Top ||
            messageItem.groupPosition == MessageItemGroupPosition.None

    val quotedMessage = messageItem.message.replyTo

    val shouldShowReads = needToShowReads && messageItem.lastReadBy.isNotEmpty()

    Box(
        modifier = modifier
            .padding(padding)
            .swipeForAction(
                allowedDirection = SwipeDirection.StartToEnd,
                onAction = {
                    onSwipeAction(messageItem.message)
                }
            )
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            Box {
                ReplyIconSwipe(screenWidth = screenWidth.value, isFromMe = false)
                Column(modifier = Modifier) {
                    Row(
                        modifier = Modifier.height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(start = 4.dp, end = 8.dp)
                        ) {
                            if (shouldShowAvatar) {
                                SocialUserAvatar(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .align(Alignment.BottomCenter),
                                    user = messageItem.message.user,
                                    showOnlineIndicator = false
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .align(Alignment.BottomCenter)
                                )
                            }
                        }
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (shouldShowSenderName) {
                                Text(
                                    modifier = Modifier.offset(y = if (quotedMessage != null) 16.dp else 0.dp),
                                    text = messageItem.message.user.name,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                            if (quotedMessage != null) {
                                MessageContainer(
                                    modifier = Modifier
                                        .offset(y = 20.dp)
                                        .alpha(0.5f),
                                    paddingValues = PaddingValues(
                                        top = 8.dp,
                                        bottom = 24.dp,
                                        start = 8.dp,
                                        end = 8.dp
                                    ),
                                    backgroundColor = Color.LightGray,
                                    minWidth = screenWidth.times(0.1f),
                                    maxWidth = screenWidth.times(0.6f),
                                    message = quotedMessage,
                                    shape = shape,
                                    textColor = Color.Black,
                                    isForQuotedMessage = true
                                )
                            }
                            if (messageItem.message.attachments.isNotEmpty()) {
                                Attachments(
                                    attachments = messageItem.message.attachments,
                                    onAttachmentClick = onAttachmentClick,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                            MessageContainer(
                                modifier = Modifier,
                                paddingValues = PaddingValues(8.dp),
                                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                                minWidth = screenWidth.times(0.1f),
                                maxWidth = screenWidth.times(0.7f),
                                message = messageItem.message,
                                shape = shape,
                                textColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                    }
                    if (shouldShowReads) {
                        MessageReadsContainer(
                            modifier = Modifier.padding(start = 44.dp, top = 4.dp),
                            reads = messageItem.lastReadBy
                        )

                    }
                }
            }
        }
    }
}

@Composable
fun MessageReadsContainer(
    modifier: Modifier = Modifier,
    reads: List<SocialChannelRead> = emptyList()
) {
    Row(modifier = modifier) {
        reads.take(READS_ICONS_LIMIT).forEach {
            SocialUserAvatar(
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(16.dp), user = it.user
            )
        }
        if (reads.size > READS_ICONS_LIMIT) {
            InitialsAvatar(
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(16.dp),
                initials = "+${reads.size - READS_ICONS_LIMIT}"
            )
        }
    }
}


@Composable
fun MessageContainer(
    modifier: Modifier = Modifier,
    message: SocialChatMessage,
    paddingValues: PaddingValues,
    maxWidth: Dp, minWidth: Dp,
    backgroundColor: Color,
    textColor: Color,
    isForQuotedMessage: Boolean = false,
    shape: Shape = RoundedCornerShape(16.dp),
) {
    Surface(
        modifier = modifier,
        shadowElevation = 2.dp, shape = shape
    ) {
        Box(
            modifier = Modifier
                .clip(shape)
                .background(backgroundColor, shape)
                .padding(paddingValues)
                .widthIn(minWidth, maxWidth)
                .padding(horizontal = 4.dp)


        ) {
            Text(
                modifier = Modifier
                    .wrapContentHeight()
                    .align(Alignment.Center),
                text = message.text,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium.copy(
                    platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),
                maxLines = if (isForQuotedMessage) MaxLinesForQuotedMessage else Int.MAX_VALUE,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun BoxScope.ReplyIconSwipe(
    modifier: Modifier = Modifier,
    screenWidth: Float,
    isFromMe: Boolean = true
) {
    val (offset, alignment) = if (isFromMe) {
        Pair((screenWidth / 5).dp, Alignment.CenterEnd)
    } else {
        Pair((-screenWidth / 5).dp, Alignment.CenterStart)
    }
    Box(
        modifier = modifier
            .size(32.dp)
            .align(alignment)
            .offset(x = offset)
            .background(
                color = MaterialTheme.colorScheme.secondary,
                shape = CircleShape
            )
    ) {
        Icon(
            modifier = Modifier.align(Alignment.Center),
            imageVector = Icons.AutoMirrored.Filled.Reply, contentDescription = "",
            tint = MaterialTheme.colorScheme.onSecondary
        )
    }
}

@Preview
@Composable
fun MessageItemWithAttachmentsPreview() {

}


@Preview(showBackground = true)
@Composable
fun MessageItemFromMePreview() {
    SocialChatAppTheme {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                MessageItemFromMe(
                    messageItem = ChatMessageItemState(
                        message = PreviewChannelData.messages.first()
                    ),
                    onAttachmentClick = {},
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun MessageItemFromOtherPreview() {
    SocialChatAppTheme {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                MessageItemFromOther(
                    messageItem = ChatMessageItemState(
                        message = PreviewChannelData.messages.first()
                    ),
                    onAttachmentClick = {},
                )
            }
        }

    }
}

@Composable
fun MessageItemsPreview() {

    SocialChatAppTheme {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                item {
                    MessageItemFromMe(
                        messageItem = ChatMessageItemState(
                            groupPosition = MessageItemGroupPosition.None,
                            message = PreviewChannelData.otherReplyOther,
                            isMine = true,
                            shouldShowSyncStatus = false
                        ),
                        isMessageInGroup = false,
                        onAttachmentClick = {}
                    )
                    MessageItemFromOther(
                        messageItem = ChatMessageItemState(
                            message = PreviewChannelData.messageWithAttachmentsInProgress
                                .copy(replyTo = PreviewChannelData.longMessage),
                            isMine = false
                        ),
                        onAttachmentClick = {}
                    )
                    MessageItemFromMe(
                        messageItem = ChatMessageItemState(
                            groupPosition = MessageItemGroupPosition.None,
                            message = PreviewChannelData.messageWithAttachmentsInProgress
                                .copy(replyTo = PreviewChannelData.longMessage),
                            isMine = true,
                            lastReadBy = PreviewUserData.users.map { user ->
                                SocialChannelRead(
                                    user = user
                                )
                            }
                        ),
                        onAttachmentClick = { }
                    )
                    MessageItemFromOther(
                        messageItem = ChatMessageItemState(
                            message = PreviewChannelData.longMessage,
                            isMine = false,
                            lastReadBy = PreviewUserData.users.map { user ->
                                SocialChannelRead(
                                    user = user
                                )
                            }
                        ),
                        needToShowReads = true,
                        onAttachmentClick = { }
                    )


                    MessageItemFromMe(
                        messageItem = ChatMessageItemState(
                            groupPosition = MessageItemGroupPosition.Bottom,
                            message = PreviewChannelData.shortMessage,
                            isMine = true,
                            lastReadBy = PreviewUserData.users.map { user ->
                                SocialChannelRead(
                                    user = user
                                )
                            }
                        ),
                        onAttachmentClick = {}
                    )
                }

            }
        }


    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MessageItemsPreviewDark() {
    MessageItemsPreview()
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun MessageItemsPreviewLight() {
    MessageItemsPreview()
}


private const val READS_ICONS_LIMIT = 5

private fun MessageItemGroupPosition.messageItemShape(fromMe: Boolean): Shape {
    return if (fromMe) {
        when (this) {
            MessageItemGroupPosition.Bottom ->
                RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 4.dp,
                    bottomStart = 20.dp,
                    bottomEnd = 20.dp
                )


            MessageItemGroupPosition.Middle ->
                RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 4.dp,
                    bottomStart = 20.dp,
                    bottomEnd = 4.dp
                )


            MessageItemGroupPosition.None ->
                RoundedCornerShape(20.dp)


            MessageItemGroupPosition.Top ->
                RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                    bottomEnd = 4.dp,
                    bottomStart = 20.dp
                )

        }
    } else {
        when (this) {
            MessageItemGroupPosition.Bottom -> RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 20.dp,
                bottomStart = 20.dp,
                bottomEnd = 20.dp
            )

            MessageItemGroupPosition.Middle -> RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 20.dp,
                bottomStart = 4.dp,
                bottomEnd = 20.dp
            )

            MessageItemGroupPosition.None -> RoundedCornerShape(20.dp)

            MessageItemGroupPosition.Top -> RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = 4.dp,
                bottomEnd = 20.dp
            )
        }
    }
}

private fun MessageItemGroupPosition.messageItemPadding(): PaddingValues {
    return when (this) {
        MessageItemGroupPosition.Bottom -> PaddingValues(bottom = 8.dp)
        MessageItemGroupPosition.Middle -> PaddingValues(bottom = 2.dp)
        MessageItemGroupPosition.None -> PaddingValues(bottom = 8.dp)
        MessageItemGroupPosition.Top -> PaddingValues(top = 8.dp, bottom = 2.dp)
    }
}

private const val MaxLinesForQuotedMessage = 3