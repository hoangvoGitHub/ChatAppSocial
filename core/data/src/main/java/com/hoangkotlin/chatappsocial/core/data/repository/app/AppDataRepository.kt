package com.hoangkotlin.chatappsocial.core.data.repository.app

import com.hoangkotlin.chatappsocial.core.model.AppUserData
import com.hoangkotlin.chatappsocial.core.model.ChatAppUser
import kotlinx.coroutines.flow.Flow

interface AppDataRepository {

    val appUserData: Flow<AppUserData>

    suspend fun signOut()

    suspend fun signIn(chatAppUser: ChatAppUser)

}