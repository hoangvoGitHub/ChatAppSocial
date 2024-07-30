package com.hoangkotlin.chatappsocial.core.offline.state

import com.hoangkotlin.chatappsocial.core.chat_client.client.ClientState
import com.hoangkotlin.chatappsocial.core.model.ChannelMute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface GeneralState {

    val channelMutes: StateFlow<List<ChannelMute>>

    fun clearState()

}


class MutableGeneralState(
    private val clientState: ClientState
) : GeneralState {
    private val _channelMutes = MutableStateFlow<List<ChannelMute>>(emptyList())

    override val channelMutes: StateFlow<List<ChannelMute>>
        get() = _channelMutes

    fun setChannelMutes(mutes: List<ChannelMute>) {
        _channelMutes.value = mutes
    }

    override fun clearState() {
        _channelMutes.value = emptyList()
    }

    companion object {
        @Volatile
        public var instance: MutableGeneralState? = null

        /**
         * Gets the singleton of [MutableGeneralState] or creates it in the first call.
         */
        fun getInstance(clientState: ClientState): MutableGeneralState {
            return instance ?: synchronized(this) {
                instance ?: MutableGeneralState(clientState).also { globalState ->
                    instance = globalState
                }
            }
        }
    }
}

fun GeneralState.asMutableState(): MutableGeneralState? {
    return this as? MutableGeneralState
}