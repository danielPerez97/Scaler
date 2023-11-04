package dev.danperez.sgp

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion

/**
 * Applies the compileSdk and jvmTargetVersion to a [LibraryExtension] or [ApplicationExtension],
 * which both extend from [CommonExtension].
 */
fun CommonExtension<*, *, *, *, *>.applyAndroidVersions(
    scalerVersionCatalog: dev.danperez.sgp.ScalerVersionCatalog,
    jvmTargetVersion: Int,
    )
{
    val compileSdk = scalerVersionCatalog.scalerCompilerSdkVersion.requiredVersion
    val javaVersion = JavaVersion.toVersion(jvmTargetVersion)

    compileSdkVersion("android-$compileSdk")
    defaultConfig {
        minSdk = scalerVersionCatalog.scalerMinSdkVersion.requiredVersion.toInt()
    }

    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
}