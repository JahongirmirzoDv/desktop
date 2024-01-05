import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven ("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation("io.ktor:ktor-client-cio-jvm:2.3.2")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.2")
//    implementation("io.ktor:ktor-client-[JAVA]:2.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0")
    implementation("com.google.code.gson:gson:2.8.9")

//    implementation("io.coil-kt:coil:2.5.0")
//    implementation("io.coil-kt:coil-compose:2.5.0")



    implementation(platform("io.github.jan-tennert.supabase:bom:2.0.1"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.github.jan-tennert.supabase:realtime-kt")
    implementation("io.github.jan-tennert.supabase:storage-kt")

    implementation("io.insert-koin:koin-core:3.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-io:0.1.16")

    implementation("org.jetbrains.compose.desktop:desktop:1.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")



//    implementation("dev.gitlive:firebase-auth:1.10.4")
//    implementation("dev.gitlive:firebase-database:1.10.4")
//    implementation("dev.gitlive:firebase-storage:1.10.4")
//    implementation("dev.gitlive:firebase-java-sdk:0.1.2")
//    implementation("com.google.android.gms:play-services-auth:20.7.0")
//    implementation("com.google.oauth-client:google-oauth-client:1.34.1")
    

//    implementation("com.google.firebase:firebase-admin:8.0.0")
//    implementation("com.google.auth:google-auth-library-oauth2-http:0.24.0")

//    implementation("org.jetbrains.compose.desktop:desktop-jvm:1.0.5")

//    implementation("org.slf4j:slf4j-simple:2.0.9")
//    implementation("org.slf4j:slf4j-api:2.0.9")
//    implementation(group = "org.projectlombok",name = "lombok", version = "1.18.30")
//    annotationProcessor("org.projectlombok:lombok:1.18.30")

    implementation("com.google.firebase:firebase-admin:8.0.0")
    implementation(project("log"))

}


compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            modules("java.sql")
            jvmArgs += listOf("-Xmx2G")
            args += listOf("-customArgument")
            targetFormats(TargetFormat.Exe,TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "desktop"
            packageVersion = "1.0.0"
            windows {
                packageVersion = "1.0.0"
                msiPackageVersion = "1.0.0"
                exePackageVersion = "1.0.0"
            }
            tasks.register("exe") {
                dependsOn("compileJava", "jar")
                // other configurations
            }
        }
    }
}
