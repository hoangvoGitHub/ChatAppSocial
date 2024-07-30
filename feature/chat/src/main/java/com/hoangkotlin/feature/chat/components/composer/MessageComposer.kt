package com.hoangkotlin.feature.chat.components.composer

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hoangkotlin.chatappsocial.core.model.SocialChatMessage
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import com.hoangkotlin.chatappsocial.core.model.attachment.SocialChatAttachment
import com.hoangkotlin.chatappsocial.core.ui.previewdatas.PreviewAttachmentData
import com.hoangkotlin.chatappsocial.core.ui.previewdatas.PreviewChannelData
import com.hoangkotlin.chatappsocial.core.ui.theme.SocialChatAppTheme
import com.hoangkotlin.feature.chat.MessageInputState
import com.hoangkotlin.feature.chat.Reply
import com.hoangkotlin.feature.chat.model.ComposerUtility
import com.hoangkotlin.chatappsocial.core.ui.R as uiR

private val DefaultVisibleUtilities = listOf(
    ComposerUtility.Gallery,
    ComposerUtility.Camera,
    ComposerUtility.Voice,
)

@Composable
fun MessageComposer(
    modifier: Modifier = Modifier,
    inputState: MessageInputState,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onRemoveQuotedMessage: () -> Unit,
    onUtilityClick: (ComposerUtility) -> Unit,
    onRemoveAttachment: (SocialChatAttachment) -> Unit = {},
) {
    val density = LocalDensity.current

    var isCollapsed by remember {
        mutableStateOf(false)
    }

    var isTyping by remember {
        mutableStateOf(false)
    }

    var isDropdownExpanded by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(isTyping, inputState.inputValue) {
        isCollapsed = isTyping && inputState.inputValue.isNotEmpty()
    }

    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier.fillMaxWidth(),
    ) {
        AnimatedVisibility(visible = (inputState.action is Reply), enter = slideInVertically {
            // Slide in from 40 dp from the top.
            with(density) { -40.dp.roundToPx() }
        } + expandVertically(
            // Expand from the top.
            expandFrom = Alignment.Top
        ) + fadeIn(
            // Fade in with the initial alpha of 0.3f.
            initialAlpha = 0.3f
        ), exit = slideOutVertically() + shrinkVertically() + fadeOut()) {
            ReplyMessageComposer(
                modifier = Modifier.padding(8.dp),
                quotedMessage = inputState.action?.message,
                currentUser = inputState.currentUser,
                onRemoveQuotedMessage = onRemoveQuotedMessage
            )
        }
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 0.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            DefaultUtilityPickerOptions(
                modifier = Modifier
                    .padding(
                        vertical = 6.dp,
                        horizontal = 4.dp
                    ),
                onUtilityClick = onUtilityClick,
                onMoreUtilityClick = {
                    if (isCollapsed) {
                        isCollapsed = false
                    } else {
                        isDropdownExpanded = !isDropdownExpanded
                    }
                },
                isCollapsed = isCollapsed,
                isDropdownExpanded = isDropdownExpanded,
                dropdown = {
                    MoreUtilitiesDropdown(
                        expanded = isDropdownExpanded,
                        onDismissRequest = {
                            isDropdownExpanded = false
                        },
                        onUtilityClick = onUtilityClick
                    )
                }
            )
            MessageComposerInput(
                modifier = Modifier.weight(1f),
                messageInputState = inputState,
                onValueChange = onMessageChange,
                onFocusChange = { isFocused ->
                    isTyping = isFocused
                },
                onRemoveAttachment = onRemoveAttachment
            )
            DefaultSendButton(
                onClick = onSendMessage,
                modifier = Modifier.padding(bottom = 6.dp, end = 4.dp),
            )
        }
    }

}


