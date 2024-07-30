package com.hoangkotlin.feature.channel_detail.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.hoangkotlin.chatappsocial.core.ui.R
import com.hoangkotlin.chatappsocial.core.ui.utils.LocalRunningForBubble
import com.hoangkotlin.feature.channel_detail.model.ChannelDetailDropdown
import com.hoangkotlin.feature.channel_detail.model.ChannelDetailDropdownsForBubble
import com.hoangkotlin.feature.channel_detail.model.ChannelDetailDropdownsForNonBubble

@Composable
fun ChannelDetailMoreDropdownMenu(
    modifier: Modifier = Modifier,
    onItemClick: (ChannelDetailDropdown) -> Unit,
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    ChannelDetailMoreDropdown(
        expanded = expanded,
        onDismissRequest = {
            expanded = false
        },
        onItemClick = onItemClick
    )

    IconButton(
        onClick = {
            expanded = !expanded
        },
        modifier = modifier
    ) {
        Icon(painter = painterResource(id = R.drawable.noun_more), contentDescription = "More")
    }
}

@Composable
fun ChannelDetailMoreDropdown(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onItemClick: (ChannelDetailDropdown) -> Unit
) {
    val isRunningInBubble = LocalRunningForBubble.current
    val dropdownItems = if (isRunningInBubble) {
        ChannelDetailDropdownsForBubble
    } else {
        ChannelDetailDropdownsForNonBubble
    }
    DropdownMenu(
        modifier = modifier,
        offset = DpOffset(x = 16.dp, y = 0.dp),
        expanded = expanded, onDismissRequest = onDismissRequest
    ) {
        dropdownItems.forEach { dropdownItem ->
            DefaultChannelDetailDropdownItem(dropdownItem = dropdownItem, onClick = onItemClick)
        }

    }
}

@Composable
fun DefaultChannelDetailDropdownItem(
    modifier: Modifier = Modifier,
    dropdownItem: ChannelDetailDropdown,
    onClick: (ChannelDetailDropdown) -> Unit
) {
    DropdownMenuItem(
        modifier = modifier,
        text = { Text(text = stringResource(id = dropdownItem.labelRes)) },
        onClick = { onClick(dropdownItem) })
}