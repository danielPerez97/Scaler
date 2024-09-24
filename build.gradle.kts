import org.jetbrains.kotlin.samWithReceiver.gradle.SamWithReceiverExtension

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", libs.versions.kotlin.get()))
        classpath(kotlin("sam-with-receiver", libs.versions.kotlin.get()))
    }
}

plugins {
    alias(libs.plugins.agp.application) apply false
    alias(libs.plugins.agp.library) apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id("com.google.devtools.ksp") version "1.9.10-1.0.13" apply false
    id("com.squareup.anvil") version "2.5.0-beta09" apply false
    id("org.jetbrains.kotlin.jvm") version "1.9.10" apply false
    id("app.cash.sqldelight") version "2.0.0" apply false
    alias(libs.plugins.compose.compiler) apply false
    `maven-publish`
}

subprojects {
    apply(plugin = "kotlin-sam-with-receiver")
    configure<SamWithReceiverExtension> { annotation("org.gradle.api.HasImplicitReceiver") }

    plugins.withId("com.vanniktech.maven.publish.base") {
        afterEvaluate {
            publishing {
                repositories {
                    maven {
                        url = uri("https://maven.pkg.github.com/danielPerez97/Scaler")

                        credentials {
                            username = System.getenv("GITHUB_ACTOR")
                            password = System.getenv("GITHUB_TOKEN")
                        }
                    }
                }
            }
        }
    }
}