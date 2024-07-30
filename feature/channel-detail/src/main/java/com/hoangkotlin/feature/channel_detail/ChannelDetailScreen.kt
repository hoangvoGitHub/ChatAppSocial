package com.hoangkotlin.feature.channel_detail

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import com.hoangkotlin.chatappsocial.core.model.getDisplayName
import com.hoangkotlin.chatappsocial.core.ui.components.BackButton
import com.hoangkotlin.chatappsocial.core.ui.components.LoadingIndicator
import com.hoangkotlin.chatappsocial.core.ui.components.dialog.ClearConversationConfirmationDialog
import com.hoangkotlin.chatappsocial.core.ui.components.image.SocialChannelAvatar
import com.hoangkotlin.chatappsocial.core.ui.components.image.mirrorRtl
import com.hoangkotlin.chatappsocial.core.ui.previewdatas.PreviewChannelData
import com.hoangkotlin.chatappsocial.core.ui.previewdatas.PreviewUserData
import com.hoangkotlin.chatappsocial.core.ui.theme.SocialChatAppTheme
import com.hoangkotlin.feature.channel_detail.components.ChannelDetailActionsContainer
import com.hoangkotlin.feature.channel_detail.components.ChannelDetailMoreDropdownMenu
import com.hoangkotlin.feature.channel_detail.components.ChannelDetailOptions
import com.hoangkotlin.feature.channel_detail.model.ChannelDetailDropdown
import com.hoangkotlin.feature.channel_detail.model.ChannelDetailOptionEntry
import com.hoangkotlin.feature.channel_detail.model.ChannelDetailUiState
import com.hoangkotlin.feature.channel_detail.model.ChannelDetailViewModel
import kotlinx.coroutines.flow.collectLatest
import com.hoangkotlin.chatappsocial.core.common.R as commonR


@Composable
fun ChannelDetailRoute(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit = {},
    onShowBubbleClicked: (channelId: String) -> Unit,
    onNavigateToAttachments: (channelId: String) -> Unit,
    onHideBubbleClicked: () -> Unit,
) {
    val viewModel: ChannelDetailViewModel = hiltViewModel()

    val channelState by viewModel.currentChannel.collectAsStateWithLifecycle()
    val channelDetailState by viewModel.channelDetailState.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    var openAlertDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(viewModel.toastMessage) {
        viewModel.toastMessage.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    ChannelDetailScreen(
        modifier = modifier,
        onBackPressed = onBackPressed,
        channelDetailUiState = channelDetailState,
        currentUser = currentUser,
        onChannelDetailAction = {},
        onChannelDetailOption = { option ->
            when (option) {
                ChannelDetailOptionEntry.ClearConversation -> {
                    openAlertDialog = true
                }

                ChannelDetailOptionEntry.MediasAndFiles -> {
                    channelState?.let { onNavigateToAttachments(it.channelId) }
                }

                else -> {
                    viewModel.showToast("This feature is not implemented yet!")
                }
            }
        }
    ) { dropdownItem ->
        when (dropdownItem) {
            ChannelDetailDropdown.ShowBubble -> channelState?.channelId?.let { cid ->
                onShowBubbleClicked(
                    cid
                )
            }

            ChannelDetailDropdown.HideBubble -> onHideBubbleClicked()
        }
    }
    if (openAlertDialog &&
        channelDetailState is ChannelDetailUiState.Success
    ) {
        ClearConversationConfirmationDialog(
            channel = (channelDetailState as ChannelDetailUiState.Success).channel,
            currentUser = currentUser,
            onDismissRequest = { openAlertDialog = false },
            onConfirmation = viewModel::clearConversationHistory
        )
    }
}

@Composable
fun ChannelDetailScreen(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
    channelDetailUiState: ChannelDetailUiState,
    currentUser: SocialChatUser?,
    onChannelDetailAction: (ChannelDetailAction) -> Unit,
    onChannelDetailOption: (ChannelDetailOptionEntry) -> Unit,
    onDropdownItemClick: (ChannelDetailDropdown) -> Unit,
) {

    Scaffold(modifier = modifier, topBar = {
        ChannelDetailAppBar(
            onBackPressed = onBackPressed,
            onDropdownItemClick = onDropdownItemClick
        )
    }) { paddingValues ->
        AnimatedContent(
            targetState = channelDetailUiState,
            modifier = Modifier.padding(paddingValues), label = "ChannelDetailUiState"
        ) { state ->
            when (state) {
                ChannelDetailUiState.LoadFailed -> ChannelDetailLoadFailedContent()
                ChannelDetailUiState.Loading -> ChannelDetailLoadingContent()
                is ChannelDetailUiState.Success -> ChannelDetailSuccessContent(
                    channel = state.channel,
                    currentUser = currentUser,
                    onChannelDetailAction = onChannelDetailAction,
                    onChannelDetailOption = onChannelDetailOption
                )
            }

        }

    }
}

@Composable
fun ChannelDetailSuccessContent(
    modifier: Modifier = Modifier,
    channel: SocialChatChannel,
    currentUser: SocialChatUser?,
    onChannelDetailAction: (ChannelDetailAction) -> Unit,
    onChannelDetailOption: (ChannelDetailOptionEntry) -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ChannelDetailAvatar(
            modifier = Modifier.size(screenWidth / 3),
            channel = channel,
            currentUser = currentUser
        )
        ChannelDetailName(
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            channelName = channel.getDisplayName(
                LocalContext.current,
                currentUser = currentUser
            )
        )
        ChannelDetailActionsContainer(onAction = onChannelDetailAction)
        ChannelDetailOptions(
            onOptionClick = onChannelDetailOption
        )
    }
}

