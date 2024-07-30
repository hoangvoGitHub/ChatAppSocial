@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.chatappsocial.android.application)
    alias(libs.plugins.chatappsocial.android.application.compose)
    alias(libs.plugins.chatappsocial.android.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.gms)
}

android {
    namespace = "com.hoangkotlin.chatappsocial"
}

dependencies {

    implementation(libs.io.reactive.rxjava2.rxjava)
    implementation(libs.io.reactive.rxjava2.rxandroid)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.compose.runtime.tracing)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil.kt.compose)
    implementation(libs.coil.kt.gif)
    implementation(libs.coil.kt.video)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.splashscreen)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation (libs.hilt.ext.work)
    implementation(libs.androidx.metrics.performance)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(platform(libs.androidx.compose.bom))

    implementation(projects.feature.auth)
    implementation(projects.feature.home)
    implementation(projects.feature.search)
    implementation(projects.feature.chat)
    implementation(projects.feature.friend)
    implementation(projects.feature.profile)
    implementation(projects.feature.channelDetail)
    implementation(projects.feature.mediaViewer)
    implementation(projects.core.ui)
    implementation(projects.core.data)
    implementation(projects.core.websocketNaiksoftware)
    implementation(projects.core.offline)
    implementation(projects.core.common)
    implementation(projects.core.notifications)
    implementation(projects.core.worker.upload)
    implementation(libs.androidx.dynamicanimation)


    androidTestImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.junit4)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.testManifest)
}