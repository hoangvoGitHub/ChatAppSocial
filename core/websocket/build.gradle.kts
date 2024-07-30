@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.chatappsocial.android.library)
    alias(libs.plugins.chatappsocial.android.hilt)

    id("kotlinx-serialization")
}

android {
    namespace = "com.hoangkotlin.chatappsocial.core.websocket"

}

dependencies {
    implementation(libs.krossbow.stomp.core)
    implementation(libs.krossbow.stomp.websocket.okhttp)
    implementation(libs.krossbow.stomp.kxserialization.json)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(projects.core.common)
    implementation(projects.core.datastore)
    implementation(projects.core.network)
    implementation(projects.core.data)
    implementation(projects.core.model)
    testImplementation(libs.junit4)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}