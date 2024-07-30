package com.hoangkotlin.feature.chat.components.composer

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.hoangkotlin.chatappsocial.core.ui.theme.SocialChatAppTheme
import com.hoangkotlin.feature.chat.model.ComposerUtility
import com.hoangkotlin.feature.chat.model.DefaultPanelUtilities


@Composable
fun MoreUtilitiesDropdown(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onUtilityClick: (ComposerUtility) -> Unit
) {
    DropdownMenu(
        modifier = modifier,
        offset = DpOffset(x = 16.dp, y = 0.dp),
        expanded = expanded, onDismissRequest = onDismissRequest
    ) {
        DefaultPanelUtilities.forEach { composerUtility ->
            DefaultUtilityDropdownItem(utility = composerUtility, onClick = onUtilityClick)
        }
    }
}


@Composable
fun DefaultUtilityDropdownItem(
    utility: ComposerUtility,
    modifier: Modifier = Modifier,
    onClick: (ComposerUtility) -> Unit,
) {
    DropdownMenuItem(
        modifier = modifier,
        text = {
            Text(
                text = stringResource(id = utility.labelRes),
                style = MaterialTheme.typography.labelLarge
            )
        },
        onClick = { onClick(utility) },
        leadingIcon = {
            Icon(
                painter = painterResource(id = utility.iconRes),
                contentDescription = stringResource(
                    id = utility.labelRes
                )
            )
        },
    )
}

@Preview(showBackground = true)
@Composable
fun MoreUtilitiesPanelPreview() {
    SocialChatAppTheme {

        Box(modifier = Modifier.fillMaxSize()) {

            MoreUtilitiesDropdown(
                modifier = Modifier.align(Alignment.BottomCenter),
                onUtilityClick = {},
                onDismissRequest = {},
                expanded = true
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MoreUtilitiesPanelPreviewDark() {
    MoreUtilitiesPanelPreview()
}