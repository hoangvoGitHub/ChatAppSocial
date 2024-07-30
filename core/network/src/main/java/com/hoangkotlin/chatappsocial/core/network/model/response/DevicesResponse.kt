package com.hoangkotlin.chatappsocial.core.network.model.response

import com.hoangkotlin.chatappsocial.core.network.model.dto.DeviceDto
import kotlinx.serialization.Serializable

@Serializable
data class DevicesResponse(
    val devices: List<DeviceDto>
) {

}
