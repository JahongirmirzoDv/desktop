pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    plugins {
        kotlin("jvm").version(extra["kotlin.version"] as String)
        id("org.jetbrains.compose").version(extra["compose.version"] as String)
        kotlin("multiplatform") version "1.8.20" // or kotlin("jvm") or any other kotlin plugin
        kotlin("plugin.serialization") version "1.8.20"
        id("com.google.gms.google-services")
    }
}

rootProject.name = "desktop"
