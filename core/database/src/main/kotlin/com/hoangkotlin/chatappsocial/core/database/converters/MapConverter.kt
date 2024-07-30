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

package com.hoangkotlin.chatappsocial.core.database.converters

import androidx.room.TypeConverter
import com.hoangkotlin.chatappsocial.core.database.model.ChatMemberEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

class MapConverter {

    private val json = Json {
        serializersModule = SerializersModule {
            contextual(ChatMemberEntity::class, ChatMemberEntity.serializer())
        }
    }

    @TypeConverter
    fun memberMapToString(someObjects: Map<String, ChatMemberEntity>): String {
        return json.encodeToString(someObjects)
    }

    @TypeConverter
    fun stringToMemberMap(data: String?): Map<String, ChatMemberEntity>? {
        if (data.isNullOrEmpty() || data == "null") {
            return emptyMap()
        }
        return json.decodeFromString(data)
    }


}
