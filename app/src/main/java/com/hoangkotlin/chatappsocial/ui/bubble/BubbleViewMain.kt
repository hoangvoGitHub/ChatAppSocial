package com.hoangkotlin.chatappsocial.ui.bubble

import android.graphics.PointF
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.hoangkotlin.chatappsocial.bubble_service.utils.BubbleConfig.BubbleOverlapRemainingSpace
import com.hoangkotlin.chatappsocial.bubble_service.utils.BubbleConfig.BubbleSize
import com.hoangkotlin.chatappsocial.core.common.utils.SetWithDefault
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import com.hoangkotlin.chatappsocial.core.ui.components.image.SocialChannelAvatar
import com.hoangkotlin.chatappsocial.core.ui.previewdatas.PreviewChannelData
import com.hoangkotlin.chatappsocial.core.ui.theme.SocialChatAppTheme
import com.hoangkotlin.chatappsocial.feature.home.components.UnreadCountIndicator
import com.hoangkotlin.chatappsocial.feature.home.navigation.channelsRoute
import com.hoangkotlin.chatappsocial.ui.main.SocialApp
import com.hoangkotlin.chatappsocial.ui.main.rememberSocialAppState
import com.hoangkotlin.feature.chat.navigation.navigateToChatGraph
import kotlinx.coroutines.launch

private const val TAG = "BubbleViewMain"

sealed class Bubble {
    data object HomeBubble : Bubble()
    data class ChannelBubble(val channel: SocialChatChannel) : Bubble() {
        override fun equals(other: Any?): Boolean {
            other as Bubble
            return when (other) {
                is ChannelBubble -> this.channel.id == other.channel.id
                HomeBubble -> false
            }

        }

        override fun hashCode(): Int {
            return channel.hashCode()
        }
    }
}

internal class CompositionScopedViewModelStoreOwner : ViewModelStoreOwner, RememberObserver {

    override val viewModelStore: ViewModelStore
        get() = ViewModelStore()

    override fun onAbandoned() {
        viewModelStore.clear()
    }

    override fun onForgotten() {
        viewModelStore.clear()
    }

    override fun onRemembered() {
        // Nothing to do here
    }
}

@Composable
fun ProvideViewModels(content: @Composable () -> Unit) {
    val viewModelStoreOwner = remember { CompositionScopedViewModelStoreOwner() }
    CompositionLocalProvider(LocalViewModelStoreOwner provides viewModelStoreOwner) {
        content()
    }
}


@Composable
fun FloatingViewMain(
    modifier: Modifier = Modifier,
    currentUser: SocialChatUser?,
    onIsCollapsedChanged: (Boolean) -> Unit = {},
    onDrag: (offsetX: Float, offsetY: Float) -> Unit = { _, _ -> },
    onDragEnd: () -> Unit = {},
    bubbleAlignment: BubbleAlignment = BubbleAlignment.Start,
    isDraggingEnable: Boolean = true,
    previousPosition: PointF = PointF(0f, 0f)
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val drag = Modifier.pointerInput(Unit) {
        detectDragGestures(
            onDragStart = {
                Log.d(TAG, "onDragStart: $previousPosition")
                offsetX = previousPosition.x
                offsetY = previousPosition.y
            },
            onDrag = { change, dragAmount ->
                change.consume()
                offsetX = (offsetX + dragAmount.x)
                offsetY = (offsetY + dragAmount.y)
                onDrag(offsetX, offsetY)
            },
            onDragEnd = {
                onDragEnd()
            }
        )
    }

    var isCollapsed by remember {
        mutableStateOf(false)
    }

    val initialBubbles =
        SetWithDefault<Bubble>(default = Bubble.HomeBubble, maxSizeExcludeDefault = 4)

    var currentBubbles by remember {
        mutableStateOf(initialBubbles)
    }

    var activeBubble by remember {
        mutableStateOf<Bubble>(Bubble.HomeBubble)
    }

    val navController = rememberNavController()

    val appState = rememberSocialAppState(
        navController = navController
    )

    val drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val currentDestination = appState.currentDestination?.route

    DisposableEffect(key1 = isCollapsed) {
        onIsCollapsedChanged(isCollapsed)

        onDispose { }
    }

    val onBubbleClickTriggered: (Bubble) -> Unit = { bubble ->
        appState.coroutineScope.launch {
            drawerState.close()
        }
        if (activeBubble != bubble) {
            if (bubble is Bubble.ChannelBubble) {
                navController.navigateToChatGraph(bubble.channel.id, navOptions {
                    if (activeBubble is Bubble.ChannelBubble) {
                        //Popup to start destination (channelsGraph) before navigating
                        //to avoid building up a large stack of destinations
                        popUpTo(navController.graph.findStartDestination().id)
                    }
                })

            } else {
                if (currentDestination == "channels/{channelId}/detail") {
                    navController.popBackStack(
                        channelsRoute,
                        inclusive = false,
                        saveState = true
                    )
                } else {
                    navController.popBackStack()
                }
            }
            activeBubble = bubble
        } else {
            isCollapsed = true
        }
    }

    Box(
        modifier = modifier
            .then(if (isDraggingEnable) drag else Modifier)
    ) {
        AnimatedVisibility(visible = isCollapsed) {
            CollapsedBubbles(
                currentBubbles = currentBubbles.items,
                onBubbleClicked = {
                    isCollapsed = false
                },
                currentUser = currentUser,
                bubbleAlignment = bubbleAlignment
            )
        }

        AnimatedVisibility(visible = !isCollapsed) {
            Column {
                AnimatedContent(targetState = currentBubbles, label = "",
                    transitionSpec = {
                        if (targetState.items.size > initialState.items.size) {
                            // If the target number is larger, it slides up and fades in
                            // while the initial (smaller) number slides up and fades out.
                            (slideInVertically { height -> height } + fadeIn()).togetherWith(
                                slideOutVertically { height -> -height } + fadeOut())
                        } else {
                            // If the target number is smaller, it slides down and fades in
                            // while the initial number slides down and fades out.
                            (slideInVertically { height -> -height } + fadeIn()).togetherWith(
                                slideOutVertically { height -> height } + fadeOut())
                        }.using(
                            // Disable clipping since the faded slide-in/out should
                            // be displayed out of bounds.
                            SizeTransform(clip = false)
                        )
                    }) { bubbles ->

                    ActiveBubblesRow(
                        activeBubbles = bubbles.items,
                        onBubbleClicked = onBubbleClickTriggered,
                        activeBubble = activeBubble,
                        currentUser = currentUser,
                        isCollapsed = isCollapsed
                    )
                }

                SocialApp(
                    appState = appState,
                    onLogOutClick = {},
                    onNavigateBackFromChannel = {
                        activeBubble = Bubble.HomeBubble
                    },
                    onChannelClicked = { channel ->
                        val newBubble = Bubble.ChannelBubble(channel = channel)
                        currentBubbles = currentBubbles.apply {
                            add(newBubble)
                        }
                        activeBubble = newBubble
                    },
                    drawerState = drawerState,
                    currentUser = currentUser
                )
            }
        }
    }
//    }
}

