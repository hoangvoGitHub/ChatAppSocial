package com.hoangkotlin.chatappsocial.feature.auth

sealed interface AuthenticationState {
    data object Idle : AuthenticationState
    data object Loading : AuthenticationState
    data class Error(val message: String = "") : AuthenticationState
    data class Success(val displayName: String = "User") : AuthenticationState
}

sealed interface SignUpState {
    data object Idle : SignUpState
    data object Loading : SignUpState
    data class Error(val message: String = "") : SignUpState
    data class Success(val email: String, val password: String, val displayName: String) :
        SignUpState
}