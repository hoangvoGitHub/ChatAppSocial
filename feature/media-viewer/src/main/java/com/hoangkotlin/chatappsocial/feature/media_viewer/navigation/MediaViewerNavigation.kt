package com.hoangkotlin.chatappsocial.feature.media_viewer.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.hoangkotlin.chatappsocial.feature.media_viewer.MediaViewerRoute
import java.net.URLEncoder
import java.util.Date
import kotlin.text.Charsets.UTF_8

private const val mediaViewerRoute = "media_viewer"
const val mediaArg = "media_id"
const val mediaDateArg = "media_created_at"
const val channelArg = "channel_id"

private val URL_CHARACTER_ENCODING = UTF_8.name()

internal class AttachmentArg(val uri: String, val createdAt: Date) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(
                checkNotNull(savedStateHandle[mediaArg]),
                Date(checkNotNull(savedStateHandle.get<Long>(mediaDateArg)))
            )
}

fun NavController.navigateToMediaViewerScreen(
    channelId: String,
    attachmentUri: String,
    createdAt: Date,
    navOptions: NavOptions? = null
) {
    val encodedUri = URLEncoder.encode(attachmentUri, URL_CHARACTER_ENCODING)
    val timeStamp = createdAt.time
    this.navigate("channels/$channelId/$mediaViewerRoute/$encodedUri/$timeStamp", navOptions)
}

fun NavGraphBuilder.mediaViewerScreen(
    onBackPress: ()-> Unit
) {
    composable(
        route = "channels/{$channelArg}/$mediaViewerRoute/{$mediaArg}/{$mediaDateArg}",
        arguments = listOf(
            navArgument(channelArg) {
                type = NavType.StringType
                this.defaultValue = ""
            },
            navArgument(mediaArg) {
                type = NavType.StringType
                this.defaultValue = ""
            },
            navArgument(mediaDateArg) {
                type = NavType.LongType
                this.defaultValue = 0L
            },
        ),
    ) {
        MediaViewerRoute(
            onBackPress = onBackPress
        )
    }
}