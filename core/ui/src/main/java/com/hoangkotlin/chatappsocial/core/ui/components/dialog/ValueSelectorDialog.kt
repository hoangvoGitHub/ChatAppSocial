package com.hoangkotlin.chatappsocial.core.ui.components.dialog

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hoangkotlin.chatappsocial.core.ui.theme.SocialChatAppTheme

@Composable
inline fun <T> ValueSelectorDialog(
    noinline onDismiss: () -> Unit,
    title: String,
    selectedValue: T,
    values: List<T>,
    crossinline onValueSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    crossinline valueText: (T) -> String = { it.toString() }
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = modifier
                .padding(all = 48.dp)
                .background(
                    color = colorScheme.background,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(vertical = 16.dp),
        ) {
            BasicText(
                text = title,
                style = typography.labelLarge,
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 24.dp)
            )

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                values.forEach { value ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .clickable(
                                onClick = {
                                    onDismiss()
                                    onValueSelected(value)
                                }
                            )
                            .padding(vertical = 12.dp, horizontal = 24.dp)
                            .fillMaxWidth()
                    ) {
                        if (selectedValue == value) {
                            Canvas(
                                modifier = Modifier

                                    .background(
                                        color = colorScheme.primary,
                                        shape = CircleShape
                                    )
                                    .shadow(
                                        ambientColor = Color.Black,
                                        spotColor = Color.Black,
                                        elevation = 4.dp,
                                        clip = true
                                    )
                                    .size(18.dp)

                            ) {
                                drawCircle(
                                    color = colorScheme.onPrimary,
                                    radius = 4.dp.toPx(),
                                    center = size.center,
//                                    shadow = Shadow(
//                                        color = Color.Black.copy(alpha = 0.4f),
//                                        blurRadius = 4.dp.toPx(),
//                                        offset = Offset(x = 0f, y = 1.dp.toPx())
//                                    )
                                )
                            }
                        } else {
                            Spacer(
                                modifier = Modifier
                                    .size(18.dp)
                                    .border(
                                        width = 1.dp,
                                        color = colorScheme.secondary,
                                        shape = CircleShape
                                    )
                            )
                        }

                        BasicText(
                            text = valueText(value),
                            style = typography.labelMedium
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 24.dp)
            ) {
                DialogTextButton(
                    text = "Cancel",
                    onClick = onDismiss,
                    modifier = Modifier
                )
            }
        }
    }
}

private enum class Value {
    ValueTest,
    ValueOk,
    ValueHaha
}

@Preview
@Composable
fun ValueSelectorDialogPreview() {
    SocialChatAppTheme {
        Scaffold {
            ValueSelectorDialog<Value>(
                modifier = Modifier.padding(it),
                onDismiss = {},
                onValueSelected = {

                },
                title = "Value",
                values = Value.entries,
                selectedValue = Value.ValueHaha

            )
        }
    }
}