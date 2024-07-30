package com.hoangkotlin.feature.chat.model

sealed class AttachmentUploadPosition {
    /**
     * Upload that is the first message in the group at the top.
     */
    data object Top : AttachmentUploadPosition()

    /**
     * Upload that has another message both at the top and bottom of it.
     */
    data object Middle : AttachmentUploadPosition()

    /**
     * Upload that's the last message in the group, at the bottom.
     */
    data object Bottom : AttachmentUploadPosition()

    /**
     * Upload that is not in a group.
     */
    data object None : AttachmentUploadPosition()
}