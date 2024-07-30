package com.hoangkotlin.chatappsocial.core.ui.components.image

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import com.hoangkotlin.chatappsocial.core.ui.previewdatas.PreviewChannelData
import com.hoangkotlin.chatappsocial.core.ui.previewdatas.PreviewUserData
import com.hoangkotlin.chatappsocial.core.ui.theme.SocialChatAppTheme


/**
 * Represents the [Channel] avatar that's shown when browsing channels or when you open the Messages screen.
 *
 * Based on the state of the [Channel] and the number of members, it shows different types of images.
 *
 * @param channel The channel whose data we need to show.
 * @param currentUser The current user, used to determine avatar data.
 * @param modifier Modifier for styling.
 * @param shape The shape of the avatar.
 * @param textStyle The [TextStyle] that will be used for the initials.
 * @param groupAvatarTextStyle The [TextStyle] that will be used for the initials in sectioned avatar.
 * @param showOnlineIndicator If we show online indicator or not.
 * @param onlineIndicatorAlignment The alignment of online indicator.
 * @param onlineIndicator Custom composable that allows to replace the default online indicator.
 * @param contentDescription The description to use for the avatar.
 * @param onClick The handler when the user clicks on the avatar.
 */
@Composable

fun SocialChannelAvatar(
    channel: SocialChatChannel,
    currentUser: SocialChatUser?,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
    groupAvatarTextStyle: TextStyle = MaterialTheme.typography.labelMedium,
    showOnlineIndicator: Boolean = true,
    onlineIndicatorAlignment: OnlineIndicatorAlignment = OnlineIndicatorAlignment.TopEnd,
    onlineIndicator: @Composable BoxScope.() -> Unit = {
        DefaultOnlineIndicator(onlineIndicatorAlignment)
    },
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null,
) {
    val members = channel.members
    val memberCount = members.size

    when {
        /**
         * If the channel has an image we load that as a priority.
         */

        channel.image.isNotEmpty() -> {
            SocialAvatar(
                modifier = modifier,
                imageUrl = channel.image,
                initials = channel.name.trim()
                    .split("\\s+".toRegex())
                    .take(2)
                    .joinToString(separator = "") { it.take(1).uppercase() },
                textStyle = textStyle,
                shape = shape,
                contentDescription = contentDescription,
                onClick = onClick
            )
        }

        /**
         * If the channel has one member we show the member's image or initials.
         */
        memberCount == 1 -> {
            val user = members.first().user

            SocialUserAvatar(
                modifier = modifier,
                user = user,
                shape = shape,
                contentDescription = user.name,
                showOnlineIndicator = showOnlineIndicator,
                onlineIndicatorAlignment = onlineIndicatorAlignment,
                onlineIndicator = onlineIndicator,
                onClick = onClick
            )
        }
        /**
         * If the channel has two members and one of the is the current user - we show the other
         * member's image or initials.
         */
        memberCount == 2 && members.any { it.user.id == currentUser?.id } -> {
            val user = members.first { it.user.id != currentUser?.id }.user

            SocialUserAvatar(
                modifier = modifier,
                user = user,
                shape = shape,
                contentDescription = user.name,
                showOnlineIndicator = showOnlineIndicator,
                onlineIndicatorAlignment = onlineIndicatorAlignment,
                onlineIndicator = onlineIndicator,
                onClick = onClick
            )
        }
        /**
         * If the channel has more than two members - we load a matrix of their images or initials.
         */
        else -> {
            val users = members.filter { it.user.id != currentUser?.id }.map { it.user }

            GroupAvatar(
                users = users,
                modifier = modifier,
                shape = shape,
                textStyle = groupAvatarTextStyle,
                onClick = onClick,
            )
        }
    }
}

/**
 * Preview of [SocialChannelAvatar] for a channel with an avatar image.
 *
 * Should show a channel image.
 */
@Preview(showBackground = true, name = "ChannelAvatar Preview (With image)")
@Composable
private fun ChannelWithImageAvatarPreview() {
    ChannelAvatarPreview(PreviewChannelData.channelWithImage)
}

///**
// * Preview of [ChannelAvatar] for a direct conversation with an online user.
// *
// * Should show a user avatar with an online indicator.
// */
//@Preview(showBackground = true, name = "ChannelAvatar Preview (Online user)")
//@Composable
//private fun ChannelAvatarForDirectChannelWithOnlineUserPreview() {
//    ChannelAvatarPreview(PreviewChannelData.channelWithOnlineUser)
//}

/**
 * Preview of [SocialChannelAvatar] for a direct conversation with only one user.
 *
 * Should show a user avatar with an online indicator.
 */
@Preview(showBackground = true, name = "ChannelAvatar Preview (Only one user)")
@Composable
private fun ChannelAvatarForDirectChannelWithOneUserPreview() {
    ChannelAvatarPreview(PreviewChannelData.channelWithTwoUsers)
}

/**
 * Preview of [SocialChannelAvatar] for a channel without image and with few members.
 *
 * Should show an avatar with 2 sections that represent the avatars of the first
 * 2 members of the channel.
 */
@Preview(showBackground = true, name = "ChannelAvatar Preview (Few members)")
@Composable
private fun ChannelAvatarForChannelWithFewMembersPreview() {
    ChannelAvatarPreview(PreviewChannelData.channelWithThreeUsers)
}

/**
 * Preview of [SocialChannelAvatar] for a channel without image and with many members.
 *
 * Should show an avatar with 4 sections that represent the avatars of the first
 * 4 members of the channel.
 */
@Preview(showBackground = true, name = "ChannelAvatar Preview (Many members)")
@Composable
private fun ChannelAvatarForChannelWithManyMembersPreview() {
    ChannelAvatarPreview(PreviewChannelData.channelWithFiveUsers)
}

/**
 * Shows [SocialChannelAvatar] preview for the provided parameters.
 *
 * @param channel The channel used to show the preview.
 */
@Composable
private fun ChannelAvatarPreview(channel: SocialChatChannel) {
    SocialChatAppTheme {
        SocialChannelAvatar(
            channel = channel,
            currentUser = PreviewUserData.user1,
            modifier = Modifier.size(36.dp)
        )
    }
}
