package com.hoangkotlin.chatappsocial.core.ui.previewdatas

import com.hoangkotlin.chatappsocial.core.model.SocialChatUser

object PreviewUserData {
    val user1: SocialChatUser = SocialChatUser(
        id = "jc",
        name = "Jc Miñarro",
        image = "https://ca.slack-edge.com/T02RM6X6B-U011KEXDPB2-891dbb8df64f-128",
    )

    val user2: SocialChatUser = SocialChatUser(
        id = "amit",
        name = "Amit Kumar",
        image = "https://ca.slack-edge.com/T02RM6X6B-U027L4AMGQ3-9ca65ea80b60-128",
    )
    val user3: SocialChatUser = SocialChatUser(
        id = "belal",
        name = "Belal Khan",
        image = "https://ca.slack-edge.com/T02RM6X6B-U02DAP0G2AV-2072330222dc-128",
    )
    val user4: SocialChatUser = SocialChatUser(
        id = "dmitrii",
        name = "Dmitrii Bychkov",
        image = "https://ca.slack-edge.com/T02RM6X6B-U01CDPY6YE8-b74b0739493e-128",
    )
    val user5: SocialChatUser = SocialChatUser(
        id = "filip",
        name = "Filip Babić",
        image = "",
    )

    val me: SocialChatUser = SocialChatUser(
        id = "1c0bf0b3-9067-4ae8-9a89-9455cf750eb2",
        name = "Jaewoong Eum",
        image = "https://ca.slack-edge.com/T02RM6X6B-U02HU1XR9LM-626fb91c334e-128",
    )

    val userWithLongName: SocialChatUser = SocialChatUser(
        id = "1c0bf0b3-9067-4ae8-9a89-9455cf750eb2",
        name = "Jaewoong Eum Jaewoong Eum Jaewoong Eum Jaewoong Eum",
        image = "https://ca.slack-edge.com/T02RM6X6B-U02HU1XR9LM-626fb91c334e-128",
    )

    val users = listOf<SocialChatUser>(
        user1,
        user2,
        user3,
        user4,
        user5,
        me,
    )
}
