package com.hoangkotlin.chatappsocial.feature.auth.sign_in

import android.content.res.Configuration
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoangkotlin.chatappsocial.core.ui.components.KeyboardAware
import com.hoangkotlin.chatappsocial.core.ui.theme.SocialChatAppTheme
import com.hoangkotlin.chatappsocial.feature.auth.AuthenticationState
import com.hoangkotlin.chatappsocial.feature.auth.components.AuthTextFieldContainer
import com.hoangkotlin.chatappsocial.feature.auth.components.PasswordInputFieldContainer
import kotlinx.coroutines.launch
import com.hoangkotlin.chatappsocial.core.ui.R as uiR

private const val TAG = "SignInScreen"

@Composable
fun SignInRoute(
    modifier: Modifier = Modifier,
    onNavigateToHome: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    viewModel: SignInViewModel = hiltViewModel()
) {
    val signInFormState by viewModel.signInFormState.collectAsStateWithLifecycle()
    val signInState by viewModel.signInState.collectAsStateWithLifecycle()

    Box(modifier = modifier) {
        when (val state = signInState) {
            is AuthenticationState.Error -> {
                Toast.makeText(LocalContext.current, state.message, Toast.LENGTH_SHORT)
                    .show()
            }

            is AuthenticationState.Idle -> Unit
            is AuthenticationState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.CenterEnd)
                )
            }

            is AuthenticationState.Success -> {
                Toast.makeText(LocalContext.current, "Sign In Successfully", Toast.LENGTH_LONG)
                    .show()
                onNavigateToHome()
            }
        }


        SignInScreen(
            modifier = Modifier
                .fillMaxSize()
                .focusable(false),
            onEmailChanged = viewModel::onEmailChanged,
            onClearEmail = viewModel::onClearEmail,
            onPasswordChanged = viewModel::onPasswordChanged,
            onCheckedChanged = viewModel::onRememberCredentialChanged,
            onSignInClicked = viewModel::signIn,
            onNavigateToSignUp = onNavigateToSignUp,
            signInFormState = signInFormState

        )
    }


}

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    signInFormState: SignInFormState = SignInFormState(),
    onEmailChanged: (String) -> Unit,
    onClearEmail: () -> Unit,
    onPasswordChanged: (String) -> Unit,
    onCheckedChanged: (Boolean) -> Unit,
    onSignInClicked: () -> Unit,
    onNavigateToSignUp: () -> Unit,
) {

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardHeight = WindowInsets.ime.getBottom(LocalDensity.current)
    LaunchedEffect(key1 = keyboardHeight) {
        coroutineScope.launch {
            scrollState.scrollBy(keyboardHeight.toFloat())
        }
    }
    KeyboardAware {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SignInWelcomeTextContainer(modifier = Modifier.padding(top = 64.dp))
            SocialAccountsRow(modifier = modifier.padding(vertical = 24.dp))
            AuthTextFieldContainer(
                labelText = "Email",
                value = signInFormState.email,
                onValueChange = onEmailChanged,
                onClearValue = onClearEmail,
                placeHolderText = "example@gmail.com",
                error = signInFormState.emailError
            )
            Spacer(modifier = Modifier.height(8.dp))
            PasswordInputFieldContainer(
                password = signInFormState.password,
                onPasswordChanged = onPasswordChanged,
                passwordError = signInFormState.passwordError,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                RememberMeCheckBox(
                    modifier = Modifier.weight(1F),
                    onCheckedChanged = onCheckedChanged
                )
                TextButton(onClick = { }) {
                    Text(
                        text = "Forgot Password?",
                        maxLines = 1,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 16.sp
                        )
                    )
                }

            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onSignInClicked,
                enabled = signInFormState.isFormValid,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Login")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account?",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 16.sp
                    )
                )
                TextButton(onClick = onNavigateToSignUp) {
                    Text(
                        text = "Register",
                        maxLines = 1,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 16.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun SocialAccountsRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        SocialPlatformIcon(iconRes = uiR.drawable.facebook_icon)
        SocialPlatformIcon(iconRes = uiR.drawable.google_icon)
        SocialPlatformIcon(iconRes = uiR.drawable.apple_icon)
    }
}

@Composable
fun SocialPlatformIcon(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    contentDescription: String = "",
    onClick: () -> Unit = {},
) {

    Image(
        modifier = modifier
            .padding(8.dp)
            .clip(CircleShape)
            .background(color = MaterialTheme.colorScheme.inversePrimary)
            .clickable { onClick() }
            .border(
                width = 1.dp,
                color = Color.Transparent,
                shape = CircleShape
            )
            .size(48.dp),

        painter = painterResource(id = iconRes),
        contentDescription = contentDescription
    )

}

@Composable
fun SignInWelcomeTextContainer(modifier: Modifier = Modifier) {
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
                    .align(Alignment.BottomStart)
            )
            Text(
                text = "Hi, there!", style = MaterialTheme.typography.headlineLarge
            )
        }
        Text(
            text = "Welcome back! Sign in using your social account or email to continue us",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge,
        )
    }

}

@Composable
fun RememberMeCheckBox(
    modifier: Modifier = Modifier,
    onCheckedChanged: (Boolean) -> Unit
) {
    var isRememberCredential by rememberSaveable {
        mutableStateOf(false)
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Checkbox(checked = isRememberCredential, onCheckedChange = {
            isRememberCredential = it
            onCheckedChanged(it)
        })
        Text(
            modifier = Modifier.weight(1f),
            text = "Remember me",
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 16.sp
            ),
            textAlign = TextAlign.Start
        )
    }
}


@Composable
fun SignInScreenPreview() {
    SocialChatAppTheme {
        SignInScreen(
            onSignInClicked = {},
            onCheckedChanged = {},
            onPasswordChanged = {},
            onClearEmail = {},
            onEmailChanged = { _ -> },
            onNavigateToSignUp = {}
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SignInScreenPreviewDark() {
    SignInScreenPreview()
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun SignInScreenPreviewLight() {
    SignInScreenPreview()
}

