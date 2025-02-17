@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.chatappsocial.android.feature)
    alias(libs.plugins.chatappsocial.android.library.compose)
}

android {
    namespace = "com.hoangkotlin.chatappsocial.feature.media_viewer"
}

dependencies {

    implementation(projects.core.offline)
    implementation(projects.core.chatClient)
    testImplementation(libs.junit4)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}