package com.hoangkotlin.chatappsocial.feature.media_viewer

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoangkotlin.chatappsocial.core.model.attachment.SocialChatAttachment
import com.hoangkotlin.chatappsocial.core.ui.components.LoadingIndicator
import com.hoangkotlin.chatappsocial.core.ui.components.image.rememberSocialImagePainter
import com.hoangkotlin.chatappsocial.core.ui.previewdatas.PreviewAttachmentData
import com.hoangkotlin.chatappsocial.core.ui.theme.SocialChatAppTheme
import com.hoangkotlin.chatappsocial.feature.media_viewer.components.DefaultMediaViewerAppBar
import com.hoangkotlin.chatappsocial.feature.media_viewer.model.MediaViewerAction
import com.hoangkotlin.chatappsocial.feature.media_viewer.utils.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MediaViewerRoute(
    onBackPress: () -> Unit,
) {
    val viewModel: MediaViewerViewModel = hiltViewModel()
    val mediaViewerState by viewModel.mediaViewerState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    MediaViewerScreen(
        mediaViewerState = mediaViewerState,
        onBackPress = onBackPress,
        onActionClick = { action, attachment ->
            when (action) {
                MediaViewerAction.Download -> {
                    viewModel.downloadAttachment(attachment)
                }

                else -> {
                    Toast.makeText(
                        context,
                        "This feature is not implemented yet",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    )
}

@Composable
fun MediaViewerScreen(
    modifier: Modifier = Modifier,
    mediaViewerState: MediaViewerState,
    onBackPress: () -> Unit,
    onActionClick: (MediaViewerAction, SocialChatAttachment) -> Unit,
) {
    AnimatedContent(
        modifier = modifier, targetState = mediaViewerState, label = "MediaViewerState"
    ) { state ->
        when (state) {
            is MediaViewerState.Failed -> FailedMediaViewerContent()
            is MediaViewerState.Loading -> LoadingMediaViewerContent()
            is MediaViewerState.Success -> SuccessMediaViewerContent(
                initialIndex = remember {
                    state.initialIndex
                },
                attachments = state.attachments,
                onBackPress = onBackPress,
                onActionClick = onActionClick
            )
        }
    }


}

@Composable
fun FailedMediaViewerContent(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        MediaViewerLoadingIndicator()
    }
}

@Composable
fun LoadingMediaViewerContent(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        MediaViewerLoadingIndicator()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SuccessMediaViewerContent(
    modifier: Modifier = Modifier,
    initialIndex: Int,
    attachments: List<SocialChatAttachment>,
    onBackPress: () -> Unit,
    onActionClick: (MediaViewerAction, SocialChatAttachment) -> Unit,
) {
    val pagerState =
        rememberPagerState(initialPage = initialIndex, pageCount = { attachments.size })

    val lazyListState = rememberLazyListState()

    var isPreviewBarVisible by remember { mutableStateOf(true) }
    var lastInteractionTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(lastInteractionTime) {
        while (true) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastInteractionTime > 2000) {
                isPreviewBarVisible = false
            }
            delay(100) // Check every 100 ms
        }
    }

    val onGesture = {
        lastInteractionTime = System.currentTimeMillis()
        isPreviewBarVisible = true
    }

    var scale by remember { mutableFloatStateOf(Constants.MinZoomScale) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    LaunchedEffect(pagerState.currentPage) {
        onGesture()
        val currentPage = pagerState.currentPage
        val firstVisibleIndex = lazyListState.firstVisibleItemIndex
        val lastVisibleIndex =
            firstVisibleIndex + lazyListState.layoutInfo.visibleItemsInfo.size - 1
        if (currentPage !in firstVisibleIndex..lastVisibleIndex) {
            lazyListState.animateScrollToItem(pagerState.currentPage)
        }
        scale = Constants.MinZoomScale
        offset = Offset.Zero
    }

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        if (zoomChange > 0f) {
            isPreviewBarVisible = false
        }
        // TODO: Restrict the zoom offset inside the image bounds
        scale = (scale * zoomChange).coerceIn(
            Constants.MinZoomScale, Constants.MaxZoomScale
        )

        // Update the offset to implement panning when zoomed.
        offset = if (scale == Constants.MinZoomScale) Offset.Zero
        else offset + panChange
    }

    MediaCarousel(
        attachments = attachments,
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        scale = Constants.MinZoomScale
                        offset = Offset.Zero
                    },
                    onTap = {
                        onGesture()
                    }
                )
            },
        pagerState = pagerState,
        previewLazyListState = lazyListState,
        onTap = onGesture,
        scale = scale,
        offset = offset,
        isPreviewBarVisible = isPreviewBarVisible,
        transformableState = transformableState,
        onBackPress = onBackPress,
        onActionClick = onActionClick
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaCarousel(
    modifier: Modifier = Modifier,
    attachments: List<SocialChatAttachment>,
    isPreviewBarVisible: Boolean,
    pagerState: PagerState,
    previewLazyListState: LazyListState,
    transformableState: TransformableState,
    scale: Float,
    offset: Offset,
    onTap: () -> Unit,
    onBackPress: () -> Unit,
    onActionClick: (MediaViewerAction, SocialChatAttachment) -> Unit,
) {
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .transformable(transformableState)
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                ),
            reverseLayout = true

        ) { index ->
            MediaContent(
                attachment = attachments[index],
            )
        }
        AnimatedVisibility(
            visible = isPreviewBarVisible,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            MediaPreviewRow(
                attachments = attachments,
                onClick = { index ->
                    onTap()
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                selectedIndex = pagerState.currentPage,
                lazyListState = previewLazyListState,
                modifier = Modifier.fillMaxWidth()
            )
        }

        DefaultMediaViewerAppBar(
            onBackPress = onBackPress,
            onActionClick = {
                onActionClick(it, attachments[pagerState.currentPage])
            }
        )

    }
}


