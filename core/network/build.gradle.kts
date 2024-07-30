@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.chatappsocial.android.library)
    alias(libs.plugins.chatappsocial.android.hilt)

    id("kotlinx-serialization")
}

android {
    namespace = "com.hoangkotlin.chatappsocial.core.network"

}

dependencies {

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    api(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.okhttp.logging)
    implementation(projects.core.datastore)
    implementation(project(":core:common"))
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation("org.jetbrains.kotlin:kotlin-test:1.9.10")
    testImplementation(libs.junit4)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}