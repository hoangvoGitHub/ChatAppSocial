package com.hoangkotlin.chatappsocial.core.common

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val socialDispatcher: SocialDispatchers)

enum class SocialDispatchers {
    Default,
    IO,
    Main,
}
