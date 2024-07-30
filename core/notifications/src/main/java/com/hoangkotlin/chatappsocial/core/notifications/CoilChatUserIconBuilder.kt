package com.hoangkotlin.chatappsocial.core.notifications

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.IconCompat
import coil.ImageLoader
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.hoangkotlin.chatappsocial.core.common.SocialDispatchers
import com.hoangkotlin.chatappsocial.core.common.di.DispatchersModule
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import okhttp3.Headers.Companion.toHeaders
import javax.inject.Inject

class CoilChatUserIconBuilder @Inject constructor(
    @ApplicationContext private val context: Context
) : ChatUserIconBuilder {
    override suspend fun buildUserIcon(user: SocialChatUser): IconCompat? =
        withContext(DispatchersModule.providesIODispatcher()) {
            user.image.takeUnless(String::isBlank)
                ?.let { url ->
                    val imageResult = ImageLoader(context)
                        .execute(
                            ImageRequest.Builder(context)
                                .headers(emptyMap<String, String>().toHeaders())
                                .data(url)
                                .transformations(CircleCropTransformation())
                                .build()
                        )
                    (imageResult.drawable as? BitmapDrawable)?.bitmap
                }?.let(IconCompat::createWithBitmap)
        }

}