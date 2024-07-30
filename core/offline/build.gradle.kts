@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.chatappsocial.android.library)
    alias(libs.plugins.chatappsocial.android.hilt)
}

android {
    namespace = "com.hoangkotlin.chatappsocial.core.offline"

}

dependencies {
    implementation("com.google.guava:guava:31.1-jre")

//    https://developer.android.com/training/dependency-injection/hilt-jetpack#workmanager
    ksp(libs.hilt.ext.compiler)
    implementation(libs.hilt.ext.work)
    implementation(libs.androidx.work.ktx)
    api(projects.core.chatClient)
    implementation(projects.core.common)
    implementation(projects.core.datastore)
    implementation(projects.core.notifications)
    implementation(projects.core.data)
    implementation(projects.core.model)
    implementation(project(":core:network"))
}