package com.hoangkotlin.feature.channel_detail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hoangkotlin.chatappsocial.core.ui.theme.SocialChatAppTheme
import com.hoangkotlin.feature.channel_detail.model.ChannelDetailOptionEntry
import com.hoangkotlin.feature.channel_detail.model.ChannelDetailOptionGroup
import com.hoangkotlin.feature.channel_detail.model.EntriesByGroup
import com.hoangkotlin.feature.channel_detail.model.tintColor

@Composable
fun ChannelDetailOptions(
    modifier: Modifier = Modifier,
    onOptionClick: (ChannelDetailOptionEntry) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        EntriesByGroup.forEach { (group, entries) ->
            ChannelDetailOptionGroup(group = group)
            entries.forEach { entry ->
                ChannelDetailOptionEntry(
                    entry = entry,
                    onOptionClick = onOptionClick
                )
            }
        }
    }
}

@Composable
fun ChannelDetailOptionGroup(
    modifier: Modifier = Modifier,
    group: ChannelDetailOptionGroup
) {
    Text(
        modifier = modifier.padding(8.dp),
        text = stringResource(id = group.labelRes),
        style = MaterialTheme.typography.headlineSmall
    )
}

@Composable
fun ChannelDetailOptionEntry(
    modifier: Modifier = Modifier,
    entry: ChannelDetailOptionEntry,
    onOptionClick: (ChannelDetailOptionEntry) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onOptionClick(entry) }
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .size(24.dp),
            painter = painterResource(id = entry.iconRes),
            tint = entry.tintColor,
            contentDescription = stringResource(id = entry.labelRes)
        )
        Text(
            text = stringResource(id = entry.labelRes),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChannelDetailOptionsPreview() {
    SocialChatAppTheme {
        ChannelDetailOptions(onOptionClick = {})
    }
}