@Composable
fun MediaContent(
    modifier: Modifier = Modifier, attachment: SocialChatAttachment
) {
    Image(
        painter = rememberSocialImagePainter(
            data = attachment.imageUrl,
            placeholderPainter = ColorPainter(MaterialTheme.colorScheme.background),
            contentScale = ContentScale.Fit
        ),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier.fillMaxSize()
    )
}

@Composable
fun MediaPreviewRow(
    modifier: Modifier = Modifier,
    attachments: List<SocialChatAttachment>,
    onClick: (index: Int) -> Unit,
    lazyListState: LazyListState,
    selectedIndex: Int
) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.outlineVariant,
            )
            .padding(vertical = 8.dp)
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(
                4.dp, Alignment.CenterHorizontally
            ),
            state = lazyListState,
            modifier = Modifier.fillMaxWidth(),
            reverseLayout = true
        ) {
            itemsIndexed(attachments) { index, attachment ->
                val isSelected = selectedIndex == index
                MediaPreviewItem(
                    attachment = attachment,
                    isSelected = isSelected,
                    onClick = {
                        onClick(index)
                    },
                )
            }
        }
    }
}

@Composable
fun MediaPreviewItem(
    modifier: Modifier = Modifier,
    attachment: SocialChatAttachment,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Image(
        modifier = modifier
            .size(80.dp)
            .clip(RoundedCornerShape(10))
            .border(
                width = if (isSelected) 4.dp else 0.dp,
                MaterialTheme.colorScheme.primary,
                RoundedCornerShape(10)
            )
            .clickable {
                onClick()
            },
        painter = rememberSocialImagePainter(
            data = attachment.imageUrl,
            size = 128,
            placeholderPainter = ColorPainter(MaterialTheme.colorScheme.outlineVariant)
        ),
        contentScale = ContentScale.Crop,
        contentDescription = null,
    )
}

@Composable
fun MediaViewerLoadingIndicator(modifier: Modifier = Modifier) {
    LoadingIndicator(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
    )
}


@Preview(showBackground = true)
@Composable
fun MediaScreenPreview() {
    SocialChatAppTheme {
        MediaViewerScreen(
            mediaViewerState = MediaViewerState.Success(
                3, PreviewAttachmentData.imageAttachments.take(3),
            ),
            onBackPress = {},
            onActionClick = { _, _ -> }
        )
    }
}

