package com.hoangkotlin.chatappsocial.core.ui.previewdatas

import com.hoangkotlin.chatappsocial.core.common.constants.ModelType
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.model.SocialChatMember
import com.hoangkotlin.chatappsocial.core.model.SocialChatMessage
import com.hoangkotlin.chatappsocial.core.model.attachment.AttachmentType
import com.hoangkotlin.chatappsocial.core.model.attachment.SocialChatAttachment
import com.hoangkotlin.chatappsocial.core.model.attachment.UploadState
import java.util.Date
import kotlin.time.Duration

object PreviewChannelData {

    private val mockAttachments = listOf(
        SocialChatAttachment(
            fileSize = 1000,
            uploadState = UploadState.Success,
            type = AttachmentType.Image,
            mimeType = ModelType.attach_mime_image_jpg
        ),
        SocialChatAttachment(
            fileSize = 2000,
            uploadState = UploadState.InProgress(1000L, 2000L),
            type = AttachmentType.Image,
            mimeType = ModelType.attach_mime_image_jpg
        ),

        SocialChatAttachment(
            fileSize = 2000,
            uploadState = UploadState.Idle,
            type = AttachmentType.Image,
            mimeType = ModelType.attach_mime_image_jpg
        )

    )

    private val mockAttachmentsFailed = listOf(
        SocialChatAttachment(
            fileSize = 1000,
            uploadState = UploadState.Success,
            type = AttachmentType.Image,
            mimeType = ModelType.attach_mime_image_jpg
        ),
        SocialChatAttachment(
            fileSize = 2000,
            uploadState = UploadState.InProgress(1000L, 2000L),
            type = AttachmentType.Image,
            mimeType = ModelType.attach_mime_image_jpg
        ),

        SocialChatAttachment(
            fileSize = 2000,
            uploadState = UploadState.Failed(""),
            type = AttachmentType.Image,
            mimeType = ModelType.attach_mime_image_jpg
        ),
        SocialChatAttachment(
            fileSize = 2000,
            uploadState = UploadState.Idle,
            type = AttachmentType.Image,
            mimeType = ModelType.attach_mime_image_jpg
        )

    )

    private val mockAttachmentsSuccess = listOf(
        SocialChatAttachment(
            fileSize = 1000,
            uploadState = UploadState.Success,
            type = AttachmentType.Image,
            mimeType = ModelType.attach_mime_image_jpg
        ),
        SocialChatAttachment(
            fileSize = 1000,
            uploadState = UploadState.Success,
            type = AttachmentType.Image,
            mimeType = ModelType.attach_mime_image_jpg
        ),
        SocialChatAttachment(
            fileSize = 1000,
            uploadState = UploadState.Success,
            type = AttachmentType.Image,
            mimeType = ModelType.attach_mime_image_jpg
        ),
        SocialChatAttachment(
            fileSize = 1000,
            uploadState = UploadState.Success,
            type = AttachmentType.Image,
            mimeType = ModelType.attach_mime_image_jpg
        ),
    )