@Composable
fun CollapsedBubbles(
    modifier: Modifier = Modifier,
    currentBubbles: List<Bubble>,
    currentUser: SocialChatUser?,
    onBubbleClicked: (Bubble) -> Unit,
    bubbleAlignment: BubbleAlignment
) {
    val width = (BubbleSize + (currentBubbles.size - 1)
            * BubbleOverlapRemainingSpace).dp

    val bubbleToDisplay = remember(bubbleAlignment, currentBubbles) {
        if (bubbleAlignment == BubbleAlignment.End) {
            currentBubbles
        } else {
            currentBubbles.reversed()
        }

    }
    val zIndexMultiplier = if (bubbleAlignment == BubbleAlignment.Start) 1 else -1

    Box(modifier = modifier) {
        Box(modifier = Modifier.width(width)) {
            bubbleToDisplay.forEachIndexed { index, bubble ->
                FloatingBubble(
                    currentUser = currentUser,
                    modifier = Modifier
                        .zIndex((-index).toFloat())
                        .offset(x = (index * BubbleOverlapRemainingSpace).dp)
                        .zIndex(zIndexMultiplier * index.toFloat()),
                    bubble = bubble,
                    onClicked = onBubbleClicked
                )
            }
        }
    }
}

@Composable
fun ActiveBubblesRow(
    modifier: Modifier = Modifier,
    activeBubbles: List<Bubble>,
    onBubbleClicked: (Bubble) -> Unit,
    currentUser: SocialChatUser?,
    activeBubble: Bubble,
    isCollapsed: Boolean
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        activeBubbles.forEach { bubble ->
            FloatingBubble(
                bubble = bubble,
                onClicked = onBubbleClicked,
                isActive = bubble == activeBubble,
                currentUser = currentUser,
                isCollapsed = isCollapsed
            )
        }
    }
}


@Composable
fun FloatingBubble(
    modifier: Modifier = Modifier,
    currentUser: SocialChatUser?,
    bubble: Bubble,
    isActive: Boolean = false,
    isCollapsed: Boolean = true,
    shouldShowUnreadCount: Boolean = false,
    onClicked: (Bubble) -> Unit = {}
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (bubble) {
            is Bubble.ChannelBubble -> ChannelBubbleContainer(
                channelBubble = bubble,
                shouldShowUnreadCount = shouldShowUnreadCount,
                currentUser = currentUser,
                onBubbleClicked = onClicked
            )

            is Bubble.HomeBubble -> HomeBubbleContainer(
                onClicked = {
                    onClicked(bubble)
                }
            )
        }
        if (!isCollapsed) {
            Box(
                modifier = Modifier

                    .clip(
                        // create a rectangle
                        GenericShape { size, _ ->
                            // 1)
                            moveTo(size.width / 2f, 0f)

                            // 2)
                            lineTo(size.width, size.height)

                            // 3)
                            lineTo(0f, size.height)
                        }
                    )
                    .size(16.dp)
                    .background(
                        if (isActive) {
                            Color.Black
                        } else {
                            Color.Transparent
                        }
                    )
                    .padding(top = 8.dp)
            )
        }
    }

}

