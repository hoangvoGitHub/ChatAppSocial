package com.hoangkotlin.chatappsocial.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import com.hoangkotlin.chatappsocial.core.ui.components.image.SocialUserAvatar
import com.hoangkotlin.chatappsocial.core.ui.previewdatas.PreviewUserData
import com.hoangkotlin.chatappsocial.core.ui.theme.SocialChatAppTheme
import com.hoangkotlin.chatappsocial.feature.friend.navigation.friendsRoute
import com.hoangkotlin.chatappsocial.feature.home.navigation.CHANNELS_GRAPH_ROUTE_PATTERN
import com.hoangkotlin.chatappsocial.feature.home.navigation.channelsRoute
import com.hoangkotlin.chatappsocial.ui.isTopLevelDestinationInHierarchy
import com.hoangkotlin.chatappsocial.ui.main.navigation.SocialNavHost
import com.hoangkotlin.chatappsocial.ui.main.navigation.TopLevelDestination
import kotlinx.coroutines.launch
import com.hoangkotlin.chatappsocial.core.common.R as commonR
import com.hoangkotlin.chatappsocial.core.ui.R as uiR

@Composable
fun SocialApp(
    onLogOutClick: () -> Unit = {},
    onChannelClicked: (SocialChatChannel) -> Unit = {},
    currentUser: SocialChatUser? = PreviewUserData.me,
    onNavigateBackFromChannel: () -> Unit = {},
    onToggleBubbleClicked: (channelId: String) -> Unit = {},
    startDestination: String = CHANNELS_GRAPH_ROUTE_PATTERN,
    appState: SocialAppState = rememberSocialAppState(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
) {
    val onLogOutTriggered = {
        appState.coroutineScope.launch {
            drawerState.close()
        }
        onLogOutClick()
    }

    val onChannelClickedTrigger: (SocialChatChannel) -> Unit = {
        appState.coroutineScope.launch {
            drawerState.close()
        }
        onChannelClicked(it)
    }

    val gesturesEnabled = when {
        appState.currentDestination?.route == friendsRoute -> false
        appState.shouldShowDrawer -> true
        !appState.shouldShowDrawer -> false
        else -> true
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = appState.shouldShowDrawer,
        drawerContent = {
            if (appState.shouldShowDrawer) {
                SocialDrawerSheet(
                    currentDestination = appState.currentDestination,
                    destinations = appState.topLevelDestinations,
                    onItemClick = appState::navigateToTopLevelDestination,
                    onLogOutClick = onLogOutTriggered,
                    currentUser = currentUser
                )

            }

        },
    ) {
        Scaffold(
            topBar = {
                val labelRes =
                    appState.currentTopLevelDestination?.labelText
                        ?: commonR.string.chatting_label
                AnimatedVisibility(visible = appState.shouldShowDrawer) {
                    SocialTopAppBar(
                        modifier = Modifier,
                        title = {
                            Text(
                                text = stringResource(id = labelRes),
                                style = MaterialTheme.typography.headlineLarge
                            )
                        },
                        onMenuIconClick = {
                            appState.coroutineScope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = {},
                                colors = IconButtonDefaults.filledTonalIconButtonColors()
                            ) {
                                Icon(
                                    painter = painterResource(id = uiR.drawable.pen_svgrepo_com),
                                    contentDescription = null
                                )
                            }
                        }
                    )
                }
            },
        ) { padding ->
            SocialNavHost(
                modifier = Modifier.padding(
                    if (appState.shouldShowDrawer) padding else PaddingValues(
                        top = 0.dp,
                        bottom = padding.calculateBottomPadding(),
                        start = padding.calculateStartPadding(LocalLayoutDirection.current),
                        end = padding.calculateEndPadding(LocalLayoutDirection.current),

                        )
                ),
                appState = appState,
                startDestination = startDestination,
                onChannelClicked = onChannelClickedTrigger,
                onNavigateBackFromChannel = onNavigateBackFromChannel,
                onToggleBubbleClicked = onToggleBubbleClicked
            )
        }
    }
}


