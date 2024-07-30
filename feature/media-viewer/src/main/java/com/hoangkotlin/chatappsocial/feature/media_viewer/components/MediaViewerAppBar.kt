package com.hoangkotlin.chatappsocial.feature.media_viewer.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.hoangkotlin.chatappsocial.core.ui.components.BackButton
import com.hoangkotlin.chatappsocial.feature.media_viewer.model.DefaultMediaViewerActions
import com.hoangkotlin.chatappsocial.feature.media_viewer.model.MediaViewerAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultMediaViewerAppBar(
    modifier: Modifier = Modifier,
    onActionClick: (MediaViewerAction) -> Unit,
    onBackPress: () -> Unit
) {
    TopAppBar(
        modifier = modifier,
        title = { },
        navigationIcon = {
            BackButton(onBackPressed = onBackPress)
        },
        actions = {
            MediaViewerActions(
                actions = DefaultMediaViewerActions,
                onActionClick = onActionClick
            )
        }
    )
}

@Composable
fun MediaViewerActions(
    actions: List<MediaViewerAction>,
    onActionClick: (MediaViewerAction) -> Unit
) {
    actions.forEach { action ->
        MediaViewerAction(
            action = action,
            onActionClick = onActionClick
        )
    }
}

@Composable
fun MediaViewerAction(
    modifier: Modifier = Modifier,
    action: MediaViewerAction,
    onActionClick: (MediaViewerAction) -> Unit,
) {
    IconButton(
        modifier = modifier,
        onClick = { onActionClick(action) }) {
        Icon(
            painter = painterResource(id = action.iconRes),
            contentDescription = stringResource(id = action.labelRes)
        )
    }
}

