package com.hoangkotlin.chatappsocial.core.data.repository.auth

import com.hoangkotlin.chatappsocial.core.data.model.DataResult
import com.hoangkotlin.chatappsocial.core.data.model.SignUpForm
import com.hoangkotlin.chatappsocial.core.network.api.AuthenticationApi
import com.hoangkotlin.chatappsocial.core.network.model.request.SignInRequest
import com.hoangkotlin.chatappsocial.core.network.model.request.SignUpRequest
import com.hoangkotlin.chatappsocial.core.datastore.SocialPreferencesDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SocialAuthRepository @Inject constructor(
    private val localDataSource: SocialPreferencesDataSource,
    private val authApi: AuthenticationApi
) : AuthRepository {

    override fun signIn(
        username: String,
        password: String,
        isRemembered: Boolean
    ): Flow<DataResult<Unit>> = flow {
        try {
            val response = authApi.signIn(SignInRequest(username, password))
            if (response.isSuccessful && response.body() != null) {
                localDataSource.signInWith(
                    username = response.body()!!.username,
                    password = password,
                    token = response.body()!!.token,
                    isRemembered = isRemembered
                )
                emit(DataResult.Success.Network(Unit))
            } else {
                emit(DataResult.Error(response.message() + response.code()))
            }
        } catch (e: Exception) {
            emit(DataResult.Error(e.message.toString()))
        }
    }

    override fun signUp(registerForm: SignUpForm): Flow<DataResult<Unit>> =
        flow {
            try {
                val response = authApi.signUp(registerForm.asSignUpRequest())
                if (response.isSuccessful && response.body() != null) {
//                    localDataSource.signInWith(
//                        username = response.body()!!.username,
//                        password = registerForm.password,
//                        token = response.body()!!.token
//                    )
                    emit(DataResult.Success.Network(Unit))
                } else {
                    emit(DataResult.Error(response.message()))
                }
            } catch (e: Exception) {
                emit(DataResult.Error(e.message.toString()))
            }
        }
}

fun SignUpForm.asSignUpRequest(): SignUpRequest {
    return SignUpRequest(
        username = email,
        password = password,
        firstName = firstName,
        lastName = lastName
    )
}