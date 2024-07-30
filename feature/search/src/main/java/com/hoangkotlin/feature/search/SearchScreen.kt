package com.hoangkotlin.feature.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoangkotlin.chatappsocial.core.data.model.DataResult
import com.hoangkotlin.chatappsocial.core.model.FriendStatus
import com.hoangkotlin.chatappsocial.core.model.SearchResult
import com.hoangkotlin.chatappsocial.core.model.search.SearchUser
import com.hoangkotlin.chatappsocial.core.ui.FriendPossibleAction
import com.hoangkotlin.chatappsocial.core.ui.components.BackButton
import com.hoangkotlin.chatappsocial.core.ui.components.SessionExpiredAlertDialog
import com.hoangkotlin.chatappsocial.core.ui.components.image.SocialAvatar
import com.hoangkotlin.chatappsocial.core.ui.components.image.mirrorRtl
import com.hoangkotlin.chatappsocial.core.ui.theme.SocialChatAppTheme
import com.hoangkotlin.chatappsocial.core.common.R as commonR
import com.hoangkotlin.chatappsocial.core.ui.R as uiR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchRoute(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onNavigateToChannel: (channelId: String) -> Unit,
    onAuthError: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val queryState by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResultUiState by viewModel.searchResultUiState.collectAsStateWithLifecycle()

    var selectedSearchUser by remember {
        mutableStateOf<SearchUserItem?>(null)
    }
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    SearchScreen(
        modifier = modifier,
        onBackClick = onBackClick,
        onUserClick = { searchUser ->
            selectedSearchUser = searchUser
            showBottomSheet = true
        },
        onSearchTriggered = {},
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        searchResultUiState = searchResultUiState,
        onAuthError = onAuthError,
        searchQuery = queryState.query
    )

    if (showBottomSheet && selectedSearchUser != null) {
        SearchUserInfoBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState,
            searchUserItem = selectedSearchUser!!,
            onFriendActionClick = viewModel::performAction,
            onGoToChannelClick = { searchItem ->
                if (searchItem.searchUser.channelId != null) {
                    onNavigateToChannel(searchItem.searchUser.channelId!!)
                }
            }
        )
    }
}

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    searchQuery: String,
    onBackClick: () -> Unit,
    onUserClick: (SearchUserItem) -> Unit,
    onSearchTriggered: (String) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onAuthError: () -> Unit = {},
    searchResultUiState: SearchResultUiState = SearchResultUiState.Loading,
) {
    Scaffold(
        topBar = {
            SearchToolbar(
                onBackPressed = onBackClick,
                onSearchQueryChanged = onSearchQueryChanged,
                onSearchTriggered = onSearchTriggered,
                searchQuery = searchQuery
            )
        }
    ) { paddingValues ->
        Column(modifier = modifier.padding(paddingValues)) {
            Spacer(Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))

            when (searchResultUiState) {
                SearchResultUiState.EmptyQuery,
                SearchResultUiState.Loading -> Unit

                is SearchResultUiState.LoadFailed -> {
                    when (searchResultUiState.errorCode) {
                        DataResult.Error.Code.NotAuthorized -> SessionExpiredAlertDialog {
                            onAuthError()
                        }

                        else -> UnknownErrorResult()
                    }
                }

                is SearchResultUiState.Success -> {
                    if (searchResultUiState.isEmpty()) {
                        EmptySearchResultBody()
                    } else {
                        SuccessBody(
                            searchResult = searchResultUiState.searchResult,
                            onUserClick = onUserClick
                        )
                    }
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun SearchUserInfoBottomSheetPreview() {
    var selectedSearchUser by remember {
        mutableStateOf<SearchUserItem?>(
            SearchUser(
                name = "Vo Tran Hoang",
                friendStatus = FriendStatus.REQUEST_FROM_OTHER,
                channelId = "abcdef",
            ).asSearchUserItem()
        )
    }
    var showBottomSheet by remember { mutableStateOf(true) }
    val sheetState = rememberModalBottomSheetState()
    SocialChatAppTheme {
        Scaffold {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                Button(onClick = {
                    showBottomSheet = true
                }) {
                    Text(text = "Shuffle")
                }
            }
            if (showBottomSheet && selectedSearchUser != null) {
                SearchUserInfoBottomSheet(
                    onDismissRequest = {
                        showBottomSheet = false
                    },
                    sheetState = sheetState,
                    searchUserItem = selectedSearchUser!!,
                    onFriendActionClick = { _, _ -> },
                    onGoToChannelClick = {}
                )
            }
        }
    }


}

@Composable
fun UnknownErrorResult(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(horizontal = 48.dp),
    ) {
        Text(
            text = "Unknown Error",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
fun EmptySearchResultBody(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(horizontal = 48.dp),
    ) {
        Text(
            text = "Empty query",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
fun SuccessBody(
    modifier: Modifier = Modifier, searchResult: SearchResultState,
    onUserClick: (SearchUserItem) -> Unit,
) {
    LazyColumn(modifier = modifier) {
        item {
            SearchResultGroup(group = SearchResultGroup.Users,
                onViewAllClick = {})
        }
        items(searchResult.userItems, key = { item -> item.searchUser.id }) { item ->
            SearchUserResultItem(
                searchUserItem = item,
                onUserClick = onUserClick
            )
        }

        item {
            SearchResultGroup(group = SearchResultGroup.Channels,
                onViewAllClick = {})
        }
        items(searchResult.groupItems, key = { item -> item.searchGroup.id }) { item ->
            SearchChannelResultItem(
                searchGroupItem = item, modifier = Modifier.padding(vertical = 4.dp),
            )
        }
    }
}

@Composable
fun SearchResultGroup(
    modifier: Modifier = Modifier,
    group: SearchResultGroup,
    onViewAllClick: (SearchResultGroup) -> Unit
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = group.titleResId),
            style = MaterialTheme.typography.headlineSmall
        )
        TextButton(onClick = { onViewAllClick(group) }) {
            Text(text = stringResource(id = commonR.string.view_all))
        }
    }

}

@Composable
fun SearchChannelResultItem(modifier: Modifier, searchGroupItem: SearchGroupItem) {

}

@Composable
fun SearchUserResultItem(
    modifier: Modifier = Modifier,
    searchUserItem: SearchUserItem,
    onUserClick: (SearchUserItem) -> Unit,
) {
    Row(
        modifier = modifier
            .clickable {
                onUserClick(searchUserItem)
            }
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SocialAvatar(
            imageUrl = searchUserItem.searchUser.image,
            modifier = Modifier
                .padding(8.dp)
                .size(32.dp),
            initials = searchUserItem.searchUser.name.trim()
                .split("\\s+".toRegex())
                .take(2)
                .joinToString(separator = "") { it.take(1).uppercase() }
        )
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(
                text = searchUserItem.searchUser.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelLarge,
            )
            if (searchUserItem.searchUser.channelId != null) {
                Text(
                    text = stringResource(id = commonR.string.connected),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }


    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchToolbar(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onSearchTriggered: (String) -> Unit,
    searchQuery: String,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            SearchTextField(
                onSearchQueryChanged = onSearchQueryChanged,
                onSearchTriggered = onSearchTriggered,
                searchQuery = searchQuery
            )
        },
        navigationIcon = {
            DefaultSearchScreenBackButton(onBackPressed = onBackPressed)
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )

}

@Composable
private fun SearchTextField(
    onSearchQueryChanged: (String) -> Unit,
    onSearchTriggered: (String) -> Unit,
    searchQuery: String = "",
) {

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val onSearchExplicitlyTriggered = {
        keyboardController?.hide()
        onSearchTriggered(searchQuery)
    }

    TextField(
        textStyle = MaterialTheme.typography.labelMedium,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurface,
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(
                    onClick = {
                        onSearchQueryChanged("")
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear text",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        },
        onValueChange = {
            if (!it.contains("\n")) {
                onSearchQueryChanged(it)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .focusRequester(focusRequester)
            .onKeyEvent {
                if (it.key == Key.Enter) {
                    onSearchExplicitlyTriggered()
                    true
                } else {
                    false
                }
            }
            .testTag("searchTextField"),
        shape = RoundedCornerShape(32.dp),
        value = searchQuery,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search,
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearchExplicitlyTriggered()
            },
        ),

        maxLines = 1,
        singleLine = true,
    )
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
fun DefaultSearchScreenBackButton(onBackPressed: () -> Unit) {
    val layoutDirection = LocalLayoutDirection.current

    BackButton(
        modifier = Modifier.mirrorRtl(layoutDirection = layoutDirection),
        vector = Icons.AutoMirrored.Filled.ArrowBack,
        onBackPressed = onBackPressed,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchUserInfoBottomSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {},
    onFriendActionClick: (userId: String, FriendPossibleAction) -> Unit,
    onGoToChannelClick: (SearchUserItem) -> Unit,
    sheetState: SheetState,
    searchUserItem: SearchUserItem,
) {
    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SocialAvatar(
                    imageUrl = searchUserItem.searchUser.image,
                    initials = searchUserItem.searchUser.name.trim()
                        .split("\\s+".toRegex())
                        .take(2)
                        .joinToString(separator = "") { it.take(1).uppercase() },
                    modifier = Modifier.size(64.dp)
                )
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = searchUserItem.searchUser.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Text(
                        text = "Joined since 2024",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = stringResource(id = commonR.string.connected),
                        style = MaterialTheme.typography.labelMedium
                    )

                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    searchUserItem.possibleActions.forEach { action ->
                        FriendActionButton(
                            action = action,
                            onFriendActionClick = { possibleAction ->
                                onFriendActionClick(
                                    searchUserItem.searchUser.id,
                                    possibleAction
                                )
                            }
                        )
                    }
                }
            }

            if (!searchUserItem.searchUser.channelId.isNullOrEmpty()) {
                DefaultNavigateToChannelButton(
                    modifier = Modifier.padding(top = 16.dp),
                    searchUserItem = searchUserItem,
                    onGoToChannelClick = onGoToChannelClick
                )
            }
        }
    }
}

@Composable
fun DefaultNavigateToChannelButton(
    modifier: Modifier = Modifier,
    searchUserItem: SearchUserItem,
    onGoToChannelClick: (SearchUserItem) -> Unit
) {
    Button(
        modifier = modifier.fillMaxWidth(),
        onClick = {
            onGoToChannelClick(searchUserItem)
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        )
    ) {
        Icon(
            modifier = Modifier.padding(horizontal = 8.dp),
            painter = painterResource(id = uiR.drawable.chat_round_line_svgrepo_com),
            contentDescription = null
        )
        Text(text = "Send a message")
    }
}

@Composable
fun FriendActionButton(
    modifier: Modifier = Modifier,
    onFriendActionClick: (FriendPossibleAction) -> Unit,
    action: FriendPossibleAction,
) {

    var enabled by remember {
        mutableStateOf(true)
    }
    Button(
        modifier = modifier,
        onClick = {
            onFriendActionClick(action)
            enabled = false
        },
        enabled = enabled
    ) {
        Text(
            text = stringResource(id = action.label),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}


@Preview
@Composable
fun EmptySearchResultBodyPreview() {
    SocialChatAppTheme {
        EmptySearchResultBody()
    }
}

@Preview(showBackground = true)
@Composable
fun SearchUserResultItemPreview() {
    val mockSearchUser: SearchUser = SearchUser(name = "Vo Tran Hoang")
    SocialChatAppTheme {
        SearchUserResultItem(
            searchUserItem = mockSearchUser.asSearchUserItem()
        ) {}
    }
}

@Preview
@Composable
fun SearchToolbarPreview() {
    SocialChatAppTheme {
        SearchToolbar(
            onBackPressed = { },
            onSearchQueryChanged = {},
            onSearchTriggered = {},
            searchQuery = ""
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    val searchUsers = List(10) {
        SearchUser(id = "$it", name = "Vo Tran Hoang $it")
    }
    SocialChatAppTheme {
        SearchScreen(
            onSearchQueryChanged = {},
            onSearchTriggered = {},
            onBackClick = {},
            onUserClick = {},
            searchResultUiState = SearchResultUiState.Success(
                searchResult = SearchResult(
                    searchUsers = searchUsers
                ).asSearchResultState()
            ),
            searchQuery = ""
        )
    }
}