@Composable
fun SocialDrawerSheet(
    modifier: Modifier = Modifier,
    currentUser: SocialChatUser?,
    destinations: List<TopLevelDestination> = TopLevelDestination.entries,
    currentDestination: NavDestination?,
    onItemClick: (TopLevelDestination) -> Unit,
    onLogOutClick: () -> Unit
) {
    ModalDrawerSheet(modifier) {
        DrawerHeader(currentUser = currentUser)
        destinations.forEach { destination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            DrawerItem(
                modifier = Modifier,
                destination = destination,
                selected = selected,
                onItemClick = onItemClick
            )

        }
        Spacer(modifier = Modifier.weight(1f))
        DrawerLogoutItem(
            onLogOutClick = onLogOutClick
        )

    }

}

@Composable
fun DrawerHeader(
    modifier: Modifier = Modifier,
    currentUser: SocialChatUser?
) {
    Row(
        modifier = modifier
            .padding(8.dp)
            .clickable {

            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        SocialUserAvatar(
            user = currentUser ?: SocialChatUser(),
            modifier = Modifier
                .padding(8.dp)
                .size(40.dp),
            showOnlineIndicator = false
        )
        Text(
            text = currentUser?.name ?: "",
            style = MaterialTheme.typography.headlineMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),

            )
        IconButton(onClick = {}) {
            Icon(
                painterResource(id = uiR.drawable.settings_minimalistic_svgrepo_com),
                contentDescription = null
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialTopAppBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    onMenuIconClick: () -> Unit,
    actions: @Composable (RowScope.() -> Unit),
    subContent: @Composable () -> Unit = {}
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title = title,
            navigationIcon = {
                SocialDrawerNavigationIcon(onClick = onMenuIconClick)
            },
            actions = actions,

            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
        )
        subContent()
    }
}

@Composable
fun SocialDrawerNavigationIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
        colors = IconButtonDefaults.filledTonalIconButtonColors()
    ) {
        Icon(
            painter = painterResource(id = uiR.drawable.widget_2_svgrepo_com),
            contentDescription = null
        )
    }
}

@Composable
fun DrawerLogoutItem(
    modifier: Modifier = Modifier,
    onLogOutClick: () -> Unit
) {

    NavigationDrawerItem(
        modifier = modifier,
        icon = {
            Icon(
                painter = painterResource(id = uiR.drawable.logout_3_svgrepo_com),
                contentDescription = null,
                modifier = Modifier.size(36.dp),
            )
        },
        label = {
            Text(
                text = "Log out",
                style = MaterialTheme.typography.headlineMedium,
            )
        },
        selected = false,
        onClick = onLogOutClick
    )

}

@Composable
fun DrawerItem(
    modifier: Modifier = Modifier,
    destination: TopLevelDestination,
    selected: Boolean,
    onItemClick: (TopLevelDestination) -> Unit
) {
    NavigationDrawerItem(
        modifier = modifier,
        icon = {
            Icon(
                painter = painterResource(destination.iconRes),
                contentDescription = null,
            )
        },
        label = {
            Text(
                stringResource(destination.labelText),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        selected = selected,
        onClick = remember(destination, onItemClick) {
            {
                onItemClick(destination)
            }
        })
}

@Preview
@Composable
fun AppScaffoldPreview() {
    SocialChatAppTheme {

        val drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                SocialDrawerSheet(
                    onItemClick = {},
                    onLogOutClick = {},
                    currentUser = PreviewUserData.me,
                    currentDestination = null
                )


            },
        ) {
            Scaffold(
                topBar = {
                    val labelRes = commonR.string.chatting_label
                    SocialTopAppBar(
                        modifier = Modifier,
                        title = {
                            Text(
                                text = stringResource(id = labelRes),
                                style = MaterialTheme.typography.headlineLarge
                            )
                        },
                        onMenuIconClick = {

                        },
                        actions = {
                            IconButton(
                                onClick = {},
                                colors = IconButtonDefaults.filledTonalIconButtonColors()
                            ) {
                                Icon(
                                    painter = painterResource(id = uiR.drawable.pen_svgrepo_com),
                                    contentDescription = null
                                )
                            }
                        }
                    )
                },
            ) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {

                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ModalDrawerSheetPreview() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Open)
    SocialChatAppTheme {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                SocialDrawerSheet(
                    currentDestination = NavDestination(channelsRoute),
                    onItemClick = { },
                    onLogOutClick = {},
                    currentUser = PreviewUserData.me
                )
            }
        ) {

        }

    }
}


private const val TAG = "SocialApp"


