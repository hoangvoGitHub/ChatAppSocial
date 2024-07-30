package com.hoangkotlin.chatappsocial.feature.friend.model

import com.hoangkotlin.chatappsocial.core.data.model.DataResult

sealed class FriendActionState {

    data object Idle : FriendActionState()

    data object Loading : FriendActionState()

    data class Failed(val message: String, val errorCode: DataResult.Error.Code = DataResult.Error.Code.None) : FriendActionState()

    data object Success : FriendActionState()


}

