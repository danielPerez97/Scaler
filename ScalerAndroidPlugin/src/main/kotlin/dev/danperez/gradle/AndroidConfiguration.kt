package dev.danperez.gradle

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion

fun CommonExtension<*, *, *, *, *>.applyAndroidConfiguration(sdkVersions: ScalerProperties.AndroidSdkProperties, jvmTargetVersion: Int) {
    val compileSdk = sdkVersions.compileSdk
    val javaVersion = JavaVersion.toVersion(jvmTargetVersion)

    compileSdkVersion("android-$compileSdk")
    defaultConfig {
        minSdk = sdkVersions.minSdk
    }

    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
}