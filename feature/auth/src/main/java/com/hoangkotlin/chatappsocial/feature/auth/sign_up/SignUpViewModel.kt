package com.hoangkotlin.chatappsocial.feature.auth.sign_up

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hoangkotlin.chatappsocial.core.data.model.DataResult
import com.hoangkotlin.chatappsocial.core.data.model.SignUpForm
import com.hoangkotlin.chatappsocial.core.data.repository.auth.AuthRepository
import com.hoangkotlin.chatappsocial.feature.auth.SignUpState
import com.hoangkotlin.chatappsocial.feature.auth.emailValidate
import com.hoangkotlin.chatappsocial.feature.auth.passwordValidate
import com.hoangkotlin.chatappsocial.feature.auth.validateName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {


    private val _signUpFormState = MutableStateFlow(SignUpFormState())
    val signUpFormState = _signUpFormState.asStateFlow()

    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)
    val signUpState = _signUpState.asStateFlow()

    private var signUpJob: Job? = null

    fun onFormAction(action: SignUpFormAction) {
        when (action) {
            is SignUpFormAction.ConfirmPasswordFieldChange -> onConfirmPasswordChanged(action.value)
            is SignUpFormAction.EmailFieldChange -> onEmailChanged(action.value)
            is SignUpFormAction.FirstNameFieldChange -> onFirstNameChanged(action.value)
            is SignUpFormAction.LastNameFieldChange -> onLastNameChanged(action.value)
            is SignUpFormAction.PasswordFieldChange -> onPasswordChanged(action.value)
        }
    }

    private fun onFirstNameChanged(firstName: String) {
        _signUpFormState.update {
            it.copy(
                firstName = firstName,
                firstNameError = firstName.validateName()
            )
        }
    }

    private fun onLastNameChanged(lastName: String) {
        _signUpFormState.update {
            it.copy(
                lastName = lastName,
                lastNameError = lastName.validateName()
            )
        }
    }

    fun onEmailChanged(email: String) {
        _signUpFormState.update {
            it.copy(
                email = email,
                emailError = email.emailValidate()
            )
        }
    }

    private fun onPasswordChanged(password: String) {
        _signUpFormState.update {
            it.copy(
                password = password,
                passwordError = password.passwordValidate(),
                confirmPasswordError = it.confirmPassword.passwordValidate(it.password)
            )
        }
    }

    private fun onConfirmPasswordChanged(confirmPassword: String) {
        _signUpFormState.update {
            it.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = confirmPassword.passwordValidate(it.password)
            )
        }
    }

    fun signUp() {
        signUpJob?.cancel()
        signUpJob = viewModelScope.launch {
            if (_signUpFormState.value.isFormValid) {
                val signUpForm = _signUpFormState.value.asSignUpForm();
                _signUpState.value = SignUpState.Loading

                authRepository.signUp(signUpForm).collect {
                    when (val result = it) {
                        is DataResult.Error -> {
                            _signUpState.value =
                                SignUpState.Error(message = result.errorMessage)
                        }


                        is DataResult.Success -> {
                            _signUpState.value = SignUpState.Success(
                                email = signUpForm.email,
                                password = signUpForm.password,
                                displayName = "User"
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        signUpJob?.cancel()
    }

    fun onBackToPreviousProgress() {
        _signUpFormState.update { currentState ->
            if (currentState.currentPage is SignUpPage.ImageUploadingPage) {
                currentState.copy(currentPage = SignUpPage.FormFillingPage)
            } else {
                currentState
            }
        }
    }

    fun onGoToNextProgress() {
        _signUpFormState.update { currentState ->
            if (currentState.currentPage is SignUpPage.FormFillingPage) {
                currentState.copy(currentPage = SignUpPage.ImageUploadingPage)
            } else {
                currentState
            }
        }
    }

    fun onImageSelected(uri: Uri?) {
        _signUpFormState.update { currentState ->
                currentState.copy(selectedImage = uri)

        }
    }


}

data class SignUpProgressState(
    val currentStep: SignUpProgress = SignUpProgress.FillingFormStep()
)

sealed class SignUpProgress {
    abstract val stepIndex: Int

    data class FillingFormStep(

        val canNavigateToNextStep: Boolean = false
    ) : SignUpProgress() {
        override val stepIndex: Int
            get() = 1
    }


    data class UploadingProfileStep(
        val canNavigateToPreviousStep: Boolean = false
    ) : SignUpProgress() {
        override val stepIndex: Int
            get() = 2
    }
}

sealed class SignUpFormAction {
    abstract val value: String

    data class FirstNameFieldChange(override val value: String) : SignUpFormAction()
    data class LastNameFieldChange(override val value: String) : SignUpFormAction()
    data class EmailFieldChange(override val value: String) : SignUpFormAction()
    data class PasswordFieldChange(override val value: String) : SignUpFormAction()
    data class ConfirmPasswordFieldChange(override val value: String) : SignUpFormAction()

    companion object {
        const val clearedValue: String = ""
    }
}

fun SignUpFormState.asSignUpForm(): SignUpForm {
    return SignUpForm(
        email = this.email,
        password = this.password,
        firstName = this.firstName,
        lastName = this.lastName
    )
}