package com.hoangkotlin.chatappsocial.core.model.attachment

import com.hoangkotlin.chatappsocial.core.common.constants.ModelType

enum class AttachmentType(
    val modelType: String,
    val displayPriority: Int
) {
    Image(
        ModelType.attach_image,
        IMAGE_DISPLAY_PRIORITY
    ),
    Video(
        ModelType.attach_video,
        VIDEO_DISPLAY_PRIORITY
    ),
    Audio(
        ModelType.attach_audio,
        AUDIO_DISPLAY_PRIORITY
    ),
    File(
        ModelType.attach_file,
        FILE_DISPLAY_PRIORITY
    ),
    Unknown(
        ModelType.attach_unknown,
        UNKNOWN_DISPLAY_PRIORITY
    )
}

private const val IMAGE_DISPLAY_PRIORITY = 1
private const val VIDEO_DISPLAY_PRIORITY = 2
private const val AUDIO_DISPLAY_PRIORITY = 3
private const val FILE_DISPLAY_PRIORITY = 4
private const val UNKNOWN_DISPLAY_PRIORITY = 5


fun String?.toAttachmentType(): AttachmentType {
    return when (this) {
        ModelType.attach_image -> AttachmentType.Image
        ModelType.attach_video -> AttachmentType.Video
        ModelType.attach_audio -> AttachmentType.Audio
        ModelType.attach_file -> AttachmentType.File
        ModelType.attach_unknown -> AttachmentType.Unknown
        else -> AttachmentType.Unknown
    }
}