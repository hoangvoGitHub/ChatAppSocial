@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.chatappsocial.android.feature)
    alias(libs.plugins.chatappsocial.android.library.compose)
}

android {
    namespace = "com.hoangkotlin.feature.channel_detail"

}

dependencies {
    implementation(projects.core.websocketNaiksoftware)
    implementation(projects.core.offline)

}