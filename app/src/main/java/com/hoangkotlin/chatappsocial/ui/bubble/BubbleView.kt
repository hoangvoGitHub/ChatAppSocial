package com.hoangkotlin.chatappsocial.ui.bubble

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import com.hoangkotlin.chatappsocial.core.model.getDisplayName
import com.hoangkotlin.chatappsocial.core.ui.components.image.SocialChannelAvatar
import com.hoangkotlin.chatappsocial.core.ui.previewdatas.PreviewChannelData
import com.hoangkotlin.chatappsocial.core.ui.previewdatas.PreviewUserData
import com.hoangkotlin.chatappsocial.core.ui.theme.SocialChatAppTheme
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private const val TAG = "BubbleView"
val LocalActiveBubbles = staticCompositionLocalOf<Set<SocialChatChannel>> { emptySet() }

internal object AppVMStoreOwnersHolder {
    private val lock = ReentrantLock()
    private val storeOwnerMap: MutableMap<String, ViewModelStoreOwner> = mutableMapOf()

    fun getOwner(key: String): ViewModelStoreOwner = lock.withLock {
        storeOwnerMap.getOrPut(key) {
            object : ViewModelStoreOwner {
                override val viewModelStore = ViewModelStore()
            }
        }
    }

    fun remove(key: String) = lock.withLock {
        storeOwnerMap[key]?.viewModelStore?.clear()
        storeOwnerMap.remove(key)
    }
}