@Composable
fun ChannelDetailLoadingContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        DefaultChannelDetailLoadingMoreIndicator(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
fun ChannelDetailLoadFailedContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Text(text = stringResource(id = commonR.string.load_failed))
    }
}

@Composable
fun DefaultChannelDetailLoadingMoreIndicator(modifier: Modifier = Modifier) {
    LoadingIndicator(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
    )
}

@Composable
fun ChannelDetailName(
    modifier: Modifier = Modifier,
    channelName: String
) {
    Text(
        modifier = modifier,
        text = channelName,
        style = MaterialTheme.typography.labelLarge
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelDetailAppBar(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
    onDropdownItemClick: (ChannelDetailDropdown) -> Unit
) {
    TopAppBar(
        modifier = modifier,
        title = { },
        navigationIcon = {
            DefaultChannelDetailScreenBackButton(onBackPressed = onBackPressed)
        },
        actions = {
            ChannelDetailMoreDropdownMenu(onItemClick = onDropdownItemClick)
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}

@Composable
fun ChannelDetailAvatar(
    modifier: Modifier = Modifier,
    channel: SocialChatChannel,
    currentUser: SocialChatUser?,
) {
    SocialChannelAvatar(
        modifier = modifier,
        channel = channel,
        currentUser = currentUser,
        showOnlineIndicator = false
    )
}


@Composable
fun DefaultChannelDetailScreenBackButton(onBackPressed: () -> Unit) {
    val layoutDirection = LocalLayoutDirection.current

    BackButton(
        modifier = Modifier.mirrorRtl(layoutDirection = layoutDirection),
        vector = Icons.AutoMirrored.Filled.ArrowBack,
        onBackPressed = onBackPressed,
    )
}


@Preview
@Composable
fun ChannelDetailScreenPreview() {
    SocialChatAppTheme {
        ChannelDetailScreen(
            onBackPressed = {},
            channelDetailUiState = ChannelDetailUiState.Success(
                channel = PreviewChannelData.channelWithTwoUsers
            ),
            currentUser = PreviewUserData.me,
            onChannelDetailAction = {},
            onChannelDetailOption = {}
        ) {}
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ChannelDetailScreenPreviewDark() {
    ChannelDetailScreenPreview()
}

