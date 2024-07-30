package com.hoangkotlin.chatappsocial.feature.auth.sign_in

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hoangkotlin.chatappsocial.core.data.model.DataResult
import com.hoangkotlin.chatappsocial.core.data.repository.auth.AuthRepository
import com.hoangkotlin.chatappsocial.feature.auth.AuthenticationState
import com.hoangkotlin.chatappsocial.feature.auth.emailValidate
import com.hoangkotlin.chatappsocial.feature.auth.navigation.emailArg
import com.hoangkotlin.chatappsocial.feature.auth.navigation.passwordArg
import com.hoangkotlin.chatappsocial.feature.auth.passwordValidate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val prefilledEmail: String? = savedStateHandle.get<String>(emailArg)
    private val prefilledPassword: String? = savedStateHandle.get<String>(passwordArg)
    private var isRememberCredentials: Boolean = false
    private val _signInFormState = MutableStateFlow(
        SignInFormState.withPrefilledData(
            prefilledEmail, prefilledPassword
        )
    )
    val signInFormState = _signInFormState.asStateFlow()

    private val _signInState = MutableStateFlow<AuthenticationState>(AuthenticationState.Idle)
    val signInState = _signInState.asStateFlow()

    private var signInJob: Job? = null

    fun onEmailChanged(email: String) {
        _signInFormState.update {
            it.copy(
                email = email,
                emailError = email.emailValidate()
            )
        }
    }

    fun onClearEmail() {
        _signInFormState.update {
            it.copy(
                email = "",
                emailError = "".emailValidate()
            )

        }
    }

    fun onPasswordChanged(password: String) {
        _signInFormState.update {
            it.copy(
                password = password,
                passwordError = password.passwordValidate()
            )
        }
    }

    fun onRememberCredentialChanged(rememberStatus: Boolean) {
        isRememberCredentials = rememberStatus
    }

    fun signIn() {
        signInJob?.cancel()
        signInJob = viewModelScope.launch {
            if (_signInFormState.value.isFormValid) {
                _signInState.value = AuthenticationState.Loading
                authRepository.signIn(
                    username = _signInFormState.value.email,
                    password = _signInFormState.value.password,
                    isRemembered = isRememberCredentials
                ).collect {
                    when (val result = it) {
                        is DataResult.Error -> {
                            _signInState.value =
                                AuthenticationState.Error(message = result.errorMessage)
                        }


                        is DataResult.Success -> {
                            _signInState.value = AuthenticationState.Success()
                        }
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        signInJob?.cancel()
    }

}

