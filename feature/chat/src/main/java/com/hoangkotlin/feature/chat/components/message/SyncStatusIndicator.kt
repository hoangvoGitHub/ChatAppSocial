package com.hoangkotlin.feature.chat.components.message

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hoangkotlin.chatappsocial.core.common.utils.SyncStatus

@Composable
fun SyncStatusIndicator(
    modifier: Modifier = Modifier,
    syncStatus: SyncStatus,
) {
    Box(modifier = modifier.size(16.dp)) {
        Icon(
            imageVector = if (syncStatus == SyncStatus.COMPLETED)
                Icons.Default.CheckCircle
            else
                Icons.Outlined.Circle,
            contentDescription = ""
        )
    }
}