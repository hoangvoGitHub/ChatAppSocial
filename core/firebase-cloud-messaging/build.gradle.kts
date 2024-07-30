@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.chatappsocial.android.library)
//    alias(libs.plugins.gms)
}

android {
    namespace = "com.hoangkotlin.chatappsocial.core.firebase_cloud_messaging"

}

dependencies {

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)

    testImplementation(libs.junit4)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}