package com.hoangkotlin.chatappsocial.core.network.utils

import com.hoangkotlin.chatappsocial.core.datastore.SocialPreferencesDataSource
import javax.inject.Inject

interface TokenManager {

    suspend fun token(): String?
}

class DefaultTokenManager @Inject constructor(
    private val dataSource : SocialPreferencesDataSource
) : TokenManager {

//    private var token: String? = null
    override suspend fun token(): String?{
//        if (token != null){
//            return  token
//        }
        return dataSource.token()
    }
}