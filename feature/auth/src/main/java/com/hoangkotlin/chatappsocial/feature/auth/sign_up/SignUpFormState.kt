package com.hoangkotlin.chatappsocial.feature.auth.sign_up

import android.net.Uri

data class SignUpFormState(
    val firstName: String = "",
    val firstNameError: String? = null,
    val lastName: String = "",
    val lastNameError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,
    val currentPage: SignUpPage = SignUpPage.FormFillingPage,
    val selectedImage: Uri? = null,
) {
    private val isFirstNameError: Boolean = firstNameError != null || firstName.isBlank()
    private val isLastNameError: Boolean = lastNameError != null || lastName.isBlank()
    private val isEmailError: Boolean = emailError != null || email.isBlank()
    private val isPasswordError: Boolean = passwordError != null || password.isBlank()
    private val isConfirmPasswordError: Boolean =
        confirmPasswordError != null || confirmPassword.isBlank()
    val isFormValid =
        !isEmailError && !isPasswordError
                && !isFirstNameError && !isLastNameError
                && !isConfirmPasswordError
}

sealed class SignUpPage {
    data object FormFillingPage : SignUpPage()
    data object ImageUploadingPage : SignUpPage()
}


