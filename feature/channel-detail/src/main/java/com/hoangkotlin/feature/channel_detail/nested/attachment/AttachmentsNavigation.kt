package com.hoangkotlin.feature.channel_detail.nested.attachment

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.hoangkotlin.feature.channel_detail.navigation.channelArg
import java.util.Date

fun NavController.navigateToAttachments(channelId: String, navOptions: NavOptions? = null) {
    this.navigate("channels/$channelId/detail/attachments", navOptions)
}

fun NavGraphBuilder.attachmentsScreen(
    onBackPressed: () -> Unit,
    onNavigateToMediaViewer: (channelId: String, attachmentUri: String, createdAt: Date) -> Unit,
) {
    composable(
        route = "channels/{$channelArg}/detail/attachments",
        arguments = listOf(
            navArgument(channelArg) {
                type = NavType.StringType
                this.defaultValue = ""
            },
        )
    ) {
        AttachmentsRoute(
            onBackPressed = onBackPressed,
            onNavigateToMediaViewer = onNavigateToMediaViewer
        )
    }
}