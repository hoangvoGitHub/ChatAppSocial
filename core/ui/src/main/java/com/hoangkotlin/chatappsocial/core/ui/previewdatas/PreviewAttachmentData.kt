package com.hoangkotlin.chatappsocial.core.ui.previewdatas

import com.hoangkotlin.chatappsocial.core.model.attachment.AttachmentType
import com.hoangkotlin.chatappsocial.core.model.attachment.SocialChatAttachment

object PreviewAttachmentData {

    val singleImage = SocialChatAttachment(
        type = AttachmentType.Image,
        thumbnailUrl = "https://plus.unsplash.com/premium_photo-1683121366070-5ceb7e007a97?q=80&w=1770&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
    )

    val singleFile = SocialChatAttachment(
        name = "votranhoang.pdf",
        type = AttachmentType.File,
        fileSize = 10000
    )

    val mixedAttachments = listOf(
        SocialChatAttachment(
            type = AttachmentType.Image,
            thumbnailUrl = "https://plus.unsplash.com/premium_photo-1683121366070-5ceb7e007a97?q=80&w=1770&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
        ),
        SocialChatAttachment(
            type = AttachmentType.Image,
            thumbnailUrl = "https://plus.unsplash.com/premium_photo-1683121366070-5ceb7e007a97?q=80&w=1770&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
        ),
        SocialChatAttachment(
            type = AttachmentType.Image,
            thumbnailUrl = "https://plus.unsplash.com/premium_photo-1683121366070-5ceb7e007a97?q=80&w=1770&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
        ),
        singleImage,
        singleFile,
        SocialChatAttachment(
            type = AttachmentType.Audio,
            fileSize = 1000
        ),
    )

    val imageAttachments = listOf(
        SocialChatAttachment(
            type = AttachmentType.Image,
            thumbnailUrl = "https://plus.unsplash.com/premium_photo-1683121366070-5ceb7e007a97?q=80&w=1770&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
        ),
        SocialChatAttachment(
            type = AttachmentType.Image,
            thumbnailUrl = "https://plus.unsplash.com/premium_photo-1683121366070-5ceb7e007a97?q=80&w=1770&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
        ),
        SocialChatAttachment(
            type = AttachmentType.Image,
            thumbnailUrl = "https://plus.unsplash.com/premium_photo-1683121366070-5ceb7e007a97?q=80&w=1770&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
        ),
        SocialChatAttachment(
            type = AttachmentType.Image,
            thumbnailUrl = "https://plus.unsplash.com/premium_photo-1683121366070-5ceb7e007a97?q=80&w=1770&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
        ),
        SocialChatAttachment(
            type = AttachmentType.Image,
            thumbnailUrl = "https://plus.unsplash.com/premium_photo-1683121366070-5ceb7e007a97?q=80&w=1770&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
        ),
        SocialChatAttachment(
            type = AttachmentType.Image,
            thumbnailUrl = "https://plus.unsplash.com/premium_photo-1683121366070-5ceb7e007a97?q=80&w=1770&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
        ),

        )

    val fileAttachments = listOf<SocialChatAttachment>(
        SocialChatAttachment(
            name = "votranhoang.pdf",
            type = AttachmentType.File
        ),
        SocialChatAttachment(
            name = "votranhoang.pdf",
            type = AttachmentType.File
        ),
        SocialChatAttachment(
            name = "votranhoang.pdf",
            type = AttachmentType.File
        ),

        )
}