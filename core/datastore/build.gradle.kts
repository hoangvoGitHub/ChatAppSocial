@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.chatappsocial.android.library)
    alias(libs.plugins.chatappsocial.android.hilt)
}

android {
    namespace = "com.hoangkotlin.chatappsocial.core.datastore"
}

dependencies {

    implementation(projects.core.common)
    implementation(projects.core.model)
    api(projects.core.datastoreProto)
    implementation(libs.protobuf.kotlin.lite)
    implementation(libs.androidx.dataStore.core)
    testImplementation(libs.junit4)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}