@Composable
fun DefaultUtilityPickerOptions(
    modifier: Modifier = Modifier,
    onUtilityClick: (ComposerUtility) -> Unit,
    onMoreUtilityClick: () -> Unit,
    isCollapsed: Boolean = true,
    isDropdownExpanded: Boolean,
    dropdown: @Composable () -> Unit = {}
) {
    Row(
        modifier = modifier.animateContentSize(
            animationSpec = tween(
                durationMillis = 200,
                easing = LinearEasing
            )
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DefaultExpandUtilityPickers(
            onClick = onMoreUtilityClick,
            dropdown = dropdown,
            isDropdownExpanded = isDropdownExpanded
        )
        if (!isCollapsed) {
            DefaultVisibleUtilities.forEach { utility ->
                DefaultVisibleUtilityOption(
                    composerUtility = utility, onClick = onUtilityClick
                )
            }
        }

    }
}

@Composable
fun DefaultExpandUtilityPickers(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isDropdownExpanded: Boolean,
    dropdown: @Composable () -> Unit = {}
) {

    val angle: Float by animateFloatAsState(
        targetValue = if (isDropdownExpanded) 45f else 0f,
        label = "",//rotation is retrieved as argument
        animationSpec = tween(
            durationMillis = 200, // rotation is retrieved with this frequency
            easing = FastOutSlowInEasing
        )
    )
    dropdown()
    DefaultComposerIconButton(
        modifier = modifier.rotate(angle),
        painter = painterResource(id = uiR.drawable.noun_plus),
        onClick = onClick
    )
}


@Composable
fun DefaultVisibleUtilityOption(
    composerUtility: ComposerUtility,
    modifier: Modifier = Modifier,
    onClick: (ComposerUtility) -> Unit
) {
    DefaultComposerIconButton(
        modifier = modifier,
        painter = painterResource(id = composerUtility.iconRes),
        contentDescription = stringResource(id = composerUtility.labelRes),
        onClick = {
            onClick(composerUtility)
        }
    )
}

@Composable
fun DefaultComposerIconButton(
    modifier: Modifier = Modifier,
    painter: Painter,
    contentDescription: String = "",
    onClick: () -> Unit
) {
    IconButton(
        modifier = modifier
            .size(32.dp),
        onClick = onClick
    ) {
        Icon(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            painter = painter,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
fun DefaultSendButton(
    modifier: Modifier = Modifier, onClick: () -> Unit
) {
    IconButton(
        modifier = modifier
            .size(32.dp),
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.background,
        )
    ) {
        Icon(
            modifier = Modifier
                .rotate(45f),
            painter = painterResource(id = uiR.drawable.plain_svgrepo_com),
            contentDescription = "Send Button",
            tint = MaterialTheme.colorScheme.primary,
        )
    }
}


@Composable
fun ReplyMessageComposer(
    modifier: Modifier = Modifier,
    currentUser: SocialChatUser? = null,
    quotedMessage: SocialChatMessage?,
    onRemoveQuotedMessage: () -> Unit,
) {
    val quotedMessageFrom: String = if (currentUser?.id == quotedMessage?.user?.id) {
        "yourself"
    } else {
        quotedMessage?.user?.name ?: ""
    }
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(14.dp),

                imageVector = Icons.AutoMirrored.Filled.Reply,
                contentDescription = Icons.AutoMirrored.Filled.Reply.name
            )
            Text(
                text = "You're replying $quotedMessageFrom",
                style = MaterialTheme.typography.labelMedium
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .alpha(0.5f),
                text = quotedMessage?.text ?: "",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 14.sp, platformStyle = PlatformTextStyle(
                        includeFontPadding = false
                    )
                ),

                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            IconButton(
                onClick = onRemoveQuotedMessage
            ) {
                Icon(imageVector = Icons.Default.Clear, contentDescription = null)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MessageInputPreviewWithAction() {

    val attachments = remember {
        mutableStateListOf<SocialChatAttachment>()
    }

    SocialChatAppTheme {
        Scaffold {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Button(onClick = {
                        attachments += PreviewAttachmentData.singleImage
                    }) {
                        Text(text = "Add image")
                    }

                    Button(onClick = {
                        if (attachments.isNotEmpty()) {
                            attachments.removeLast()
                        }
                    }) {
                        Text(text = "Remove image")
                    }
                }
                MessageComposer(
                    inputState = MessageInputState(
                        action = Reply(message = PreviewChannelData.messages.first()),
                        attachments = attachments,
                        inputValue = "longText"
                    ),
                    onSendMessage = {},
                    onMessageChange = {},
                    onRemoveQuotedMessage = {
                    },
                    onUtilityClick = {},
                    onRemoveAttachment = { attachment ->
                        attachments.remove(attachment)
                    })
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MessageInputPreviewWithActionDark() {
    MessageInputPreviewWithAction()
}

@Preview(showBackground = true)
@Composable
fun MessageInputPreviewWithImageAttachments() {
    SocialChatAppTheme {
        Scaffold {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                Column(modifier = Modifier.weight(1f)) {

                }
                MessageComposer(
                    inputState = MessageInputState(
                        action = Reply(message = PreviewChannelData.messages.first()),
                        attachments = PreviewAttachmentData.imageAttachments,
                        inputValue = "longText"
                    ),
                    onSendMessage = {},
                    onMessageChange = {},
                    onRemoveQuotedMessage = {
                    },
                    onUtilityClick = {},
                    onRemoveAttachment = {})
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MessageInputPreviewWithImageAttachmentsDark() {
    MessageInputPreviewWithImageAttachments()
}

@Preview(showBackground = true)
@Composable
fun MessageInputPreviewWithFilesAttachments() {
    SocialChatAppTheme {
        Scaffold {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                Column(modifier = Modifier.weight(1f)) {

                }
                MessageComposer(
                    inputState = MessageInputState(
                        action = Reply(message = PreviewChannelData.messages.first()),
                        attachments = PreviewAttachmentData.fileAttachments,
                        inputValue = "longText"
                    ),
                    onSendMessage = {},
                    onMessageChange = {},
                    onRemoveQuotedMessage = {
                    },
                    onUtilityClick = {},
                    onRemoveAttachment = {})
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MessageInputPreviewWithFilesAttachmentsDark() {
    MessageInputPreviewWithFilesAttachments()
}

private const val longText: String = """
    I am going to do it. I have made up my mind. 
     are the first few words of the new… the best … 
     the Longest Text In The Entire History Of The Known Universe! 
     This Has To Have Over 35,000 words the beat the current world 
     record set by that person who made that flaming chicken handbooky thingy. 
     I might just be saying random things the whole time I type in this so you might get confused a lot. 
     I just discovered something terrible. autocorrect is on!! no!!! this has to be crazy, 
     so I will have to break all the English language rules and the basic knowledge of the average human being. 
     I am not an average human being, however I am special. no no no, not THAT kind of special ;). 
     Why do people send that wink face! it always gives me nightmares! it can make a completely normal sentence creepy. 
     """

private const val TAG = "MessageComposer"



