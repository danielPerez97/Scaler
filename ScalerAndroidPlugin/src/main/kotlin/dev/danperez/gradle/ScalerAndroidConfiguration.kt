package dev.danperez.gradle

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.squareup.anvil.plugin.AnvilExtension
import dev.danperez.gradle.handlers.DaggerHandler
import org.gradle.api.GradleException
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

/**
 * Configuration for Android Projects used by [ScalerLibraryPlugin]
 *
 *
 * TODO: Document more of this
**/
internal class ScalerAndroidConfiguration(
    private val scalerProperties: ScalerProperties,
    private val versionCatalog: VersionCatalog,
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
                compileSdk = versionCatalog.findVersion("scaler-compilersdkVersion").get().requiredVersion.toInt()

                defaultConfig {
                    minSdk = versionCatalog.findVersion("scaler-minsdkVersion").get().requiredVersion.toInt()
                    targetSdk = versionCatalog.findVersion("scaler-targetsdkVersion").get().requiredVersion.toInt()
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
//                kotlinOptions {
//                    jvmTarget = "1.8"
//                }
                buildFeatures {
                    compose = true
                }
                composeOptions {
                    kotlinCompilerExtensionVersion = versionCatalog.findVersion("composeCompiler").get().requiredVersion
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
                applyAndroidVersions(scalerProperties.requireAndroidSdkProperties(), 8)
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

        // Dagger
        val daggerConfig = scalerExtension.featuresHandler.daggerHandler.computeConfig()
        project.configureDagger(daggerConfig, versionCatalog)

        // Android Features
        scalerExtension.androidHandler.featuresHandler.configureProject(this, project, versionCatalog)
    }

    private fun Project.configureDagger(
        daggerConfig: DaggerHandler.DaggerConfig?,
        versionCatalog: VersionCatalog,
    ) {

        logger.lifecycle("""
                [Dagger Configuration]
                $daggerConfig
            """.trimIndent())

        if(daggerConfig != null) {
            // Add Dagger Annotations
            dependencies.add("implementation", versionCatalog.findLibrary("dagger-api").get())

            if(daggerConfig.enableAnvil) {
                // Add Anvil and Anvil Optional Annotations
                pluginManager.apply("com.squareup.anvil")
                dependencies.add("implementation", versionCatalog.findLibrary("anvil-annotations-optional").get())

                if(daggerConfig.anvilFactories) {
                    configure<AnvilExtension> {
                        generateDaggerFactories.set(true)
                    }
                }
            }

            if (!daggerConfig.runtimeOnly && daggerConfig.useDaggerCompiler) {
                    pluginManager.apply("org.jetbrains.kotlin.kapt")
                    dependencies.add("kapt", versionCatalog.findLibrary("dagger-compiler").get())
            }

            // Add the scopes project
            dependencies.add("implementation", project(":scopes"))
        }
    }
}