package com.hoangkotlin.chatappsocial.feature.media_viewer

import com.hoangkotlin.chatappsocial.core.model.attachment.SocialChatAttachment
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.util.Date

class SearchAttachmentIndexTest {
    private val attachments = listOf(
        SocialChatAttachment("0", createdAt = Date.from(Instant.ofEpochMilli(1100000))),
        SocialChatAttachment("1", createdAt = Date.from(Instant.ofEpochMilli(1100000))),
        SocialChatAttachment("2", createdAt = Date.from(Instant.ofEpochMilli(1200000))),
        SocialChatAttachment("3", createdAt = Date.from(Instant.ofEpochMilli(1300000))),
        SocialChatAttachment("4", createdAt = Date.from(Instant.ofEpochMilli(1300000))),
        SocialChatAttachment("5", createdAt = Date.from(Instant.ofEpochMilli(1300000))),
        SocialChatAttachment("6", createdAt = Date.from(Instant.ofEpochMilli(1500000))),
        SocialChatAttachment("7", createdAt = Date.from(Instant.ofEpochMilli(1600000))),
        SocialChatAttachment("8", createdAt = Date.from(Instant.ofEpochMilli(1700000))),
        SocialChatAttachment("9", createdAt = Date.from(Instant.ofEpochMilli(1800000))),


        )
    private val attachmentToSearch =
        SocialChatAttachment("4", createdAt = Date.from(Instant.ofEpochMilli(1300000)))

    @Test
    fun testFindRange() {
        val expectedIndexRange = Pair(3,5)
        val pivotIndex =
            attachments.binarySearch(comparison = { it.createdAt!!.compareTo(attachmentToSearch.createdAt) })
        val actualRange = findRangeDate(pivotIndex, attachments)
        assertEquals(expectedIndexRange, actualRange)

    }

    @Test
    fun testFindIndex() {
        val attachments = listOf(
            SocialChatAttachment(imageUrl = "0", createdAt = Date.from(Instant.ofEpochMilli(1100000))),
            SocialChatAttachment(imageUrl = "1", createdAt = Date.from(Instant.ofEpochMilli(1100000))),
            SocialChatAttachment(imageUrl = "2", createdAt = Date.from(Instant.ofEpochMilli(1200000))),
            SocialChatAttachment(imageUrl = "3", createdAt = Date.from(Instant.ofEpochMilli(1300000))),
            SocialChatAttachment(imageUrl = "4", createdAt = Date.from(Instant.ofEpochMilli(1300000))),
            SocialChatAttachment(imageUrl = "5", createdAt = Date.from(Instant.ofEpochMilli(1300000))),
            SocialChatAttachment(imageUrl = "6", createdAt = Date.from(Instant.ofEpochMilli(1500000))),
            SocialChatAttachment(imageUrl = "7", createdAt = Date.from(Instant.ofEpochMilli(1600000))),
            SocialChatAttachment(imageUrl = "8", createdAt = Date.from(Instant.ofEpochMilli(1700000))),
            SocialChatAttachment(imageUrl = "9", createdAt = Date.from(Instant.ofEpochMilli(1800000))),


            )
        val attachmentToSearch =
            SocialChatAttachment(imageUrl = "4", createdAt = Date.from(Instant.ofEpochMilli(1300000)))

        val expectedIndex = 4
        val actualIndex = searchForIndex(attachments, attachmentToSearch)
        assertEquals(expectedIndex, actualIndex)

    }


}