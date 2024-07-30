package com.hoangkotlin.chatappsocial.feature.profile.utils

import android.graphics.Bitmap
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView

internal class Constants {

    companion object{
        val cropImageOptions : CropImageOptions = CropImageOptions(
        cropShape = CropImageView.CropShape.RECTANGLE,
        outputCompressFormat = Bitmap.CompressFormat.PNG,
        fixAspectRatio = true,
        activityBackgroundColor = android.graphics.Color.WHITE,
        activityMenuIconColor = android.graphics.Color.CYAN, // icon color
        toolbarBackButtonColor =  android.graphics.Color.GREEN, //back button color

        )
    }
}