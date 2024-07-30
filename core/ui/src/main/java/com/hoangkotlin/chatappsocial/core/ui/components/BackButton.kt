package com.hoangkotlin.chatappsocial.core.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun BackButton(
    painter: Painter,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        modifier = modifier,
        onClick = onBackPressed
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
//            tint = MaterialTheme.colorScheme.text,
        )
    }
}

@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    vector: ImageVector = Icons.AutoMirrored.Filled.ArrowBack,
    onBackPressed: () -> Unit,
) {
    IconButton(
        modifier = modifier,
        onClick = onBackPressed
    ) {
        Icon(
            imageVector = vector,
            contentDescription = null,
//            tint = MaterialTheme.colorScheme.text,
        )
    }
}