@Composable
fun BubbleFloatingView(
    modifier: Modifier = Modifier,
    onIsCollapsedChanged: (Boolean) -> Unit = {},
    onDrag: (offsetX: Float, offsetY: Float) -> Unit = { _, _ -> },
) {
    var isCollapsed by remember {
        mutableStateOf(true).also {
            onIsCollapsedChanged(it.value)
        }
    }

    val channels by remember {
        mutableStateOf(
            setOf(
                PreviewChannelData.channelWithTwoUsers,
                PreviewChannelData.channelWithThreeUsers,
                PreviewChannelData.channelWithFourUsers,
                PreviewChannelData.channelWithFiveUsers,
            )
        )
    }

    var channelsInBubble by remember {
        mutableStateOf(
            setOf(
                PreviewChannelData.channelWithTwoUsers,
                PreviewChannelData.channelWithThreeUsers,
                PreviewChannelData.channelWithFourUsers,
            )
        )
    }

    var selectedBubble by remember {
        mutableStateOf<SocialChatChannel?>(null)
    }

    var prevOffsetX by remember {
        mutableFloatStateOf(0f)
    }

    var prevOffsetY by remember {
        mutableFloatStateOf(0f)
    }

//    var pagerState = rememberPagerState {
//        channelsInBubble.size + 1
//    }
//
//    LaunchedEffect(selectedBubble) {
//        if (selectedBubble == null) {
//            pagerState.animateScrollToPage(0)
//        } else {
//            pagerState.animateScrollToPage(channelsInBubble.indexOf(selectedBubble) + 1)
//        }
//    }


//    LaunchedEffect(pagerState.currentPage) {
//        selectedTabIndex = pagerState.currentPage
//    }
    var offsetX by remember { mutableFloatStateOf(prevOffsetX) }
    var offsetY by remember { mutableFloatStateOf(prevOffsetY) }
    val drag = Modifier.pointerInput(Unit) {
        detectDragGestures(onDragStart = {}, onDrag = { change, dragAmount ->
            change.consume()
            offsetX = (offsetX + dragAmount.x)
            offsetY = (offsetY + dragAmount.y)
            onDrag(offsetX, offsetY)
        }, onDragEnd = {})
    }

    Box(modifier = modifier.then(drag)) {
        if (isCollapsed) {
//            BubbleCircle(
//                onDrag = { offsetX, offsetY ->
//                    prevOffsetX = offsetX
//                    prevOffsetY = offsetY
//                    onDrag(offsetX, offsetY)
////                isDragInProgress = offsetY > 200f
//                },
//                channel = selectedBubble ?: channelsInBubble.first(),
//                currentUser = PreviewUserData.user1,
//                onClicked = {
//                    selectedBubble = it
//                    isCollapsed = false
//                    onIsCollapsedChanged(false)
//                },
//                prevOffsetX = prevOffsetX,
//                prevOffsetY = prevOffsetY
//            )
            Row(
                modifier = Modifier
                    .clip(
                        GenericShape { size, _ ->
                            val height = size.height
                            val width = ((channelsInBubble.size + 1) * 62).toFloat()
                            this.addRect(Rect(0f, 0f, width, height))
                        }
                    )
                    .background(color = Color.Yellow)
                    .clickable {
                        isCollapsed = false
                        onIsCollapsedChanged(false)
                    }
            ) {
                channelsInBubble.forEachIndexed { index, socialChatChannel ->
                    val color = when (index) {
                        0 -> Color.Red
                        1 -> Color.Gray
                        2 -> Color.White
                        3 -> Color.Magenta
                        else -> Color.Black
                    }
                    BubbleCircle(
                        modifier = Modifier
                            .zIndex(-index.toFloat())
                            .offset(x = -(index * 62).dp)
                            .drawBehind {

                            },
                        onDrag = { offsetX, offsetY ->

                        },
                        channel = socialChatChannel,
                        currentUser = PreviewUserData.user1,
                        onClicked = {
                            selectedBubble = it
                            isCollapsed = false
                            onIsCollapsedChanged(false)
                        },
                        prevOffsetX = prevOffsetX,
                        prevOffsetY = prevOffsetY
                    )
                }
            }
        } else {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(color = Color.Transparent, shape = CircleShape)
                            .clickable {
                                if (selectedBubble == null) {
                                    isCollapsed = true
                                    onIsCollapsedChanged(true)
                                } else {
                                    selectedBubble = null
                                }
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Message, contentDescription = "",
                            modifier = Modifier
                                .size(72.dp)
                        )
                    }

                    channelsInBubble.forEach { channel ->
                        BubbleCircle(
                            channel = channel,
                            currentUser = PreviewUserData.user1,
                            onClicked = {
                                if (selectedBubble == it) {
                                    isCollapsed = true
                                    onIsCollapsedChanged(true)
                                } else {
                                    selectedBubble = it
                                }

                            },
                            isSelected = selectedBubble?.id == channel.id
                        )
                    }
                }

                Scaffold {
                    if (selectedBubble == null) {
                        LazyColumn(modifier = Modifier.padding(it)) {
                            items(channels.toList(), key = { item: SocialChatChannel -> item.id }) {
                                ListItem(headlineContent = {
                                    Text(
                                        text = it.getDisplayName(
                                            LocalContext.current,
                                            currentUser = PreviewUserData.user1
                                        )
                                    )
                                },
                                    modifier = Modifier.clickable {
                                        if (!channelsInBubble.contains(it) && channelsInBubble.size > 2) {
                                            val currentChannelsInBubble = channelsInBubble.toList()
                                            channelsInBubble =
                                                channelsInBubble.toMutableSet().apply {
                                                    add(it)
                                                    removeIf { channel -> channel.id == currentChannelsInBubble.first().id }
                                                    AppVMStoreOwnersHolder.remove(
                                                        currentChannelsInBubble.first().id
                                                    )
                                                }
                                        }
                                        selectedBubble = it
                                    })
                            }
                        }
                    } else {
//                        ProvidesViewModelStoreOwner(
//                            ownerKey = selectedBubble!!.id
//                        ) {
                        ChatView(selectedBubble!!)
//                        }
                    }

                }
            }
        }
    }


}

@Composable
fun CircleBubble(
    modifier: Modifier = Modifier,
    color: Color = Color.Transparent
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .size(72.dp)
            .background(color)
    )
}

@Preview
@Composable
fun BubblesOverlayPreview() {
    val tempList = List<Int>(6) { it }
    SocialChatAppTheme {
        val density = LocalDensity.current

        Row(
            modifier = Modifier
//            .clip(
//                GenericShape { size, _ ->
//                    val height = size.height
//
//                    with(density) {
//                        val width = (72 + (10 * (tempList.size - 1))).dp
//
//                        this@GenericShape.addRect(
//                            Rect(
//                                0f,
//                                0f,
////                                width
////                                    .roundToPx()
////                                    .toFloat()
//                                        size.width
//                                ,
//                                height
//                            )
//                        )
//                    }
//
//                }
//            )
//            .background(Color.Yellow)
        ) {

            tempList.forEach { num ->
                val color = when (num) {
                    0 -> Color.Red
                    1 -> Color.Gray
                    2 -> Color.White
                    3 -> Color.Blue
                    4 -> Color.DarkGray
                    5 -> Color.Green
                    6 -> Color.Cyan
                    else -> Color.DarkGray
                }
                CircleBubble(
                    modifier = Modifier
                        .zIndex(-num.toFloat())
                        .offset(x = -(num * 62).dp),
                    color = color
                )
            }
            Box(
                modifier = Modifier
                    .height(72.dp)
                    .width(10.dp)
                    .background(Color.Red)
            )
        }
    }


}