    val messages = listOf(
        SocialChatMessage(
            id = "1",
            cid = "channelType:channelId2",
            text = "Hey, how's it going? Im here Hey, how's it going? Hey, how's it going? Hey, how's it going? Hey, how's it going? Hey, how's it going? ",
            createdAt = Date(System.currentTimeMillis() - 20000L),
            updatedAt = Date(),
            user = PreviewUserData.user1
        ),
        SocialChatMessage(
            id = "2",
            cid = "channelType:channelId2",
            text = "Not bad, thanks for asking!Not bad, thanks for asking!Not bad, thanks for asking!Not bad, thanks for asking!Not bad, thanks for asking!Not bad, thanks for asking!",
            createdAt = Date(System.currentTimeMillis() - 19000L),
            updatedAt = Date(),
            user = PreviewUserData.user2,
            replyTo = SocialChatMessage(
                id = "1",
                cid = "channel_1",
                text = "Hey, how's it going?",
                createdAt = Date(),
                updatedAt = Date(),
                user = PreviewUserData.user1
            )
        ),
        SocialChatMessage(
            id = "3",
            cid = "channelType:channelId2",
            text = "Any plans for the weekend?",
            createdAt = Date(System.currentTimeMillis() - 18000),
            updatedAt = Date(),
            user = PreviewUserData.user1,
            attachments = mockAttachmentsSuccess
        ),
        SocialChatMessage(
            id = "4",
            cid = "channelType:channelId2",
            text = "Nothing",
            createdAt = Date(System.currentTimeMillis() - 17000),
            updatedAt = Date(),
            user = PreviewUserData.user2
        ),
        SocialChatMessage(
            id = "5",
            cid = "channelType:channelId2",
            text = "How's the weather today?",
            createdAt = Date(System.currentTimeMillis() - 16000L),
            updatedAt = Date(),
            user = PreviewUserData.user1
        ),
        SocialChatMessage(
            id = "6",
            cid = "channelType:channelId2",
            text = "I'm feeling excited!",
            createdAt = Date(System.currentTimeMillis() - 15000L),
            updatedAt = Date(),
            user = PreviewUserData.user2
        ),
        SocialChatMessage(
            id = "7",
            cid = "channelType:channelId2",
            text = "What's for lunch?",
            createdAt = Date(System.currentTimeMillis() - 14000L),
            updatedAt = Date(),
            user = PreviewUserData.user1
        ),
        SocialChatMessage(
            id = "8",
            cid = "channelType:channelId2",
            text = "Just finished my workout!",
            createdAt = Date(System.currentTimeMillis() - 13000L),
            updatedAt = Date(),
            user = PreviewUserData.user2
        ),

        SocialChatMessage(
            id = "15",
            cid = "channelType:channelId2",
            text = "a",
            createdAt = Date(),
            updatedAt = Date(),
            user = PreviewUserData.user2
        ),
        SocialChatMessage(
            id = "16",
            cid = "channelType:channelId2",
            text = "ab",
            createdAt = Date(),
            updatedAt = Date(),
            user = PreviewUserData.user1
        ),
        SocialChatMessage(
            id = "9",
            cid = "channelType:channelId2",
            text = "Looking forward to the weekenddd!",
            createdAt = Date(System.currentTimeMillis() - 12000L),
            updatedAt = Date(),
            user = PreviewUserData.user1,
            attachments = mockAttachmentsSuccess
        ),
        SocialChatMessage(
            id = "aaa9",
            cid = "channelType:channelId2",
            text = "So sad",
            createdAt = Date(System.currentTimeMillis() - 12000L),
            updatedAt = Date(),
            user = PreviewUserData.user1,
        ),
        SocialChatMessage(
            id = "bbb9",
            cid = "channelType:channelId2",
            text = "Haizzz",
            createdAt = Date(System.currentTimeMillis() - 12000L),
            updatedAt = Date(),
            user = PreviewUserData.user1,
        ),
        SocialChatMessage(
            id = "ccc9",
            cid = "channelType:channelId2",
            text = "Looking forward to the weekenddd!",
            createdAt = Date(System.currentTimeMillis() - 12000L),
            updatedAt = Date(),
            user = PreviewUserData.user1,
        ),
        SocialChatMessage(
            id = "10",
            cid = "channelType:channelId2",
            text = "Has anyone seen my keyssss?",
            createdAt = Date(System.currentTimeMillis() - 11000L),
            updatedAt = Date(),
            user = PreviewUserData.user2
        ),
        SocialChatMessage(
            id = "11",
            cid = "channelType:channelId2",
            text = "Just booked my vacation!",
            createdAt = Date(System.currentTimeMillis() - 10000L),
            updatedAt = Date(),
            user = PreviewUserData.user1
        ),
        SocialChatMessage(
            id = "12",
            cid = "channelType:channelId2",
            text = "Time to relax and unwind.",
            createdAt = Date(),
            updatedAt = Date(),
            user = PreviewUserData.user2
        ),
        SocialChatMessage(
            id = "13",
            cid = "channelType:channelId2",
            text = "Looking for recommendations for a good book.",
            createdAt = Date(),
            updatedAt = Date(),
            user = PreviewUserData.user1
        ),
        SocialChatMessage(
            id = "14",
            cid = "channelType:channelId2",
            text = "Just finished cooking dinner!",
            createdAt = Date(System.currentTimeMillis() - Duration.INFINITE.inWholeMilliseconds),
            updatedAt = Date(),
            user = PreviewUserData.user2
        ),


        )
    val channelWithImage: SocialChatChannel = SocialChatChannel(
        id = "channelType:channelId1asdasdasd",
        image = "https://picsum.photos/id/237/128/128",
        members = listOf(
            SocialChatMember(user = PreviewUserData.user1),
            SocialChatMember(user = PreviewUserData.user2),
            SocialChatMember(user = PreviewUserData.user3),
        )
    )

    val channelWithTwoUsers: SocialChatChannel = SocialChatChannel(
        id = "channelType:channelId2",
        members = listOf(
            SocialChatMember(user = PreviewUserData.user1),
            SocialChatMember(user = PreviewUserData.user2),
        ),
        messages = messages.take(5).sortedBy { it.createdAt }
    )

    val channelWithThreeUsers: SocialChatChannel = SocialChatChannel(
        id = "channelType:threeusers",
        members = listOf(
            SocialChatMember(user = PreviewUserData.user1),
            SocialChatMember(user = PreviewUserData.user2),
            SocialChatMember(user = PreviewUserData.user3),
        ),
        messages = messages.take(5).sortedBy { it.createdAt }
    )

    val channelWithFourUsers: SocialChatChannel = SocialChatChannel(
        id = "channelType:fourusers",
        members = listOf(
            SocialChatMember(user = PreviewUserData.user1),
            SocialChatMember(user = PreviewUserData.user2),
            SocialChatMember(user = PreviewUserData.user3),
            SocialChatMember(user = PreviewUserData.user4),
        ),
        messages = messages.take(5).sortedBy { it.createdAt }
    )

