plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    alias(libs.plugins.bestPracticesPlugin)
    alias(libs.plugins.mavenPublish)
}

gradlePlugin {
    plugins.create("scaler-base") {
        id = "dev.danperez.scaler"
        implementationClass = "dev.danperez.sgp.plugins.ScalerLibraryPlugin"
    }

    plugins.create("scaler-root") {
        id = "dev.danperez.scaler.base"
        implementationClass = "dev.danperez.sgp.plugins.ScalerBasePlugin"
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