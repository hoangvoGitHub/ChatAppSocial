package com.hoangkotlin.chatappsocial.core.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun SessionExpiredAlertDialog(
    onConfirmation: ()-> Unit,
) {
    AlertDialog(

        text = {
            Text(text = "Session has expired, please login!")
        },
        onDismissRequest = {
            onConfirmation()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        }
    )
}