    val channelWithFiveUsers: SocialChatChannel = SocialChatChannel(
        id = "channelType:fiveusers",
        members = listOf(
            SocialChatMember(user = PreviewUserData.user1),
            SocialChatMember(user = PreviewUserData.user2),
            SocialChatMember(user = PreviewUserData.user3),
            SocialChatMember(user = PreviewUserData.user4),
            SocialChatMember(user = PreviewUserData.user5),
        ),
        messages = messages.take(5).sortedBy { it.createdAt }
    )


    val longMessage = SocialChatMessage(
        id = "212asdasdasd3",
        cid = "channelType:channeasdlId2",
        text = "Not bad, thanks for asking! Not bad, thanks for asking! Not bad, thanks for asking! Not bad, thanks for asking! Not bad, thanks for asking! Not bad, thanks for asking! ",
        createdAt = Date(System.currentTimeMillis() - 19000L),
        updatedAt = Date(),
        user = PreviewUserData.me,
    )

    val shortMessage = SocialChatMessage(
        id = "212asdasaaasdsadasd1231dasd3",
        cid = "channelType:channelId2",
        text = "a",
        createdAt = Date(System.currentTimeMillis() - 19000L),
        updatedAt = Date(),
        user = PreviewUserData.me,
    )

    val meReplyMe = SocialChatMessage(
        id = "2123",
        cid = "channelType:channelId2",
        text = "Not bad, thanks for asking! Not bad, thanks for asking! Not bad, thanks for asking! Not bad, thanks for asking! Not bad, thanks for asking! Not bad, thanks for asking! ",
        createdAt = Date(System.currentTimeMillis() - 19000L),
        updatedAt = Date(),
        user = PreviewUserData.me,
        replyTo = SocialChatMessage(
            id = "1",
            cid = "channel_1",
            text = "Hey, how's it going?",
            createdAt = Date(),
            updatedAt = Date(),
            user = PreviewUserData.me
        )
    )

    val meReplyOther = SocialChatMessage(
        id = "asdasd2",
        cid = "channelType:channelId2",
        text = "Not bad, thanks for asking!",
        createdAt = Date(System.currentTimeMillis() - 19000L),
        updatedAt = Date(),
        user = PreviewUserData.me,
        replyTo = SocialChatMessage(
            id = "1",
            cid = "channel_1",
            text = "Hey, how's it going? Hey, how's it going? Hey, how's it going? Hey, how's it going? Hey, how's it going? Hey, how's it going? Hey, how's it going? ",
            createdAt = Date(),
            updatedAt = Date(),
            user = PreviewUserData.user1
        )
    )

    val otherReplyOther = SocialChatMessage(
        id = "2asdasdasd",
        cid = "channelType:channelId2",
        text = "Not bad, thanks for asking!",
        createdAt = Date(System.currentTimeMillis() - 19000L),
        updatedAt = Date(),
        user = PreviewUserData.user2,
        replyTo = SocialChatMessage(
            id = "1",
            cid = "channel_1",
            text = "Hey, how's it going?",
            createdAt = Date(),
            updatedAt = Date(),
            user = PreviewUserData.user2
        )
    )

    val otherReplyMe = SocialChatMessage(
        id = "zzzznn2",
        cid = "channelType:channelId2",
        text = "Not bad, thanks for asking!",
        createdAt = Date(System.currentTimeMillis() - 19000L),
        updatedAt = Date(),
        user = PreviewUserData.user2,
        replyTo = SocialChatMessage(
            id = "1",
            cid = "channel_1",
            text = "Hey, how's it going?",
            createdAt = Date(),
            updatedAt = Date(),
            user = PreviewUserData.me
        )
    )

    val messageWithAttachmentsSuccess = SocialChatMessage(
        id = "zzzznn2",
        cid = "channelType:channelId2:with_attachments_inprogress",
        text = "Not bad, thanks for asking!",
        createdAt = Date(System.currentTimeMillis() - 19000L),
        updatedAt = Date(),
        user = PreviewUserData.user2,
        attachments = mockAttachmentsSuccess
    )

    val messageWithAttachmentsInProgress = SocialChatMessage(
        id = "zzzznn2",
        cid = "channelType:channelId2:with_attachments_inprogress",
        text = "Not bad, thanks for asking!",
        createdAt = Date(System.currentTimeMillis() - 19000L),
        updatedAt = Date(),
        user = PreviewUserData.user2,
        attachments = mockAttachments
    )

    val messageWithAttachmentsFailed = SocialChatMessage(
        id = "zzzznn2",
        cid = "channelType:channelId2:with_attachments_inprogress",
        text = "Not bad, thanks for asking!",
        createdAt = Date(System.currentTimeMillis() - 19000L),
        updatedAt = Date(),
        user = PreviewUserData.user2,
        attachments = mockAttachmentsFailed
    )


}