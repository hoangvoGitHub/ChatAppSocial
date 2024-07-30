package com.hoangkotlin.chatappsocial.feature.auth.sign_in

data class SignInFormState(
    val email: String = "hoang-1387538611@gmail.com",
    val emailError: String? = null,
    val password: String = "0147852369Fa",
    val passwordError: String? = null,

) {
    private val isPasswordError: Boolean = passwordError != null || password.isBlank()
    private val isEmailError: Boolean = emailError != null || email.isBlank()
    val isFormValid = !isEmailError && !isPasswordError

    companion object {
        fun withPrefilledData(
            prefilledEmail: String?,
            prefilledPassword: String?
        ): SignInFormState {
            return SignInFormState(
                email = prefilledEmail ?: "hoang-1387538611@gmail.com",
                password = prefilledPassword ?: "0147852369Fa"
            )
        }
    }
}

