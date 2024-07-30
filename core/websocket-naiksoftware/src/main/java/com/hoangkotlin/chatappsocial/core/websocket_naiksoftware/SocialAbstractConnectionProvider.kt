package com.hoangkotlin.chatappsocial.core.websocket_naiksoftware

import android.annotation.SuppressLint
import android.util.Log
import io.reactivex.Completable
import io.reactivex.CompletableEmitter
import io.reactivex.subjects.CompletableSubject
import ua.naiksoftware.stomp.provider.AbstractConnectionProvider
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

abstract class SocialAbstractConnectionProvider :
    AbstractConnectionProvider() {

    private val receiptAckMap = ConcurrentHashMap<String, CompletableSubject>()


    @SuppressLint("CheckResult")
    open fun send(stompMessage: String, receiptId: String? = null): Completable {
        if (receiptId == null) return send(stompMessage)
        val completableSubject = CompletableSubject.create()
        receiptAckMap[receiptId] = completableSubject
        return Completable.create { emitter: CompletableEmitter ->
            if (socket == null) {
                receiptAckMap.remove(
                    receiptId
                )
                emitter.onError(IllegalStateException("Not connected"))
            } else {


                Log.d(
                    TAG,
                    "Send STOMP message with receipt: $stompMessage, receiptId: $receiptId"
                )
                // Include the receipt header in the message
                // Include the receipt header in the message
                rawSend(stompMessage)

                // Subscribe to the completableSubject to complete or error out

                // Subscribe to the completableSubject to complete or error out
                completableSubject.subscribe(emitter::onComplete, emitter::onError)
            }
        }.andThen(receiptAckMap[receiptId]!!.timeout(30, TimeUnit.SECONDS)?.doOnTerminate {
            receiptAckMap.remove(
                receiptId
            )
        })
    }

    open fun handleReceiptAck(stompMessage: String) {
        Log.d(TAG, "handleReceiptAck: $stompMessage")
        // Handle receipt acknowledgment
        if (stompMessage.startsWith("RECEIPT")) {
            val receiptId = parseReceiptId(stompMessage)
            if (receiptId != null && receiptAckMap.containsKey(receiptId)) {
                // Complete the Completable associated with the receipt ID
                receiptAckMap[receiptId]?.onComplete()

            }
        }
    }

    // New: Helper method to parse the receipt ID from a STOMP message
    private fun parseReceiptId(stompMessage: String): String? {
        // Parse the receipt ID from the STOMP message
        val lines = stompMessage.split("\n".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        for (line in lines) {
            if (line.startsWith("receipt-id:")) {
                return line.substring("receipt-id:".length).trim { it <= ' ' }
            }
        }
        return null
    }

    companion object {
        private const val TAG = "SocialAbstractConnection"
    }


}