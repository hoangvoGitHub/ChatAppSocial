package com.hoangkotlin.chatappsocial.core.data.repository.app

import com.hoangkotlin.chatappsocial.core.data.repository.app.AppDataRepository
import com.hoangkotlin.chatappsocial.core.model.AppUserData
import com.hoangkotlin.chatappsocial.core.model.ChatAppUser
import com.hoangkotlin.chatappsocial.core.datastore.SocialPreferencesDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultAppDataRepository @Inject constructor(
    private val appDataSource: SocialPreferencesDataSource
) : AppDataRepository {

    override val appUserData: Flow<AppUserData> = appDataSource.appUserData

    override suspend fun signOut() {
        appDataSource.signOut()
    }

    override suspend fun signIn(chatAppUser: ChatAppUser) {
        appDataSource.addUser(chatAppUser)
    }

}