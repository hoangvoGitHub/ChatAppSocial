@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.chatappsocial.android.library)
    alias(libs.plugins.chatappsocial.android.hilt)
}

android {
    namespace = "com.hoangvo.core.worker.upload"
}

dependencies {
    implementation("com.google.guava:guava:31.1-jre")

//    https://developer.android.com/training/dependency-injection/hilt-jetpack#workmanager
    ksp(libs.hilt.ext.compiler)
    implementation(libs.hilt.ext.work)
    implementation(projects.core.data)
    implementation(libs.androidx.work.ktx)
    implementation(projects.core.websocketNaiksoftware)
    implementation(projects.core.offline)

}