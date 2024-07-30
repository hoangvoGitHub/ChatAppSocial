package com.hoangkotlin.chatappsocial.bubble_service

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

class SocialViewModelStoreOwner(
    override val viewModelStore: ViewModelStore
) : ViewModelStoreOwner