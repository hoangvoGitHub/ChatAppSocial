package com.hoangkotlin.chatappsocial.core.data.model

data class SignUpForm(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val image: String? = null
) {
}