/*
 * Copyright (c) 2014-2022 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-chat-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hoangkotlin.chatappsocial.core.chat_client.utils


/**
 * Sealed class represents possible cause of disconnection.
 */
sealed class DisconnectCause {

    /**
     * Happens when networks is not available anymore.
     */
    data object NetworkNotAvailable : DisconnectCause()

    /**
     * Happens when Web Socket connection is not available.
     */
    data object WebSocketNotAvailable : DisconnectCause()

    /**
     * Happens when some non critical error occurs.
     * @param error Instance of [ChatNetworkError] as a reason of it.
     */
    class Error(val error: String?) : DisconnectCause()

    /**
     * Happens when a critical error occurs. Connection can't be restored after such disconnection.
     * @param error Instance of [ChatNetworkError] as a reason of it.
     */
    class UnrecoverableError(public val error: String?) : DisconnectCause()

    /**
     * Happens when disconnection has been done intentionally. E.g. we release connection when app went to background
     * or when the user logout.
     */
    data object ConnectionReleased : DisconnectCause()
}
