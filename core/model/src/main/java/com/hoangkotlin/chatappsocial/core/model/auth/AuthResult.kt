package com.hoangkotlin.chatappsocial.core.model.auth

sealed interface AuthResult {
    data object Loading : AuthResult
    data class Failed(val error: String) : AuthResult
    data class Success(val someText: String = "Welcome") : AuthResult
}