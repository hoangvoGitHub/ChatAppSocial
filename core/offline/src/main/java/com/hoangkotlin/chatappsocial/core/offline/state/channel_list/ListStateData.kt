package com.hoangkotlin.chatappsocial.core.offline.state.channel_list

sealed class ListStateData<out T> {

    data object NoQueryActive : ListStateData<Nothing>()

    data object Loading : ListStateData<Nothing>()

    data object OfflineNoData : ListStateData<Nothing>()

    data class Result<T>(val items: List<T>) : ListStateData<T>()
}