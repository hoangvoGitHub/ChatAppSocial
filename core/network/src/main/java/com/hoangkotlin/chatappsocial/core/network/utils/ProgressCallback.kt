package com.hoangkotlin.chatappsocial.core.network.utils

/**
 * ProgressCallback Interface
 *
 * Provides a mechanism to receive updates during a file upload process. This interface
 * includes methods to handle success, error, and progress events.
 * This interface is designed for listening to file upload status, with acknowledgment
 * from [https://getstream.io/blog/android-upload-progress].
 */
interface ProgressCallback {

    /**
     * Called when the upload completes successfully.
     *
     * @param url The URL of the uploaded resource. This can be null if the URL is not applicable or available.
     */
    fun onSuccess(url: String)

    /**
     * Called when an error occurs during the upload process.
     *
     * @param error An instance of Throwable representing the error that occurred.
     */
    fun onError(error: Throwable)

    /**
     * Called periodically to indicate the progress of the upload.
     *
     * @param bytesUploaded The number of bytes that have been uploaded so far.
     * @param totalBytes The total number of bytes to be uploaded.
     */
    fun onProgress(bytesUploaded: Long, totalBytes: Long)
}