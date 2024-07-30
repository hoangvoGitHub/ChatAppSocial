package com.hoangkotlin.chatappsocial.feature.friend

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoangkotlin.chatappsocial.core.ui.FriendPossibleAction
import com.hoangkotlin.chatappsocial.core.ui.components.DefaultSearchBar
import com.hoangkotlin.chatappsocial.core.ui.components.PullToRefreshLayout
import com.hoangkotlin.chatappsocial.core.ui.theme.SocialChatAppTheme
import com.hoangkotlin.chatappsocial.feature.friend.components.FriendItemContainer
import com.hoangkotlin.chatappsocial.feature.friend.model.ChatFriendItemState
import com.hoangkotlin.chatappsocial.feature.friend.model.FriendActionState
import com.hoangkotlin.chatappsocial.feature.friend.model.SocialChatFriendTab

private const val TAG = "FriendScreen"

@Composable
fun FriendRoute(
    onSearchBarClick: () -> Unit,
    onFriendWithChannelClick: (String) -> Unit,
) {
    FriendScreen(
        onSearchBarClick = onSearchBarClick,
        onFriendClick = { item ->
            item.chatFriend.cid?.let { channelId ->
                onFriendWithChannelClick(channelId)
            }

        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FriendScreen(
    modifier: Modifier = Modifier,
    onSearchBarClick: () -> Unit,
    onFriendClick: (ChatFriendItemState) -> Unit,
) {

    val viewModel: FriendsViewModel = hiltViewModel()

    val friendsUiState by viewModel.friendListState.collectAsStateWithLifecycle()
    val friendRequestsUiState by viewModel.friendRequestState.collectAsStateWithLifecycle()

    val pagerState = rememberPagerState {
        SocialChatFriendTab.entries.size
    }

    var selectedTabIndex by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }
    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndex = pagerState.currentPage
    }

    PullToRefreshLayout(
        modifier = modifier,
        isRefreshing = if (selectedTabIndex == 0) friendsUiState.isLoading else friendRequestsUiState.isLoading,
        onRefresh = {
            viewModel.refresh(selectedTabIndex)
        }
    ) {
        DefaultSearchBar(
            onSearchBarClick = onSearchBarClick,
            placeHolderText = "Search for friends \uD83E\uDEC2"
        )
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = MaterialTheme.colorScheme.background
        ) {
            SocialChatFriendTab.entries.forEachIndexed { index, tab ->
                SocialFriendTabItem(
                    tab = tab, selected = index == selectedTabIndex,
                    onTabClicked = {
                        selectedTabIndex = index
                    }
                )
            }

        }
        HorizontalPager(state = pagerState) {
            val currentTab =
                SocialChatFriendTab.entries.getOrElse(
                    it,
                    defaultValue = { SocialChatFriendTab.FriendList })
            when (currentTab) {
                SocialChatFriendTab.FriendList -> FriendListTabContent(
                    friendStateItems = friendsUiState.channelItems.also { viewModel.initOnceObservationForFriends() },
                    onAction = viewModel::performActionForFriend,
                    onFriendClick = onFriendClick
                )

                SocialChatFriendTab.FriendRequest -> FriendListTabContent(
                    friendStateItems = friendRequestsUiState.channelItems.also { viewModel.initOnceObservationForFriendRequests() },
                    onAction = viewModel::performActionForFriendRequests,
                    onFriendClick = onFriendClick
                )
            }
        }
    }

}

@Composable
fun FriendListTabContent(
    modifier: Modifier = Modifier,
    friendStateItems: List<ChatFriendItemState>,
    onAction: (friendId: String, action: FriendPossibleAction) -> Unit,
    onFriendClick: (ChatFriendItemState) -> Unit,
) {

    LazyColumn(modifier = modifier.fillMaxHeight()) {
        if (friendStateItems.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxSize())
            }
        }
        items(friendStateItems, { item -> item.chatFriend.id }) { item ->
            AnimatedVisibility(
                visible = !item.actionType.containsValue(FriendActionState.Success),
                exit = fadeOut(animationSpec = tween(durationMillis = 1000))
            ) {
                FriendItemContainer(
                    friendItemState = item,
                    onAction = onAction,
                    onFriendClick = onFriendClick
                )

            }
        }
    }
}


@Composable
private fun calculateColorForAction(
    action: FriendPossibleAction,
    state: FriendActionState
): Pair<Color, Color> {
    val color: Pair<Color, Color> = when (state) {
        FriendActionState.Loading -> if (action is FriendPossibleAction.RejectFriend) {
            Pair(
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
            )
        } else {
            Pair(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
            )
        }

        is FriendActionState.Failed -> {
            Pair(
                MaterialTheme.colorScheme.errorContainer,
                MaterialTheme.colorScheme.onErrorContainer
            )
        }

        else -> {
            if (action is FriendPossibleAction.RejectFriend) {
                Pair(
                    MaterialTheme.colorScheme.primaryContainer,
                    MaterialTheme.colorScheme.onPrimaryContainer
                )
            } else {
                Pair(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
    return color
}

@Composable
fun FriendActionButton(
    modifier: Modifier = Modifier,
    action: FriendPossibleAction,
    state: FriendActionState,
    onClick: (FriendPossibleAction) -> Unit,
) {
    val (backgroundColor, textColor) = calculateColorForAction(action = action, state = state)

    if (state is FriendActionState.Failed) state.message.take(15) else stringResource(id = action.label)

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(30))
                .clickable { onClick(action) }
                .background(color = backgroundColor)
                .padding(8.dp)
                .animateContentSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = action.label),
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(modifier = Modifier.width(4.dp))
            ActionStateIcon(state = state, iconTint = textColor)
        }
    }
}

@Composable
fun ActionStateIcon(
    modifier: Modifier = Modifier,
    state: FriendActionState,
    iconTint: Color,
) {

    AnimatedContent(
        modifier = modifier,
        targetState = state, label = ""
    ) { currentState ->
        when (currentState) {
            is FriendActionState.Failed -> Icon(
                modifier = Modifier.size(20.dp),
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = iconTint
            )


            FriendActionState.Loading -> CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = iconTint
            )

            FriendActionState.Success -> Icon(
                modifier = Modifier.size(20.dp),
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = iconTint
            )


            else -> {}
        }

    }
}


@Composable
fun SocialFriendTabItem(
    modifier: Modifier = Modifier,
    selected: Boolean = true,
    tab: SocialChatFriendTab,
    onTabClicked: (SocialChatFriendTab) -> Unit,
) {
    Tab(modifier = modifier,
        selected = selected, onClick = { onTabClicked(tab) },
        text = {
            Text(text = stringResource(id = tab.displayNameRes))
        },
        icon = {
            Icon(
                painter = painterResource(id = tab.iconRes),
                contentDescription = stringResource(id = tab.displayNameRes)
            )
        })
}

@Preview(showBackground = true)
@Composable
fun SocialFriendTabItemPreview() {
    SocialChatAppTheme {
        SocialFriendTabItem(
            tab = SocialChatFriendTab.FriendList,
            onTabClicked = {}
        )
    }
}

