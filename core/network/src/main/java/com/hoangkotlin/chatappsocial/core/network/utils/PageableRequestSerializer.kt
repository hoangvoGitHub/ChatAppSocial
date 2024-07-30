package com.hoangkotlin.chatappsocial.core.network.utils

import com.hoangkotlin.chatappsocial.core.network.model.request.PageableRequest
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.properties.Delegates

/**
 * Custom [KSerializer] for class [PageableRequest]
 * The normal [Serializable] annotation is used in conjunction with a custom [KSerializer] to ensure proper
 * encoding and decoding of this class. This is necessary because the default serialization does not
 * encode the class properly in some cases for an unknown reason.
 */
object PageableRequestSerializer : KSerializer<PageableRequest> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("PageableRequest") {
        element<Int>("limit")
        element<Int>("offset")
    }

    override fun serialize(encoder: Encoder, value: PageableRequest) {
        val compositeOutput = encoder.beginStructure(descriptor)
        compositeOutput.encodeIntElement(descriptor, 0, value.limit)
        compositeOutput.encodeIntElement(descriptor, 1, value.offset)
        compositeOutput.endStructure(descriptor)
    }

    override fun deserialize(decoder: Decoder): PageableRequest {
        val compositeInput = decoder.beginStructure(descriptor)
        var limit by Delegates.notNull<Int>()
        var offset by Delegates.notNull<Int>()
        loop@ while (true) {
            when (val index = compositeInput.decodeElementIndex(descriptor)) {
                CompositeDecoder.DECODE_DONE -> break@loop
                0 -> limit = compositeInput.decodeIntElement(descriptor, index)
                1 -> offset = compositeInput.decodeIntElement(descriptor, index)
                else -> throw SerializationException("Unknown index $index")
            }
        }
        compositeInput.endStructure(descriptor)
        return PageableRequest(limit, offset)
    }

}