package com.hoangkotlin.chatappsocial.core.common.constants

import android.content.Context
import com.hoangkotlin.chatappsocial.core.common.R

object DeeplinkUri {
    val Context.scheme
        get() = getString(R.string.uri_scheme)

    val Context.hostUri
        get() = getString(R.string.uri_host)




}