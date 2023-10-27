plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    alias(libs.plugins.bestPracticesPlugin)
}

gradlePlugin {
//    plugins.create("scaler-root") {
//        id = "com.scaler.gradle.root"
//        implementationClass = "dev.danperez.gradle.ScalerRootPlugin"
//    }
    plugins.create("scaler-base") {
        id = "com.scaler.gradle.base"
        implementationClass = "dev.danperez.gradle.plugins.ScalerLibraryPlugin"
    }
//    plugins.create("apkVersioning") {
//        id = "com.scaler.gradle.apk-versioning"
//        implementationClass = "dev.danperez.gradle.ApkVersioningPlugin"
//    }
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