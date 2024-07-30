package com.hoangkotlin.chatappsocial.core.network.utils

import com.hoangkotlin.chatappsocial.core.network.model.request.PageableRequest
import com.hoangkotlin.chatappsocial.core.network.model.request.SearchResource
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ResourceSerializer : KSerializer<Map<SearchResource, PageableRequest>> {

    private val mapSerializer =
        MapSerializer(SearchResource.serializer(), PageableRequest.serializer())

    override fun deserialize(decoder: Decoder): Map<SearchResource, PageableRequest> {
        return mapSerializer.deserialize(decoder)
    }

    override val descriptor: SerialDescriptor
        get() = mapSerializer.descriptor

    override fun serialize(encoder: Encoder, value: Map<SearchResource, PageableRequest>) {
        mapSerializer.serialize(encoder, value)
    }
}