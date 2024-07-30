package com.hoangkotlin.chatappsocial.feature.auth.sign_up

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoangkotlin.chatappsocial.core.common.model.PrefilledData
import com.hoangkotlin.chatappsocial.core.ui.components.KeyboardAware
import com.hoangkotlin.chatappsocial.core.ui.theme.SocialChatAppTheme
import com.hoangkotlin.chatappsocial.feature.auth.SignUpState
import com.hoangkotlin.chatappsocial.feature.auth.components.AuthTextFieldContainer
import com.hoangkotlin.chatappsocial.feature.auth.components.PasswordInputFieldContainer
import com.hoangkotlin.chatappsocial.feature.auth.sign_up.SignUpFormAction.Companion.clearedValue
import kotlinx.coroutines.launch


@Composable
fun SignUpRoute(
    modifier: Modifier = Modifier,
    onNavigateToSignIn: (PrefilledData?) -> Unit,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val signUpFormState by viewModel.signUpFormState.collectAsStateWithLifecycle()
    val signUpState by viewModel.signUpState.collectAsStateWithLifecycle()
    val currentPage = signUpFormState.currentPage

    Box(modifier = modifier) {
        when (val state = signUpState) {
            is SignUpState.Error -> {
                Toast.makeText(LocalContext.current, state.message, Toast.LENGTH_SHORT)
                    .show()
            }

            is SignUpState.Idle -> Unit
            is SignUpState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.Center)
                )
            }

            is SignUpState.Success -> {
                Toast.makeText(LocalContext.current, "Sign Up Successfully", Toast.LENGTH_LONG)
                    .show()
                onNavigateToSignIn(
                    PrefilledData(
                        email = state.email,
                        password = state.password
                    )
                )
            }
        }
        SignUpScreen(
            signUpFormState = signUpFormState,
            onSignUpFormAction = viewModel::onFormAction,
            onSignUpClick = viewModel::signUp,
            onNavigateToSignIn = onNavigateToSignIn,
        )

    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    signUpFormState: SignUpFormState,
    onSignUpFormAction: (SignUpFormAction) -> Unit,
    onSignUpClick: () -> Unit,
    onNavigateToSignIn: (PrefilledData?) -> Unit
) {
    val scrollState = rememberScrollState()


    var isEmailFocused by remember {
        mutableStateOf(false)
    }
    var isPasswordFocused by remember {
        mutableStateOf(false)
    }
    var isConfirmPasswordFocused by remember {
        mutableStateOf(false)
    }

    val shouldScroll = isEmailFocused || isPasswordFocused || isConfirmPasswordFocused

    val coroutineScope = rememberCoroutineScope()
    val keyboardHeight = WindowInsets.ime.getBottom(LocalDensity.current)
    LaunchedEffect(keyboardHeight, shouldScroll) {
        if (shouldScroll) {
            coroutineScope.launch {
                scrollState.animateScrollBy(
                    keyboardHeight.toFloat(), TweenSpec(
                        easing = FastOutSlowInEasing
                    )
                )
            }
        }
    }
    KeyboardAware {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SignUpWelcomeTextContainer(modifier.padding(top = 64.dp, bottom = 32.dp))
            AuthTextFieldContainer(
                labelText = "First name",
                value = signUpFormState.firstName,
                error = signUpFormState.firstNameError,
                onValueChange = { value ->
                    onSignUpFormAction(SignUpFormAction.FirstNameFieldChange(value))
                },
                onClearValue = {
                    onSignUpFormAction(SignUpFormAction.FirstNameFieldChange(clearedValue))
                }
            )
            AuthTextFieldContainer(
                labelText = "Last name",
                value = signUpFormState.lastName,
                error = signUpFormState.lastNameError,
                onValueChange = { value ->
                    onSignUpFormAction(SignUpFormAction.LastNameFieldChange(value))
                },
                onClearValue = {
                    onSignUpFormAction(SignUpFormAction.LastNameFieldChange(clearedValue))
                }
            )
            AuthTextFieldContainer(
                value = signUpFormState.email,
                onValueChange = { value ->
                    onSignUpFormAction(SignUpFormAction.EmailFieldChange(value))
                },
                onClearValue = {
                    onSignUpFormAction(SignUpFormAction.LastNameFieldChange(clearedValue))
                },
                placeHolderText = "example@gmail.com",
                labelText = "Email",
                error = signUpFormState.emailError,
                onFocusChange = {
                    isEmailFocused = it
                }
            )
            PasswordInputFieldContainer(
                password = signUpFormState.password,
                onPasswordChanged = { value ->
                    onSignUpFormAction(SignUpFormAction.PasswordFieldChange(value))
                },
                passwordError = signUpFormState.passwordError,
                onFocusChange = { isFocused ->
                    isPasswordFocused = isFocused
                }
            )
            PasswordInputFieldContainer(
                password = signUpFormState.confirmPassword,
                labelText = "Confirm password",
                onPasswordChanged = { value ->
                    onSignUpFormAction(SignUpFormAction.ConfirmPasswordFieldChange(value))
                },
                passwordError = signUpFormState.confirmPasswordError,
                onFocusChange = { isFocused ->
                    isConfirmPasswordFocused = isFocused
                }
            )
            Button(
                onClick = onSignUpClick,
                enabled = signUpFormState.isFormValid,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Sign Up")
            }
            Spacer(modifier = Modifier.height(16.dp))
            AlreadyHaveAccountContainer(onNavigateToSignIn = onNavigateToSignIn)

        }
    }

}


@Composable
fun SignUpWelcomeTextContainer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.padding(bottom = 8.dp)) {
            Box(
                modifier = Modifier
                    .height(6.dp)
                    .width(52.dp)
                    .offset(y = (-8).dp)
                    .background(color = MaterialTheme.colorScheme.primary)
                    .align(Alignment.BottomEnd)
            )
            Text(
                text = "Sign up with Email", style = MaterialTheme.typography.headlineLarge
            )
        }
        Text(
            text = "Get chatting with friends and family today by signing up for our chat app!",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge,
        )
    }

}

@Composable
fun AlreadyHaveAccountContainer(
    modifier: Modifier = Modifier,
    onNavigateToSignIn: (PrefilledData?) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Already have an account?",
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 16.sp
            )
        )
        TextButton(onClick = {
            onNavigateToSignIn(null)
        }) {
            Text(
                text = "Log in",
                maxLines = 1,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 16.sp
                )
            )
        }
    }
}

@Composable
fun SignUpScreenPreview() {
    SocialChatAppTheme {
        Scaffold { innerPadding ->
            SignUpScreen(
                modifier = Modifier.padding(innerPadding),
                signUpFormState = SignUpFormState(),
                onSignUpFormAction = { _ -> },
                onNavigateToSignIn = {},
                onSignUpClick = {}
            )
        }

    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SignUpScreenPreviewDark() {
    SignUpScreenPreview()
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun SignUpScreenPreviewLight() {
    SignUpScreenPreview()
}

