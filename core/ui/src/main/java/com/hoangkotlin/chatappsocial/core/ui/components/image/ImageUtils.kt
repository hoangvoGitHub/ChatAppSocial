package com.hoangkotlin.chatappsocial.core.ui.components.image

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.LayoutDirection
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.hoangkotlin.chatappsocial.core.ui.R
import kotlin.math.abs

private const val GradientDarkerColorFactor = 1.3f
private const val GradientLighterColorFactor = 0.7f

/**
 * Generates a gradient for an initials avatar based on the user initials.
 *
 * @param initials The user initials to use for gradient colors.
 * @return The [Brush] that represents the gradient.
 */
@Composable
@ReadOnlyComposable
internal fun initialsGradient(initials: String): Brush {
    val gradientBaseColors =
        LocalContext.current.resources.getIntArray(R.array.avatar_gradient_colors)

    val baseColorIndex = abs(initials.hashCode()) % gradientBaseColors.size
    val baseColor = gradientBaseColors[baseColorIndex]

    return Brush.linearGradient(
        listOf(
            Color(adjustColorBrightness(baseColor, GradientDarkerColorFactor)),
            Color(adjustColorBrightness(baseColor, GradientLighterColorFactor)),
        )
    )
}

/**
 * Applies the given mirroring scaleX based on the [layoutDirection] that's currently configured in the UI.
 *
 * Useful since the Painter from Compose doesn't know how to parse `autoMirrored` flags in SVGs.
 */
public fun Modifier.mirrorRtl(layoutDirection: LayoutDirection): Modifier {
    return this.scale(
        scaleX = if (layoutDirection == LayoutDirection.Ltr) 1f else -1f,
        scaleY = 1f
    )
}

/**
 * Wrapper around the [coil.compose.rememberAsyncImagePainter] that plugs in our [LocalStreamImageLoader] singleton
 * that can be used to customize all image loading requests, like adding headers, interceptors and similar.
 *
 * @param data The data to load as a painter.
 * @param placeholderPainter The painter used as a placeholder, while loading.
 * @param errorPainter The painter used when the request fails.
 * @param fallbackPainter The painter used as a fallback, in case the data is null.
 * @param onLoading Handler when the loading starts.
 * @param onSuccess Handler when the request is successful.
 * @param onError Handler when the request fails.
 * @param contentScale The scaling model to use for the image.
 * @param filterQuality The quality algorithm used when scaling the image.
 *
 * @return The [AsyncImagePainter] that remembers the request and the image that we want to show.
 */
@Composable
fun rememberSocialImagePainter(
    data: Any?,
    placeholderPainter: Painter? = null,
    errorPainter: Painter? = null,
    fallbackPainter: Painter? = errorPainter,
    onLoading: ((AsyncImagePainter.State.Loading) -> Unit)? = null,
    onSuccess: ((AsyncImagePainter.State.Success) -> Unit)? = null,
    onError: ((AsyncImagePainter.State.Error) -> Unit)? = null,
    contentScale: ContentScale = ContentScale.Crop,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    disableCache: Boolean = false
): AsyncImagePainter {
    val cachePolicy = if (disableCache) CachePolicy.DISABLED else CachePolicy.ENABLED
    return rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .diskCachePolicy(cachePolicy)
            .memoryCachePolicy(cachePolicy)
            .data(data)
            .build(),
        imageLoader = LocalStreamImageLoader.current,
        placeholder = placeholderPainter,
        error = errorPainter,
        fallback = fallbackPainter,
        contentScale = contentScale,
        onSuccess = onSuccess,
        onError = onError,
        onLoading = onLoading,
        filterQuality = filterQuality
    )
}

@Composable
fun rememberSocialImagePainter(
    data: Any?,
    size: Int,
    placeholderPainter: Painter? = null,
    errorPainter: Painter? = null,
    fallbackPainter: Painter? = errorPainter,
    onLoading: ((AsyncImagePainter.State.Loading) -> Unit)? = null,
    onSuccess: ((AsyncImagePainter.State.Success) -> Unit)? = null,
    onError: ((AsyncImagePainter.State.Error) -> Unit)? = null,
    contentScale: ContentScale = ContentScale.Crop,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    disableCache: Boolean = false
): AsyncImagePainter {
    val cachePolicy = if (disableCache) CachePolicy.DISABLED else CachePolicy.ENABLED
    return rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .size(size)
            .diskCachePolicy(cachePolicy)
            .memoryCachePolicy(cachePolicy)
            .data(data)
            .build(),
        imageLoader = LocalStreamImageLoader.current,
        placeholder = placeholderPainter,
        error = errorPainter,
        fallback = fallbackPainter,
        contentScale = contentScale,
        onSuccess = onSuccess,
        onError = onError,
        onLoading = onLoading,
        filterQuality = filterQuality
    )
}

