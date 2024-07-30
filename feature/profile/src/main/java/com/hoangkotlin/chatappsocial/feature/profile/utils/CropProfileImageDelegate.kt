package com.hoangkotlin.chatappsocial.feature.profile.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.hoangkotlin.chatappsocial.feature.profile.model.ImageSourceOption

internal object CropProfileImageDelegate {

    @Composable
    fun rememberLauncherForActivityResult(onUriResult: (CropImageResult) -> Unit): ManagedActivityResultLauncher<CropImageContractOptions, CropImageView.CropResult> {
        val context = LocalContext.current
        return rememberLauncherForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                // Use the returned uri.
                val uriContent = result.uriContent
                val uriFilePath = result.getUriFilePath(context) // optional usage
                if (uriContent != null && uriFilePath != null) {
                    onUriResult(CropImageResult.Success(uriContent, uriFilePath))
                }else{
                    onUriResult(CropImageResult.Error)
                }
            } else {
                // An error occurred.
                onUriResult(CropImageResult.Error)
            }
        }
    }

    private fun buildCropImageContractOptions(imageSourceOption: ImageSourceOption): CropImageContractOptions {
        val (imageSourceIncludeGallery,
            imageSourceIncludeCamera
        ) = when (imageSourceOption) {
            ImageSourceOption.Camera -> false to true
            ImageSourceOption.Gallery -> true to false

        }
        return CropImageContractOptions(
            uri = null,
            CropImageOptions(
                cropShape = CropImageView.CropShape.RECTANGLE,
                outputCompressFormat = Bitmap.CompressFormat.PNG,
                fixAspectRatio = true,
                imageSourceIncludeGallery = imageSourceIncludeGallery,
                imageSourceIncludeCamera = imageSourceIncludeCamera,
                activityBackgroundColor = Color.WHITE,
                activityMenuIconColor = Color.CYAN, // icon color
                toolbarBackButtonColor = Color.GREEN, //back button color

            )
        )
    }

    fun ManagedActivityResultLauncher<CropImageContractOptions, CropImageView.CropResult>.launchWithImageSourceOption(
        imageSourceOption: ImageSourceOption
    ) {
        this.launch(buildCropImageContractOptions(imageSourceOption))
    }

    private const val TAG = "CropProfileImageDelegate"

    sealed class CropImageResult {
        data class Success(val uri: Uri, val filePath: String) : CropImageResult()
        data object Error : CropImageResult()
    }
}