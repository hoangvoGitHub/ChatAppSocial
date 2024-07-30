package com.hoangkotlin.chatappsocial.feature.profile.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hoangkotlin.chatappsocial.core.ui.R
import com.hoangkotlin.chatappsocial.core.ui.components.image.AvatarImage
import com.hoangkotlin.chatappsocial.core.ui.components.image.SocialUserAvatar
import com.hoangkotlin.chatappsocial.core.ui.components.image.rememberSocialImagePainter
import com.hoangkotlin.chatappsocial.feature.profile.DefaultProfileImagePickerIcon
import com.hoangkotlin.chatappsocial.feature.profile.ToDisplayImage
import com.hoangkotlin.chatappsocial.feature.profile.UploadProfileImageState


@Composable
fun ProfileImageContainer(
    modifier: Modifier = Modifier,
    state: UploadProfileImageState = UploadProfileImageState(),
    onUpdateProfileImageClick: () -> Unit = {}
) {
    val startAngle = 90f - 18f
    val swipeAngle = 308f

    val backgroundArcColor = MaterialTheme.colorScheme.secondaryContainer
    val foregroundArcColor = MaterialTheme.colorScheme.primary
    val errorColor = MaterialTheme.colorScheme.error
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val blinking by infiniteTransition.animateColor(
        backgroundArcColor, backgroundArcColor,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2_000
                backgroundArcColor.copy(alpha = 0.5f) at 1_000
            }
        ),
        label = ""
    )
    Box(
        modifier = modifier
            .padding(8.dp)
    ) {
        Canvas(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize()
        ) {
            // Background Arc
            val colorForBackground = if (state.isFailed)
                errorColor
            else if (state.isUpdating)
                blinking
            else backgroundArcColor

            drawArc(
                color = colorForBackground,
                startAngle,
                swipeAngle,
                false,
                style = Stroke(10.dp.toPx(), cap = StrokeCap.Round),
                size = Size(size.width, size.height),
            )


            if (state.isUpdating) {
                val progressAngle = swipeAngle * state.progress
                // Foreground Arc
                drawArc(
                    color = foregroundArcColor,
                    startAngle,
                    progressAngle,
                    false,
                    style = Stroke(10.dp.toPx(), cap = StrokeCap.Round),
                    size = Size(size.width, size.height)
                )
            }

        }
        MyProfileImage(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(8.dp),
            uploadProfileImageState = state,
            onUpdateProfileImageClick = onUpdateProfileImageClick
        )
    }
}

@Composable
fun MyProfileImage(
    modifier: Modifier = Modifier,
    uploadProfileImageState: UploadProfileImageState,
    onUpdateProfileImageClick: () -> Unit
) {
    val toDisplayImage = uploadProfileImageState.imageToDisplay
    Box(modifier = modifier) {
        when (toDisplayImage) {
            is ToDisplayImage.FromLocal -> AvatarImage(
                painter = rememberSocialImagePainter(
                    data = toDisplayImage.uri,
                    placeholderPainter = painterResource(id = R.drawable.social_preview_avatar),
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.BottomEnd)

            )

            is ToDisplayImage.FromUser -> SocialUserAvatar(
                user = toDisplayImage.user,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.BottomEnd),
                showOnlineIndicator = false
            )
        }

        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.background)
                .padding(6.dp)
                .align(Alignment.BottomEnd)
        ) {
            DefaultProfileImagePickerIcon(onClick = onUpdateProfileImageClick)
        }
    }
}







