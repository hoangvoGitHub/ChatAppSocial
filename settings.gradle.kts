pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
//        maven { setUrl("https://jitpack.io") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
    }
}

rootProject.name = "ChatAppSocial"
//Enable root project accessor extension
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":app")
include(":core:auth")
include(":core:datastore")
include(":core:database")
include(":core:network")
include(":core:data")
include(":core:model")
include(":core:websocket")
include(":core:datastore-proto")
include(":core:common")
include(":feature:auth")
include(":core:ui")
include(":feature:home")
include(":core:service")
include(":core:websocket-naiksoftware")
include(":feature:search")
include(":feature:chat")
include(":core:chat-client")
include(":feature:profile")
include(":feature:friend")
include(":core:firebase-cloud-messaging")
include(":feature:channel-detail")
include(":core:notifications")
include(":core:worker:upload")
include(":core:offline")
include(":core:websocket-krossbow")
include(":feature:media-viewer")
