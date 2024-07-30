package com.hoangkotlin.chatappsocial.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hoangkotlin.chatappsocial.core.ui.theme.SocialChatAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultSearchBar(
    modifier: Modifier = Modifier,
    onSearchBarClick: () -> Unit,
    placeHolderText: String = "Search something"
) {
    Box(
        modifier = modifier
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .clip(SearchBarDefaults.dockedShape)
                .clickable {
                    onSearchBarClick()
                }
                .fillMaxWidth()
//                .padding(16.dp)
                .height(SearchBarDefaults.InputFieldHeight)
                .background(
                    shape = SearchBarDefaults.dockedShape,
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
        ) {
            Icon(
                imageVector = Icons.Default.Search, contentDescription = "Search",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Text(
                text = placeHolderText,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview
@Composable
fun SearchBarContainerPreview() {
    SocialChatAppTheme {
        DefaultSearchBar(
            onSearchBarClick = {}
        )
    }
}