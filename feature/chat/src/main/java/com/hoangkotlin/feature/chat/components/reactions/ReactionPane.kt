package com.hoangkotlin.feature.chat.components.reactions

import android.view.MotionEvent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.hoangkotlin.chatappsocial.core.ui.theme.SocialChatAppTheme

@Composable
fun ReactionPane(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.secondaryContainer,
                RoundedCornerShape(16.dp)
            )
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        testReactions.forEach { reaction ->
            Reaction(reaction = reaction, onReactionSelected = {})
        }
        IconButton(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.outlineVariant,
                    CircleShape
                )
                .size(24.dp),
            onClick = { /*TODO*/ }) {
            Icon(
                imageVector = Icons.Rounded.Add, contentDescription =
                Icons.Rounded.Add.name,
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}

private val testReactions = listOf<String>(
    "\uD83D\uDE05",
    "❤\uFE0F",
    "\uD83D\uDE25",
    "\uD83D\uDCA9",
    "\uD83D\uDE21"
)

@Composable
fun Reaction(
    modifier: Modifier = Modifier,
    reaction: String,
    onReactionSelected: (String) -> Unit,
    fontSize: TextUnit = 20.sp
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(modifier = modifier
        .clickable(
            interactionSource = interactionSource,
            indication = null
        ) {
            onReactionSelected(reaction)
        }) {
        Text(
            text = reaction,
            fontSize = fontSize,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@ExperimentalComposeUiApi
@Composable
fun ReactionsComponent() {
    val density = LocalDensity.current
    var selectedIndex by remember {
        mutableIntStateOf(-1)
    }
    val iconSize = 30.dp
    val boxPadding = 8.dp
    val iconSizePx = with(density) { iconSize.toPx() }
    val boxPaddingPx = with(density) { boxPadding.toPx() }
    val increaseSize = iconSize.times(2f)
    val icons = listOf(
        Icons.Default.Favorite,
        Icons.Default.Star,
        Icons.Default.Call,
        Icons.Default.AccountBox,
        Icons.Default.ThumbUp
    )
    Box(
        Modifier
            .height(increaseSize + 2 * boxPadding)
            .width(IntrinsicSize.Min)
            .pointerInteropFilter {
                val selection = ((it.x - boxPaddingPx) / iconSizePx).toInt()
                selectedIndex =
                    if (selection >= icons.size || selection < 0 || it.x < boxPaddingPx) {
                        -1
                    } else if (it.action == MotionEvent.ACTION_UP) {
                        -1 // finger released
                    } else {
                        selection
                    }
                true
            }
    ) {
        Box(
            Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .height(iconSize + boxPadding.times(2))
                .background(Color.LightGray, CircleShape)
        )
        Row(
            Modifier
                .align(Alignment.BottomStart)
                .width(IntrinsicSize.Min)
                .padding(boxPadding),
            verticalAlignment = Alignment.Bottom
        ) {
            testReactions.forEachIndexed { index, icon ->
                val size = if (selectedIndex == index) increaseSize else iconSize
                val animateSize by animateDpAsState(size, label = "animateSize")
                Reaction(
                    fontSize = animateSize.value.times(0.5).sp,
                    reaction = icon,
                    modifier = Modifier
                        .size(animateSize)
                        .background(Color.White, CircleShape)
                        .clip(CircleShape),
                    onReactionSelected = {}
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun ReactionPanePreview() {
    SocialChatAppTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            ReactionPane()
            ReactionsComponent()
        }

    }
}