pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven ("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }


    plugins {
        kotlin("jvm").version(extra["kotlin.version"] as String)
        id("org.jetbrains.compose").version(extra["compose.version"] as String)
        kotlin("multiplatform") version "1.8.20" // or kotlin("jvm") or any other kotlin plugin
        kotlin("plugin.serialization") version "1.8.20"
        id("org.jetbrains.compose.desktop") version "1.0.1"
    }
}

rootProject.name = "desktop"
include("log")
