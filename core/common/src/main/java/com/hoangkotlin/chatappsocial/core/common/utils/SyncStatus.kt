package com.hoangkotlin.chatappsocial.core.common.utils

private const val SYNC_NEEDED_STATUS_CODE = -1
private const val COMPLETED_STATUS_CODE = 1
private const val FAILED_PERMANENTLY_STATUS_CODE = 2
private const val IN_PROGRESS_STATUS_CODE = 3
private const val AWAITING_ATTACHMENTS_STATUS_CODE = 4
enum class SyncStatus(val status: Int) {
    /**
     * When the entity is new or changed.
     */
    SYNC_NEEDED(SYNC_NEEDED_STATUS_CODE),

    /**
     * When the entity has been successfully synced.
     */
    COMPLETED(COMPLETED_STATUS_CODE),

    /**
     * After the retry strategy we still failed to sync this.
     */
    FAILED_PERMANENTLY(FAILED_PERMANENTLY_STATUS_CODE),

    /**
     * When sync is in progress.
     */
    IN_PROGRESS(IN_PROGRESS_STATUS_CODE),

    /**
     * When message waits its' attachments to be sent.
     *
     * Notes:
     * SyncStatus is not a Message related only to store Attachment status here.
     * SyncStatus is also a property of Channel and Reaction.
     * AWAITING_ATTACHMENTS should be replace with MessageSyncType.IN_PROGRESS_AWAIT_ATTACHMENTS
     */
    AWAITING_ATTACHMENTS(AWAITING_ATTACHMENTS_STATUS_CODE);

    companion object {
        private val map = entries.associateBy(SyncStatus::status)
        public fun fromInt(type: Int): SyncStatus? = map[type]
    }
}