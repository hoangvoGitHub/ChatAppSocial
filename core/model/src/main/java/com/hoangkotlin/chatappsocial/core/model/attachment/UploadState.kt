package com.hoangkotlin.chatappsocial.core.model.attachment


/**
 * Represents various states in attachment upload lifecycle.
 */
sealed class UploadState {
    /**
     * Idle state before attachment starts to upload.
     */
    data object Idle : UploadState()

    /**
     * State representing attachment upload progress.
     */
    class InProgress(val bytesUploaded: Long, val totalBytes: Long) : UploadState()

    /**
     * State indicating that the attachment was uploaded successfully
     */
    data object Success : UploadState()

    /**
     * State indicating that the attachment upload failed.
     */
    data class Failed(val message: String) : UploadState()
}
