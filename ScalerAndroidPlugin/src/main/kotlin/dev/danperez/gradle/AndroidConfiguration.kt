package dev.danperez.gradle

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion

/**
 * Applies the compileSdk and jvmTargetVersion to a [LibraryExtension] or [ApplicationExtension],
 * which both extend from [CommonExtension].
 */
fun CommonExtension<*, *, *, *, *>.applyAndroidVersions(sdkVersions: ScalerProperties.AndroidSdkProperties, jvmTargetVersion: Int) {
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