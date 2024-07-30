package com.hoangkotlin.feature.chat.utils

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

object AttachmentsPickerDelegate {

    @Composable
    fun rememberLauncherForImagePickerActivityResult(
        onUrisResult: (List<Uri>) -> Unit
    ): ManagedActivityResultLauncher<PickVisualMediaRequest, List<@JvmSuppressWildcards Uri>> {
        return rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) {
            onUrisResult(it)
        }
    }

}