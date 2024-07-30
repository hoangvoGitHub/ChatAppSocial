package com.hoangkotlin.chatappsocial.feature.profile

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import com.hoangkotlin.chatappsocial.core.ui.theme.Green700
import com.hoangkotlin.chatappsocial.core.ui.theme.SocialChatAppTheme
import com.hoangkotlin.chatappsocial.feature.profile.components.MyProfileImage
import com.hoangkotlin.chatappsocial.feature.profile.components.ProfileImageContainer
import com.hoangkotlin.chatappsocial.feature.profile.model.ImageSourceOption
import com.hoangkotlin.chatappsocial.feature.profile.utils.CropProfileImageDelegate
import com.hoangkotlin.chatappsocial.feature.profile.utils.CropProfileImageDelegate.launchWithImageSourceOption
import kotlinx.coroutines.launch
import com.hoangkotlin.chatappsocial.core.common.R as commonR
import com.hoangkotlin.chatappsocial.core.ui.R as uiR

private const val TAG = "imageSourceOption"

@Composable
fun ProfileRoute(
    modifier: Modifier = Modifier
) {
    val viewModel: ProfileViewModel = hiltViewModel()
    val uploadProfileImageState by viewModel.uploadProfileImageState.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val cropImageLauncher =
        CropProfileImageDelegate.rememberLauncherForActivityResult(onUriResult = { result ->
            when (result) {
                is CropProfileImageDelegate.CropImageResult.Error -> {
                    Toast.makeText(context, "Some errors occur!", Toast.LENGTH_LONG)
                        .show()
                }

                is CropProfileImageDelegate.CropImageResult.Success -> {
                    viewModel.updateProfileImage(
                        uri = result.uri,
                        filePath = result.filePath
                    )
                }
            }
        })

    MyProfileScreen(
        modifier = modifier,
        uploadProfileImageState = uploadProfileImageState,
        currentUser = currentUser,
        onProfileSourceOptionClick = { option ->
            cropImageLauncher.launchWithImageSourceOption(option)
        })


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProfileScreen(
    modifier: Modifier = Modifier,
    currentUser: SocialChatUser?,
    uploadProfileImageState: UploadProfileImageState,
    onProfileSourceOptionClick: (ImageSourceOption) -> Unit

) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileImageContainer(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .size(144.dp),
            state = uploadProfileImageState,
            onUpdateProfileImageClick = {
                showBottomSheet = true
            }
        )

        MyProfileNameContainer(displayName = currentUser?.name)
        ListItem(
            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background),
            headlineContent = {
                Text(
                    text = "Dark mode",
                    style = MaterialTheme.typography.labelLarge
                )
            },
            supportingContent = {
                Text(
                    text = "System",
                    style = MaterialTheme.typography.bodySmall
                )
            },
            leadingContent = {
                Icon(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                        .padding(8.dp),
                    imageVector = Icons.Rounded.DarkMode,
                    contentDescription = Icons.Rounded.DarkMode.name,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        )

        ListItem(
            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background),
            headlineContent = {
                Text(
                    text = "Online status",
                    style = MaterialTheme.typography.labelLarge
                )
            },
            supportingContent = {
                Text(
                    text = "Enable",
                    style = MaterialTheme.typography.bodySmall
                )
            },
            leadingContent = {
                Icon(
                    modifier = Modifier
                        .background(
                            color = Green700,
                            shape = CircleShape
                        )
                        .padding(8.dp),
                    painter = painterResource(id = uiR.drawable.noun_active_status),
                    contentDescription = Icons.Rounded.DarkMode.name,
                    tint = Color.White
                )
            }
        )
    }

    if (showBottomSheet) {
        ImagePickerSourceSelectionBottomSheet(
            sheetState = sheetState,
            onOptionClick = { sourceOption ->
                onProfileSourceOptionClick(sourceOption)
                scope.launch {
                    sheetState.hide()

                }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showBottomSheet = false
                    }
                }
            },
            onDismissRequest = {
                showBottomSheet = false
            })
    }
}

@Composable
fun MyProfileNameContainer(modifier: Modifier = Modifier, displayName: String?) {
    Text(
        modifier = modifier,
        text = displayName ?: stringResource(id = commonR.string.chat_notification_empty_username),
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onBackground
    )
}


@Composable
fun DefaultProfileImagePickerIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Icon(
        modifier = modifier
            .clip(CircleShape)
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(8.dp),
        imageVector = Icons.Rounded.CameraAlt,
        contentDescription = Icons.Rounded.CameraAlt.name
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePickerSourceSelectionBottomSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {},
    sheetState: SheetState,
    onOptionClick: (ImageSourceOption) -> Unit
) {

    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Change my profile image",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Column(modifier = Modifier.padding(8.dp)) {
            ImageSourceOption.entries.forEach { option ->
                ImageSourceOptionContainer(
                    imageSourceOption = option,
                    onOptionClick = onOptionClick
                )
            }
        }

    }
}

@Composable
fun ImageSourceOptionContainer(
    modifier: Modifier = Modifier,
    imageSourceOption: ImageSourceOption,
    onOptionClick: (ImageSourceOption) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onOptionClick(imageSourceOption)
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            modifier = Modifier.padding(16.dp),
            imageVector = imageSourceOption.imageVector,
            contentDescription = stringResource(id = imageSourceOption.labelRes)
        )
        Text(
            text = stringResource(id = imageSourceOption.labelRes),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.weight(1f)
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileImageContainerPreview() {

    SocialChatAppTheme {
        Scaffold {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileImageContainer(
                    modifier = Modifier
                        .size(144.dp),
                    state = UploadProfileImageState(
                        isUpdating = true,
                        progress = 0.5f,
                        isFailed = true
                    ),
                    onUpdateProfileImageClick = {

                    }
                )
                MyProfileImage(
                    modifier = Modifier.size(120.dp),
                    uploadProfileImageState = UploadProfileImageState(),
                    onUpdateProfileImageClick = {}
                )
            }

        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfileImageContainerPreviewDark() {
    ProfileImageContainerPreview()
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    device = Devices.PIXEL_TABLET
)
@Composable
fun ProfileImageContainerPreviewLight() {
    ProfileImageContainerPreview()
}


@Composable
fun ProfileRoutePreview() {
    SocialChatAppTheme {
        ProfileRoute()
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProfileRoutePreviewDark() {
    ProfileRoutePreview()
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun ProfileRoutePreviewLight() {
    ProfileRoutePreview()
}