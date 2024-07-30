package com.hoangkotlin.chatappsocial.feature.friend.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hoangkotlin.chatappsocial.core.model.FriendStatus
import com.hoangkotlin.chatappsocial.core.model.SocialChatFriend
import com.hoangkotlin.chatappsocial.core.ui.FriendPossibleAction
import com.hoangkotlin.chatappsocial.core.ui.components.image.SocialUserAvatar
import com.hoangkotlin.chatappsocial.core.ui.previewdatas.PreviewUserData
import com.hoangkotlin.chatappsocial.core.ui.theme.SocialChatAppTheme
import com.hoangkotlin.chatappsocial.feature.friend.FriendActionButton
import com.hoangkotlin.chatappsocial.feature.friend.model.ChatFriendItemState
import com.hoangkotlin.chatappsocial.feature.friend.model.FriendActionState

@Composable
fun FriendItemContainer(
    modifier: Modifier = Modifier,
    friendItemState: ChatFriendItemState,
    onAction: (friendId: String, action: FriendPossibleAction) -> Unit,
    onFriendClick: (ChatFriendItemState) -> Unit
) {
    Row(
        modifier = modifier
            .clickable {
                onFriendClick(friendItemState)
            }
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SocialUserAvatar(
            user = friendItemState.chatFriend.user,
            modifier = Modifier
                .padding(8.dp)
                .size(65.dp),
            showOnlineIndicator = false

        )
        Box(modifier = Modifier
            .padding(start = 8.dp)
            .weight(5f)) {
            Text(
                text = friendItemState.chatFriend.user.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelLarge,
            )
        }

        Column(
            modifier = Modifier.weight(3f),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            friendItemState.actionType.entries.forEach { entry ->
                FriendActionButton(
                    modifier = modifier,
                    action = entry.key,
                    state = entry.value,
                    onClick = { action ->
                        if (!friendItemState.actionType.containsValue(FriendActionState.Loading)) {
                            onAction(friendItemState.chatFriend.user.id, action)
                        }

                    }
                )
            }
//            }
        }


    }

}

@Composable
fun FriendItemContainerPreview(friendItemState: ChatFriendItemState) {
    SocialChatAppTheme {
        FriendItemContainer(
            friendItemState = friendItemState,
            onAction = { _, _ -> },
            onFriendClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FriendItemContainerInFriendListActionIdlePreview() {
    val friend = SocialChatFriend(
        user = PreviewUserData.userWithLongName,
        status = FriendStatus.FRIEND
    )

    FriendItemContainerPreview(
        friendItemState = ChatFriendItemState(
            chatFriend = friend,
            actionType = mapOf(
                FriendPossibleAction.AcceptFriend to FriendActionState.Idle
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
fun FriendItemContainerInFriendListActionSuccessPreview() {
    val friend = SocialChatFriend(
        user = PreviewUserData.userWithLongName,
        status = FriendStatus.FRIEND
    )

    FriendItemContainerPreview(
        friendItemState = ChatFriendItemState(
            chatFriend = friend,
            actionType = mapOf(
                FriendPossibleAction.AcceptFriend to FriendActionState.Success
            )
        )
    )
}


@Preview(showBackground = true)
@Composable
fun FriendItemContainerInFriendListLoadingRemovePreview() {
    val friend = SocialChatFriend(
        user = PreviewUserData.userWithLongName,
        status = FriendStatus.FRIEND
    )

    FriendItemContainerPreview(
        friendItemState = ChatFriendItemState(
            chatFriend = friend,
            actionType = mapOf(
                FriendPossibleAction.RemoveFriend to FriendActionState.Loading
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
fun FriendItemContainerInFriendListRemoveFailedPreview() {
    val friend = SocialChatFriend(
        user = PreviewUserData.userWithLongName,
        status = FriendStatus.FRIEND
    )

    FriendItemContainerPreview(
        friendItemState = ChatFriendItemState(
            chatFriend = friend,
            actionType = mapOf(
                FriendPossibleAction.RemoveFriend to FriendActionState.Failed("Unknown Error")
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
fun FriendItemContainerInFriendRequestsPreview() {
    val friend = SocialChatFriend(
        user = PreviewUserData.userWithLongName,
        status = FriendStatus.REQUEST_FROM_OTHER
    )

    FriendItemContainerPreview(
        friendItemState = ChatFriendItemState(
            chatFriend = friend,
            actionType = mapOf(
                FriendPossibleAction.AcceptFriend to FriendActionState.Idle,
                FriendPossibleAction.RejectFriend to FriendActionState.Idle,
            )
        )
    )
}