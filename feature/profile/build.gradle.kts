@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.chatappsocial.android.feature)
    alias(libs.plugins.chatappsocial.android.library.compose)
}

android {
    namespace = "com.hoangkotlin.chatappsocial.feature.profile"

}

dependencies {
    implementation("com.vanniktech:android-image-cropper:4.5.0")
    implementation(projects.core.worker.upload)
    implementation(projects.core.offline)
    implementation(projects.core.websocketNaiksoftware)
    testImplementation(libs.junit4)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}