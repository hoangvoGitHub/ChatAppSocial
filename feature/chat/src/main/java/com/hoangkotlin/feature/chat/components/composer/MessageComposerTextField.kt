package com.hoangkotlin.feature.chat.components.composer

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageComposerTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    labelText: String = "Let's chat",
    onFocusChange: (isFocused: Boolean) -> Unit
) {
    var isFocused by remember {
        mutableStateOf(false)
    }

    BasicTextField(value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .focusRequester(FocusRequester.Default)
            .onFocusChanged { state ->
                onFocusChange(state.isFocused)
                isFocused = state.isFocused
            },
        textStyle = MaterialTheme.typography.labelLarge.copy(
            color = MaterialTheme.colorScheme.onBackground
        ),
        maxLines = 10,
        enabled = true,
        singleLine = !isFocused,
        keyboardOptions = KeyboardOptions(
            KeyboardCapitalization.Sentences,
            autoCorrect = false,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Default
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        decorationBox = {
            TextFieldDefaults.DecorationBox(value = value,
                innerTextField = { it() },
                enabled = true,
                singleLine = !isFocused,
                visualTransformation = remember {
                    DefaultVisualTransformation()
                },
                contentPadding = PaddingValues(10.dp),
                interactionSource = remember { MutableInteractionSource() },
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                shape = RoundedCornerShape(20.dp),
                placeholder = {
                    if (value.isEmpty()) {
                        Text(
                            text = labelText, style = MaterialTheme.typography.labelLarge.copy(
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                        )
                    }
                })

        })
}