@Composable
fun ChannelBubbleContainer(
    modifier: Modifier = Modifier,
    currentUser: SocialChatUser? = null,
    channelBubble: Bubble.ChannelBubble,
    shouldShowUnreadCount: Boolean = false,
    onBubbleClicked: (Bubble) -> Unit
) {
    Box(modifier = modifier) {
        SocialChannelAvatar(
            modifier = Modifier
                .size(BubbleSize.dp)
                .clip(CircleShape)
                .clickable { onBubbleClicked(channelBubble) },
            channel = channelBubble.channel,
            currentUser = currentUser,
            showOnlineIndicator = false
        )
        if (shouldShowUnreadCount && channelBubble.channel.unreadCount > 0) {
            UnreadCountIndicator(
                modifier = Modifier.align(Alignment.TopEnd),
                unreadCount = channelBubble.channel.unreadCount
            )
        }
    }
}


@Composable
fun HomeBubbleContainer(
    modifier: Modifier = Modifier,
    onClicked: () -> Unit,
) {
    Box(
        modifier = modifier
            .background(color = Color.LightGray, shape = CircleShape)
            .clip(CircleShape)
            .clickable { onClicked() }
    ) {
        Icon(
            modifier = Modifier
                .size(BubbleSize.dp)
                .padding(16.dp),
            imageVector = Icons.Default.ChatBubbleOutline,
            contentDescription = null
        )
    }
}

@Composable
fun FloatingCloseBubble(
    modifier: Modifier = Modifier,
    shouldZoomCloseView: Boolean = false
) {
    val animatedSize by animateDpAsState(
        targetValue = if (shouldZoomCloseView) BubbleSize.dp.plus(40.dp) else BubbleSize.dp,
        label = "animatedSize"
    )

    val animatedColor by animateColorAsState(
        targetValue = if (shouldZoomCloseView)
            Color.Red.copy(alpha = 0.3f)
        else
            Color.Gray.copy(0.5f),
        label = "animatedColor"
    )

    Box(
        modifier = modifier
            .background(animatedColor.copy(0.3f), CircleShape)
            .size(animatedSize)
            .clip(CircleShape)
    ) {
        Icon(
            imageVector = Icons.Rounded.Close, contentDescription = "Floating Close Bubble",
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.Center)
                .size(BubbleSize.dp)
                .border(
                    BorderStroke((1).dp, Color.White),
                    CircleShape
                )
                .background(
                    Color.Transparent,
                    CircleShape
                )
                .padding(8.dp)
                .clip(CircleShape)
        )
    }
}

@Preview
@Composable
fun FloatingCloseBubblePreview(shouldZoomCloseView: Boolean = false) {
    SocialChatAppTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                FloatingCloseBubble(
                    shouldZoomCloseView = shouldZoomCloseView
                )
            }
        }

    }
}

@Preview
@Composable
fun FloatingCloseBubbleZoomPreview() {
    FloatingCloseBubblePreview(shouldZoomCloseView = true)
}

@Preview
@Composable
fun FloatingMainBubblePreview() {
    SocialChatAppTheme {
        FloatingViewMain(
            currentUser = null,
        )
    }
}

@Preview
@Composable
fun FloatChannelBubblePreview() {
    SocialChatAppTheme {
        ChannelBubbleContainer(
            currentUser = null,
            channelBubble = Bubble.ChannelBubble(
                channel = PreviewChannelData.channelWithTwoUsers.copy(
                    unreadCount = 10
                )
            ),
            shouldShowUnreadCount = true,
            onBubbleClicked = {}
        )
    }
}

@Preview
@Composable
fun BubbleContainersAlignStartPreview() {
    val bubbles = listOf(
        Bubble.HomeBubble,
        Bubble.ChannelBubble(channel = PreviewChannelData.channelWithImage.copy(unreadCount = 10)),
        Bubble.ChannelBubble(channel = PreviewChannelData.channelWithTwoUsers),
        Bubble.ChannelBubble(channel = PreviewChannelData.channelWithThreeUsers)
    )
    SocialChatAppTheme {
        CollapsedBubbles(
            currentUser = null,
            currentBubbles = bubbles,
            onBubbleClicked = {},
            bubbleAlignment = BubbleAlignment.Start
        )
    }
}

@Preview
@Composable
fun BubbleContainersAlignEndPreview() {
    val bubbles = listOf(
        Bubble.HomeBubble,
        Bubble.ChannelBubble(channel = PreviewChannelData.channelWithImage.copy(unreadCount = 10)),
        Bubble.ChannelBubble(channel = PreviewChannelData.channelWithTwoUsers),
        Bubble.ChannelBubble(channel = PreviewChannelData.channelWithThreeUsers)
    )
    SocialChatAppTheme {
        CollapsedBubbles(
            currentUser = null,
            currentBubbles = bubbles,
            onBubbleClicked = {},
            bubbleAlignment = BubbleAlignment.End
        )
    }
}