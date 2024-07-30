package com.hoangkotlin.chatappsocial.core.common

sealed class SearchGraphNavigationOption {
    data object UserOnly : SearchGraphNavigationOption()
    data object Mix : SearchGraphNavigationOption()
}