plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    alias(libs.plugins.bestPracticesPlugin)
}

gradlePlugin {
    plugins.create("scaler-base") {
        id = "com.scaler.gradle.base"
        implementationClass = "dev.danperez.gradle.plugins.ScalerLibraryPlugin"
    }
}

dependencies {
    compileOnly("com.android.tools.build:gradle:8.1.2")
    compileOnly(platform(libs.kotlin.bom))
    compileOnly(libs.kotlin.reflect)
    compileOnly(libs.agp)

    // Plugins
    compileOnly(libs.gradlePlugins.compose)
    compileOnly(libs.gradlePlugins.anvil)
    compileOnly(libs.gradlePlugins.kgp)
}