@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.chatappsocial.android.library)
}

android {
    namespace = "com.hoangkotlin.chatappsocial.core.model"
}

dependencies {
    api(projects.core.common)
    implementation(libs.androidx.annotation.jvm)
}