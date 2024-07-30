package com.hoangkotlin.chatappsocial.feature.auth.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hoangkotlin.chatappsocial.core.ui.R as uiR


@Composable
fun AuthTextFieldContainer(
    modifier: Modifier = Modifier,
    placeHolderText: String? = null,
    labelText: String = "",
    value: String = "",
    onValueChange: (String) -> Unit = {},
    onClearValue: () -> Unit = {},
    onFocusChange: (Boolean) -> Unit = {},
    error: String? = null,
) {
    Column(modifier = modifier) {
        Text(
            text = labelText,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            ),
        )
        OutlinedTextField(
            colors = authTextFieldColors,
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(FocusRequester.Default)
                .onFocusChanged { state ->
                    onFocusChange(state.isFocused)
                },
            placeholder = {
                placeHolderText?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.labelLarge
                    )
                }

            },
            textStyle = MaterialTheme.typography.labelLarge,
            isError = error != null,
            supportingText = {
                Text(text = error ?: "")
            },
            singleLine = true,
            trailingIcon = {
                if (value.isNotBlank()) {
                    IconButton(onClick = onClearValue) {
                        Icon(imageVector = Icons.Filled.Clear, contentDescription = "Clear text")
                    }
                }

            },
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
fun PasswordInputFieldContainer(
    modifier: Modifier = Modifier,
    labelText: String = "Password",
    password: String,
    passwordError: String? = null,
    onPasswordChanged: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit = {}
) {
    var passwordHidden by rememberSaveable { mutableStateOf(true) }
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = labelText,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            ),
        )
        TextField(
            colors = authTextFieldColors,
            value = password,
            onValueChange = onPasswordChanged,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    onFocusChange(it.isFocused)
                },
            textStyle = MaterialTheme.typography.labelLarge,
            isError = passwordError != null,
            supportingText = {
                Text(text = passwordError ?: "")
            },
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { passwordHidden = !passwordHidden }) {
                    val visibilityIconRes =
                        if (passwordHidden) uiR.drawable.eye_svgrepo_com else uiR.drawable.eye_closed_svgrepo_com
                    // Please provide localized description for accessibility services
                    val description = if (passwordHidden) "Show password" else "Hide password"
                    Icon(painter = painterResource(id = visibilityIconRes), contentDescription = description)
                }
            },
            visualTransformation = if (passwordHidden) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            shape = RoundedCornerShape(8.dp)
        )
    }
}


private val authTextFieldColors: TextFieldColors
    @Composable get() =
        OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            errorBorderColor = Color.Transparent,
            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            errorContainerColor = MaterialTheme.colorScheme.errorContainer,

            )