import org.gradle.kotlin.dsl.projects

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.chatappsocial.android.feature)
    alias(libs.plugins.chatappsocial.android.library.compose)
}

android {
    namespace = "comhoangkotlin.chatappsocial.feature.friend"

}

dependencies {
    implementation(projects.core.websocketNaiksoftware)
    implementation(projects.core.offline)
    implementation(libs.accompanist.swiperefresh)
    testImplementation(libs.junit4)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}