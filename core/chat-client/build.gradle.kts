@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.chatappsocial.android.library)
    alias(libs.plugins.chatappsocial.android.hilt)

    id("kotlinx-serialization")
}

android {
    namespace = "com.hoangkotlin.chatappsocial.core.chat_client"

}

dependencies {
    implementation(libs.io.reactive.rxjava2.rxjava)
    implementation(libs.io.reactive.rxjava2.rxandroid)
    implementation(libs.naiksoftware.stomp.protocol.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(projects.core.common)
    implementation(projects.core.datastore)
    implementation(projects.core.notifications)
    implementation(projects.core.data)
    implementation(projects.core.model)
    api(projects.core.network)
    implementation(project(":core:websocket-naiksoftware"))
    implementation(project(":core:websocket-krossbow"))

    testImplementation(libs.junit4)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}