package com.hoangkotlin.chatappsocial.core.data.repository.auth

import com.hoangkotlin.chatappsocial.core.data.model.DataResult
import com.hoangkotlin.chatappsocial.core.data.model.SignUpForm
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun signIn(username: String, password: String, isRemembered: Boolean): Flow<DataResult<Unit>>

    fun signUp(registerForm: SignUpForm): Flow<DataResult<Unit>>
}