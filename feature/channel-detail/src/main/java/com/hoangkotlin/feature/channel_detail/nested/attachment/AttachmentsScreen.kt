package com.hoangkotlin.feature.channel_detail.nested.attachment

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoangkotlin.chatappsocial.core.chat_client.extension.isMedia
import com.hoangkotlin.chatappsocial.core.model.attachment.SocialChatAttachment
import com.hoangkotlin.chatappsocial.core.model.attachment.UploadState
import com.hoangkotlin.chatappsocial.core.ui.components.BackButton
import com.hoangkotlin.chatappsocial.core.ui.components.LoadingIndicator
import com.hoangkotlin.chatappsocial.core.ui.components.image.mirrorRtl
import com.hoangkotlin.chatappsocial.core.ui.components.image.rememberSocialImagePainter
import com.hoangkotlin.chatappsocial.core.ui.previewdatas.PreviewAttachmentData
import com.hoangkotlin.chatappsocial.core.ui.theme.SocialChatAppTheme
import com.hoangkotlin.feature.channel_detail.utils.Constants.MediaGridAdaptiveMinSize
import java.util.Date
import com.hoangkotlin.chatappsocial.core.ui.R as uiR

@Composable
fun AttachmentsRoute(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit = {},
    onNavigateToMediaViewer: (channelId: String, attachmentUri: String, createdAt: Date) -> Unit,

    ) {
    val viewModel: AttachmentsViewModel = hiltViewModel()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val attachmentsState by viewModel.attachmentsState.collectAsStateWithLifecycle()
    val channelId by viewModel.channelId.collectAsStateWithLifecycle()

    AttachmentScreen(
        modifier = modifier,
        selectedFilter = selectedFilter,
        onFilterSelected = viewModel::onFilterSelected,
        onBackPressed = onBackPressed,
        attachmentsState = attachmentsState,
        onAttachmentClick = { attachment ->
            if (attachment.isMedia
                && attachment.uploadState == UploadState.Success
                && channelId != null
            ) {
                attachment.createdAt?.let { createdAt ->
                    onNavigateToMediaViewer(
                        channelId!!,
                        attachment.upload?.path
                            ?: attachment.url
                            ?: attachment.imageUrl
                            ?: "",
                        createdAt
                    )
                }

            }
        }
    )
}

@Composable
fun AttachmentScreen(
    modifier: Modifier = Modifier,
    selectedFilter: AttachmentsFilter,
    onFilterSelected: (AttachmentsFilter) -> Unit,
    onBackPressed: () -> Unit,
    attachmentsState: AttachmentsState,
    onAttachmentClick: (SocialChatAttachment) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            AttachmentsScreenTopAppBar(onBackPressed = onBackPressed)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            AttachmentsFiltersRow(
                modifier = Modifier
                    .padding(8.dp),
                selectedFilter = selectedFilter,
                onFilterSelected = onFilterSelected
            )
            AttachmentContent(
                attachmentsFilter = selectedFilter,
                attachmentsState = attachmentsState,
                onAttachmentClick = onAttachmentClick
            )
        }
    }
}

@Composable
fun AttachmentContent(
    modifier: Modifier = Modifier,
    attachmentsFilter: AttachmentsFilter,
    attachmentsState: AttachmentsState,
    onAttachmentClick: (SocialChatAttachment) -> Unit
) {
    Box(modifier = modifier) {
        when (attachmentsFilter) {
            AttachmentsFilter.Media -> MediaAttachmentGrid(
                attachmentsState = attachmentsState,
                onAttachmentClick = onAttachmentClick,
            )

            AttachmentsFilter.File -> Box(modifier = Modifier.fillMaxSize())
            AttachmentsFilter.Other -> Box(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
fun MediaAttachmentGrid(
    modifier: Modifier = Modifier,
    onAttachmentClick: (SocialChatAttachment) -> Unit,
    attachmentsState: AttachmentsState
) {
    val (isLoading, isLoadingMore, _, attachments) = attachmentsState

    val attachmentsToDisplay = remember(attachments){
        attachments.reversed()
    }
    Box(modifier = modifier) {
        if (isLoading) {
            DefaultAttachmentsIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Adaptive(minSize = MediaGridAdaptiveMinSize.dp)
        ) {
            items(attachmentsToDisplay) { attachment ->
                Image(
                    modifier = Modifier
                        .clickable {
                            onAttachmentClick(attachment)
                        }
                        .height(128.dp),
                    painter = rememberSocialImagePainter(
                        data = attachment.upload ?: attachment.imageUrl ?: attachment.thumbnailUrl,
                        placeholderPainter = painterResource(id = uiR.drawable.hook_photo_1),
                        contentScale = ContentScale.Crop
                    ),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                )
            }
            if (isLoadingMore) {
                item(
                    span = {
                        GridItemSpan(maxLineSpan)
                    }
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        DefaultAttachmentsIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AttachmentsFiltersRow(
    modifier: Modifier = Modifier,
    filters: List<AttachmentsFilter> = DefaultAttachmentsFilters,
    selectedFilter: AttachmentsFilter,
    onFilterSelected: (AttachmentsFilter) -> Unit
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { filter ->
            val isSelected = selectedFilter == filter
            AttachmentsFilter(
                filter = filter,
                isSelected = isSelected,
                onFilterSelected = onFilterSelected
            )
        }

    }
}

@Composable
fun AttachmentsFilter(
    modifier: Modifier = Modifier,
    filter: AttachmentsFilter,
    isSelected: Boolean,
    onFilterSelected: (AttachmentsFilter) -> Unit
) {
    FilterChip(
        modifier = modifier,
        onClick = { onFilterSelected(filter) },
        label = { Text(text = stringResource(id = filter.labelRes)) },
        selected = isSelected,
        shape = RoundedCornerShape(20.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttachmentsScreenTopAppBar(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
) {
    TopAppBar(
        modifier = modifier,
        title = { },
        navigationIcon = {
            DefaultAttachmentsScreenBackButton(onBackPressed = onBackPressed)
        }
    )
}

@Composable
fun DefaultAttachmentsScreenBackButton(onBackPressed: () -> Unit) {
    val layoutDirection = LocalLayoutDirection.current

    BackButton(
        modifier = Modifier.mirrorRtl(layoutDirection = layoutDirection),
        vector = Icons.AutoMirrored.Filled.ArrowBack,
        onBackPressed = onBackPressed,
    )
}

@Composable
fun DefaultAttachmentsIndicator(modifier: Modifier = Modifier) {
    LoadingIndicator(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
    )
}


@Preview
@Composable
fun AttachmentScreenPreview() {
    SocialChatAppTheme {
        AttachmentScreen(
            selectedFilter = AttachmentsFilter.Media,
            onFilterSelected = {},
            onBackPressed = {},
            attachmentsState = AttachmentsState(
                isLoading = false,
                isLoadingMore = true,
                attachments = PreviewAttachmentData.imageAttachments
            ),
            onAttachmentClick = {}
        )
    }
}

@Preview(device = Devices.PIXEL_TABLET)
@Composable
fun AttachmentScreenPreviewTablet() {
    SocialChatAppTheme {
        AttachmentScreenPreview()
    }
}