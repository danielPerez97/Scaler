import org.jetbrains.kotlin.samWithReceiver.gradle.SamWithReceiverExtension

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", libs.versions.kotlin.get()))
        classpath(kotlin("sam-with-receiver", libs.versions.kotlin.get()))
    }
}

plugins {
    id("com.android.application") version "8.1.2" apply false
    id("com.android.library") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id("com.google.devtools.ksp") version "1.9.10-1.0.13" apply false
    id("com.squareup.anvil") version "2.4.8" apply false
    id("org.jetbrains.kotlin.jvm") version "1.9.10" apply false
    id("app.cash.sqldelight") version "2.0.0" apply false
}

subprojects {
    apply(plugin = "kotlin-sam-with-receiver")
    configure<SamWithReceiverExtension> { annotation("org.gradle.api.HasImplicitReceiver") }
}