@Composable
fun ChatView(
    socialChatChannel: SocialChatChannel,
    modifier: Modifier = Modifier,
    isInActiveList: Boolean = true
) {


//    val myViewModel: MyViewModel =
//        viewModel(
//            factory = MyViewModelFactory(socialChatChannel.id),
////            key = socialChatChannel.id
//        )


    if (!isInActiveList) {
        AppVMStoreOwnersHolder.remove(socialChatChannel.id)
    }

    val state: BubbleState = BubbleState.Success(socialChatChannel.id)
//    Log.d(TAG, "ChatView: rebuild chatview ${socialChatChannel.id}")
    Scaffold {
        Column(
            modifier
                .padding(it)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedContent(targetState = state, label = "") { bubbleState ->
                when (bubbleState) {
                    is BubbleState.Failed -> Text(text = "failed")
                    is BubbleState.Loading -> CircularProgressIndicator()
                    is BubbleState.Success -> Text(text = bubbleState.id)
                }
            }


        }

    }

}

@Preview
@Composable
fun BubbleFloatingViewPreview() {
    SocialChatAppTheme {
        BubbleFloatingView()
    }
}


@Composable
fun BubbleCircle(
    channel: SocialChatChannel,
    currentUser: SocialChatUser?,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClicked: (SocialChatChannel) -> Unit = {},
    prevOffsetX: Float = 0f,
    prevOffsetY: Float = 0f,
    onDrag: (offsetX: Float, offsetY: Float) -> Unit = { _, _ -> },
) {

    var offsetX by remember { mutableFloatStateOf(prevOffsetX) }
    var offsetY by remember { mutableFloatStateOf(prevOffsetY) }
    val drag = Modifier.pointerInput(Unit) {
        detectDragGestures(onDragStart = {}, onDrag = { change, dragAmount ->
            change.consume()
            offsetX = (offsetX + dragAmount.x)
            offsetY = (offsetY + dragAmount.y)
            onDrag(offsetX, offsetY)
        }, onDragEnd = {})
    }

    SocialChannelAvatar(
        channel = channel,
        currentUser = currentUser,
        modifier = modifier
            .then(drag)
            .size(72.dp)
            .background(color = if (isSelected) Color.Red else Color.Transparent)
            .clickable {
                onClicked(channel)
            })
}

@Composable
fun OverlappingRow(
    modifier: Modifier = Modifier,
    overlappingPercentage: Float,
    content: @Composable () -> Unit
) {

    val factor = (1 - overlappingPercentage)

    Layout(
        modifier = modifier,
        content = content,
        measurePolicy = { measurables, constraints ->
            val placeables = measurables.map { it.measure(constraints) }
            val widthsExceptFirst = placeables.subList(1, placeables.size).sumOf { it.width }
            val firstWidth = placeables.getOrNull(0)?.width ?: 0
            val width = (widthsExceptFirst * factor + firstWidth).toInt()
            val height = placeables.maxOf { it.height }
            layout(width, height) {
                var x = 0
                for (placeable in placeables) {
                    placeable.placeRelative(x, 0, 0f)
                    x += (placeable.width * factor).toInt()
                }
            }
        }
    )
}

@Composable
fun OverlapTestView() {
    Column(modifier = Modifier.padding(16.dp)) {
        Box(
            modifier = Modifier
//                .clip(RectangleShape)
                .size(200.dp)
                .border(2.dp, Color.Black)
                .graphicsLayer {
                    clip = true
                    shape = CircleShape
                    translationY = 50.dp.toPx()
                }
                .background(Color(0xFFF06292))
        ) {
            Text(
                "Hello Compose",
                style = TextStyle(color = Color.Black, fontSize = 46.sp),
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(500.dp))
                .background(Color(0xFF4DB6AC))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OverlapTestViewPreview() {
    SocialChatAppTheme {
        OverlapTestView()
    }
}

@Preview
@Composable
fun BubbleCirclePreview() {
    SocialChatAppTheme {
        BubbleCircle(
            channel = PreviewChannelData.channelWithTwoUsers,
            currentUser = PreviewUserData.user1
        )
    }
}