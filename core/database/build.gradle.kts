@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.chatappsocial.android.library)
    alias(libs.plugins.chatappsocial.android.hilt)
    alias(libs.plugins.chatappsocial.android.room)
    id("kotlinx-serialization")
}

android {
    namespace = "com.hoangkotlin.chatappsocial.core.database"
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
   }

dependencies {

    implementation(libs.kotlinx.serialization.json)
    implementation(projects.core.common)
    testImplementation(libs.junit4)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation("org.jetbrains.kotlin:kotlin-test:1.9.10")

    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}