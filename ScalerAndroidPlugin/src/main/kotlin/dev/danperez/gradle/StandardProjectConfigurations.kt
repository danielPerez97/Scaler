package dev.danperez.gradle

import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import dev.danperez.gradle.gradle.configureKotlinCompilationTask
import dev.danperez.gradle.util.setDisallowChanges
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.logging.Logger
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.configurationcache.extensions.serviceOf
import org.gradle.jvm.toolchain.JavaCompiler
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.internal.KaptGenerateStubsTask
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePlugin
import java.io.File


private const val LOG = "ScalerPlugin:"
private fun Logger.logWithTag(message: String) {
    debug("$LOG $message")
}

/**
 * Standard [Project] configurations. This class will be iterated on over time as we grow out our
 * bootstrapping options for Gradle subprojects.
 *
 * Principles:
 * - Avoid duplicating work and allocations. This runs at configuration time and should be as low
 *   overhead as possible.
 * - Do not resolve dependencies at configuration-time. Use appropriate callback APIs!
 * - Support Kotlin, Android, and Java projects.
 * - One-off configuration should be left to individual projects to declare.
 * - Use debug logging.
 */
@Suppress("TooManyFunctions")
internal class StandardProjectConfigurations(
    private val globalProperties: ScalerProperties,
    private val versionCatalog: VersionCatalog,
    private val scalerTools: ScalerTools,
) {

    fun applyTo(project: Project) {
        val scalerProperties = ScalerProperties(project)
        val scalerExtension =
            project.extensions.create(
                "scaler",
                ScalerExtension::class.java,
                globalProperties,
                scalerProperties,
                versionCatalog
            )
//        project.applyCommonConfigurations()
        val jdkVersion = project.jdkVersion()
        val jvmTargetVersion = project.jvmTargetVersion()
        project.applyJvmConfigurations(jdkVersion, jvmTargetVersion, scalerProperties, scalerExtension)
        project.configureKotlinProjects(jdkVersion, jvmTargetVersion, scalerProperties)
    }

    @Suppress("unused")
    private fun Project.javaCompilerFor(version: Int): Provider<JavaCompiler> {
        return extensions.getByType<JavaToolchainService>().compilerFor {
            languageVersion.setDisallowChanges(JavaLanguageVersion.of(version))
//            it.scalerTools.globalConfig.jvmVendor?.let(vendor::set)
        }
    }

    private fun Project.applyJvmConfigurations(
        jdkVersion: Int,
        jvmTargetVersion: Int,
        scalerProperties: ScalerProperties,
        scalerExtension: ScalerExtension,
    ) {
        configureAndroidProjects(scalerExtension, jvmTargetVersion, scalerProperties)
        configureJavaProject(jdkVersion, jvmTargetVersion, scalerProperties)
        scalerExtension.applyTo(this)
    }

    /** Adds common configuration for Java projects. */
    private fun Project.configureJavaProject(
        jdkVersion: Int,
        jvmTargetVersion: Int,
        scalerProperties: ScalerProperties,
    ) {
        plugins.withType(JavaBasePlugin::class.java).configureEach {
            project.configure<JavaPluginExtension> {
                val version = JavaVersion.toVersion(jvmTargetVersion)
                sourceCompatibility = version
                targetCompatibility = version
            }
            if (jdkVersion >= 9) {
                tasks.configureEach<JavaCompile> {
                    if (!isAndroid) {
                        logger.logWithTag("Configuring release option for $path")
                        options.release.setDisallowChanges(jvmTargetVersion)
                    }
                }
            }
        }

        val javaToolchains by lazy { project.serviceOf<JavaToolchainService>() }

        tasks.withType(JavaCompile::class.java).configureEach {
            // Keep parameter names, this is useful for annotation processors and static analysis tools
            options.compilerArgs.addAll(listOf("-parameters"))

            // Android is our lowest JVM target, so if we're an android project we'll always use that
            // source target.
            // TODO is this late enough to be safe?
            // TODO if we set it in android, does the config from this get safely ignored?
            // TODO re-enable in android at all after AGP 7.1
            if (!isAndroid) {
                val target = if (isAndroid) jvmTargetVersion else jdkVersion
                logger.logWithTag("Configuring toolchain for $path to $jdkVersion")
                // Can't use disallowChanges here because Gradle sets it again later for some reason
                javaCompiler.set(
                    javaToolchains.compilerFor {
                        languageVersion.setDisallowChanges(JavaLanguageVersion.of(target))
//                        it.scalerTools.globalConfig.jvmVendor?.let(vendor::set)
                    }
                )
            }
        }
    }

    private fun Project.configureAndroidProjects(
        scalerExtension: ScalerExtension,
        jvmTargetVersion: Int,
        scalerProperties: ScalerProperties,
    ) {
        val javaVersion = JavaVersion.toVersion(jvmTargetVersion)
        val sdkVersions = lazy { scalerProperties.requireAndroidSdkProperties() }

        val commonBaseExtensionConfig: BaseExtension.(applyTestOptions: Boolean) -> Unit = {applyTestOptions ->
            val compileSdk = sdkVersions.value.compileSdk
            compileSdkVersion("android-$compileSdk")
            defaultConfig {
                minSdk = sdkVersions.value.minSdk
            }

            compileOptions {
                sourceCompatibility = javaVersion
                targetCompatibility = javaVersion
            }
        }

        pluginManager.withPlugin("com.android.library") {
            logger.lifecycle("Configuring com.android.library")
            configure<LibraryAndroidComponentsExtension> {
                finalizeDsl {
                    if(namespace == null) {
                        check(scalerExtension.androidHandler.libraryHandler.namespace.isPresent) {
                            "namespace must be set"
                        }
                    }

                    it.namespace = scalerExtension.androidHandler.libraryHandler.namespace.get()
                }
            }
            configure<LibraryExtension> {
                commonBaseExtensionConfig(true)
            }

            scalerExtension.androidHandler.applyTo(project)
        }
    }

    @Suppress("LongMethod")
    private fun Project.configureKotlinProjects(
        jdkVersion: Int?,
        jvmTargetVersion: Int,
        scalerProperties: ScalerProperties,
    ) {
        val actualJvmTarget =
            if (jvmTargetVersion == 8) {
                "1.8"
            } else {
                jvmTargetVersion.toString()
            }

        plugins.withType(KotlinBasePlugin::class.java).configureEach {
            logger.lifecycle("Configuring KotlinBasePlugin")
            project.kotlinExtension.apply {
//                kotlinDaemonJvmArgs = scalerTools.globalConfig.kotlinDaemonArgs
                if (jdkVersion != null) {
                    jvmToolchain {
                        languageVersion.set(JavaLanguageVersion.of(jdkVersion))
//                        scalerTools.globalConfig.jvmVendor?.let(vendor::set)
                    }
                }
            }

            tasks.configureKotlinCompilationTask(includeKaptGenerateStubsTask = true) {
                // Don't add compiler args to KaptGenerateStubsTask because it inherits arguments from the
                // target compilation
                val isKaptGenerateStubsTask = this is KaptGenerateStubsTask

                compilerOptions {
                    progressiveMode.set(true)
                    // TODO probably just want to make these configurable in ScalerProperties
                    optIn.addAll(
                        "kotlin.contracts.ExperimentalContracts",
                        "kotlin.experimental.ExperimentalTypeInference",
                        "kotlin.ExperimentalStdlibApi",
                        "kotlin.time.ExperimentalTime",
                    )
//                    if (!slackProperties.allowWarnings && !name.contains("test", ignoreCase = true)) {
//                        allWarningsAsErrors.set(true)
//                    }
//                    if (!isKaptGenerateStubsTask) {
//                        freeCompilerArgs.addAll(kotlinCompilerArgs)
//                    }

                    if (this is KotlinJvmCompilerOptions) {
                        jvmTarget.set(JvmTarget.fromTarget(actualJvmTarget))
                        // Potentially useful for static analysis or annotation processors
                        javaParameters.set(true)
//                        freeCompilerArgs.addAll(KotlinBuildConfig.kotlinJvmCompilerArgs)

                        // Set the module name to a dashified version of the project path to ensure uniqueness
                        // in created .kotlin_module files
                        val pathProvider = project.provider { project.path.replace(":", "-") }
                        moduleName.set(pathProvider)
                    }
                }
            }

//            configureFreeKotlinCompilerArgs()
        }

        pluginManager.withPlugin("org.jetbrains.kotlin.android") {
            logger.lifecycle("Configuring org.jetbrains.kotlin.android")
            // Configure kotlin sources in Android projects
            configure<BaseExtension> {
                sourceSets.configureEach {
                    val nestedSourceDir = "src/$name/kotlin"
                    val dir = File(projectDir, nestedSourceDir)
                    if (dir.exists()) {
                        // Standard source set
                        // Only added if it exists to avoid potentially adding empty source dirs
                        java.srcDirs(layout.projectDirectory.dir(nestedSourceDir))
                    }
                }
            }
        }
    }
}