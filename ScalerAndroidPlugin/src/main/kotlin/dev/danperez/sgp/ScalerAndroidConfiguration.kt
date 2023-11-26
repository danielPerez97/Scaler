package dev.danperez.sgp

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.GradleException
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

/**
 * Configuration for Android Projects used by [dev.danperez.sgp.plugins.ScalerLibraryPlugin]
 *
 *
 * TODO: Document more of this
**/
internal class ScalerAndroidConfiguration(
    private val scalerVersionCatalog: ScalerVersionCatalog,
    private val scalerExtension: ScalerExtension,
) {
    fun applyTo(project: Project) {
        project.configureKotlin()

        val pluginManager = project.pluginManager
        if(pluginManager.hasPlugin("com.android.application") && pluginManager.hasPlugin("com.android.library")) {
            throw GradleException("Both the 'com.android.app' and 'com.android.library' cannot be applied. Pick one.")
        }

        if(pluginManager.hasPlugin("com.android.application")) {
            project.configureAndroidApp()
        }

        if(pluginManager.hasPlugin("com.android.library")) {
            project.configureAndroidLibrary()
        }
    }

    private fun Project.configureKotlin() {
        configure<KotlinAndroidProjectExtension> {
            compilerOptions {
                jvmToolchain(8)
            }
        }
    }

    private fun Project.configureAndroidApp() {
        pluginManager.withPlugin("com.android.application") {
            configure<ApplicationAndroidComponentsExtension> {
                finalizeDsl {
                    it.configureAndroidModule(this@configureAndroidApp)
                    it.defaultConfig.applicationId = scalerExtension.androidHandler.appHandler.applicationId.get()
                }
            }
            configure<BaseAppModuleExtension> {
                namespace = "dev.danperez.scaler"
                compileSdk = scalerVersionCatalog.scalerCompilerSdkVersion.requiredVersion.toInt()

                defaultConfig {
                    minSdk = scalerVersionCatalog.scalerMinSdkVersion.requiredVersion.toInt()
                    targetSdk = scalerVersionCatalog.scalerTargetSdkVersion.requiredVersion.toInt()
                    versionCode = 1
                    versionName = "1.0"

                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    vectorDrawables {
                        useSupportLibrary = true
                    }
                }

                buildTypes {
                    release {
                        isMinifyEnabled = false
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                    }
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_1_8
                    targetCompatibility = JavaVersion.VERSION_1_8
                }
                packaging {
                    resources {
                        excludes += "/META-INF/{AL2.0,LGPL2.1}"
                    }
                }
            }
        }
    }

    private fun Project.configureAndroidLibrary() {
        pluginManager.withPlugin("com.android.library") {
            configure<LibraryAndroidComponentsExtension> {
                finalizeDsl {
                    it.configureAndroidModule(this@configureAndroidLibrary)
                }
            }

            configure<LibraryExtension> {
                applyAndroidVersions(scalerVersionCatalog, 8)
            }
        }
    }

    private fun CommonExtension<*,*,*,*,*>.configureAndroidModule(project: Project) {
        // Namespace
        when(this) {
            is ApplicationExtension -> {
                project.logger.lifecycle("Configuring ApplicationExtension")
                require(scalerExtension.androidHandler.appHandler.namespace.isPresent) {
                    "namespace must be set"
                }
                namespace = scalerExtension.androidHandler.appHandler.namespace.get()
                project.logger.lifecycle("Namespace = $namespace")
            }
            is LibraryExtension -> {
                project.logger.lifecycle("Configuring LibraryExtension")
                require(scalerExtension.androidHandler.libraryHandler.namespace.isPresent) {
                    "namespace must be set"
                }
                namespace = scalerExtension.androidHandler.libraryHandler.namespace.get()
                project.logger.lifecycle("Namespace = $namespace")
            }
            else -> {
                project.logger.lifecycle("Configuring Unknown: $this")
            }
        }

        // Jvm Features
        scalerExtension.jvmFeaturesHandler.configureProject(project)

        // Android Features
        scalerExtension.androidHandler.featuresHandler.configureProject(this, project)
    }
}