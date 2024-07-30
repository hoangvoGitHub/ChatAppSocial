package com.hoangkotlin.feature.channel_detail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hoangkotlin.feature.channel_detail.ChannelDetailAction
import com.hoangkotlin.feature.channel_detail.DefaultChannelDetailActions


@Composable
fun ChannelDetailActionsContainer(
    modifier: Modifier = Modifier,
    onAction: (ChannelDetailAction) -> Unit,
    actions: List<ChannelDetailAction> = DefaultChannelDetailActions
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        actions.forEach { action ->
            ChannelDetailAction(
                action = action,
                onAction = onAction
            )
        }
    }
}

@Composable
fun ChannelDetailAction(
    modifier: Modifier = Modifier,
    action: ChannelDetailAction,
    onAction: (ChannelDetailAction) -> Unit
) {
    Box(modifier = modifier
        .clip(CircleShape)
        .clickable { onAction(action) }) {
        Icon(
            modifier = Modifier
                .padding(8.dp)
                .size(32.dp),
            painter = painterResource(id = action.iconRes),
            contentDescription = stringResource(id = action.labelRes)
        )
    }
}