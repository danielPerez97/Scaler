package dev.danperez.gradle

import dev.danperez.gradle.util.booleanProperty
import dev.danperez.gradle.util.getOrCreateExtra
import dev.danperez.gradle.util.intProperty
import dev.danperez.gradle.util.optionalStringProperty
import org.gradle.api.Project

class ScalerProperties private constructor(private val project: Project) {

    private fun intProperty(key: String, defaultValue: Int = -1): Int =
        project.intProperty(key, defaultValue = defaultValue)

    private fun booleanProperty(key: String, defaultValue: Boolean = false): Boolean =
        project.booleanProperty(key, defaultValue = defaultValue)

    private fun optionalStringProperty(key: String, defaultValue: String? = null): String? =
        project.optionalStringProperty(key, defaultValue = defaultValue)

    internal val versions: ScalerVersions by lazy {
        project.rootProject.getOrCreateExtra("scaler-versions") {
            ScalerVersions(project.rootProject.getVersionsCatalog())
        }
    }

    /**
     * Anvil generator projects that should always be included when Anvil is enabled.
     *
     * This should be semicolon-delimited Gradle project paths.
     */
    public val anvilGeneratorProjects: String?
        get() = optionalStringProperty("scaler.anvil.generatorProjects")

    /**
     * Anvil runtime projects that should always be included when Anvil is enabled.
     *
     * This should be semicolon-delimited Gradle project paths.
     */
    public val anvilRuntimeProjects: String?
        get() = optionalStringProperty("scaler.anvil.runtimeProjects")

    /** Log Scaler extension configuration state verbosely. */
    public val scalerExtensionVerbose: Boolean
        get() = booleanProperty("scaler.extension.verbose", true)

    /** Flag to enable/disable Napt. */
    public val allowNapt: Boolean
        get() = booleanProperty("scaler.allow-napt")

    /** Flag to enable/disable Anvil KSP. Requires [allowDaggerKsp]. */
    public val allowAnvilKsp: Boolean
        get() = booleanProperty("scaler.ksp.allow-anvil")

    /**
     * An alias name to a libs.versions.toml bundle for common Android Compose dependencies that
     * should be added to android projects with compose enabled
     */
    public val defaultComposeAndroidBundleAlias: String?
        get() = optionalStringProperty("scaler.compose.android.defaultBundleAlias")

    /**
     * Enables live literals. Note that they are disabled by default due to
     * https://issuetracker.google.com/issues/274207650 and
     * https://issuetracker.google.com/issues/274231394.
     */
    public val composeEnableLiveLiterals: Boolean
        get() = booleanProperty("scaler.compose.android.enableLiveLiterals", false)

    /**
     * If true, uses the AndroidX compose compiler [ScalerVersions.composeCompiler] for Compose
     * Multiplatform compilations rather than the Jetbrains one. This can be useful in testing where
     * AndroidX's compiler is farther ahead.
     */
    public val forceAndroidXComposeCompilerForComposeMultiplatform: Boolean
        get() = booleanProperty("sgp.compose.multiplatform.forceAndroidXComposeCompiler", false)

    /** The JDK version to use for compilations. */
    public val jdkVersion: Int
        get() = versions.jdk

    /** The JDK runtime to target for compilations. */
    public val jvmTarget: Int
        get() = versions.jvmTarget

    internal fun requireAndroidSdkProperties(): AndroidSdkProperties {
        val compileSdk = compileSdkVersion ?: error("scaler.compileSdkVersion not set")
        val minSdk = minSdkVersion?.toInt() ?: error("scaler.minSdkVersion not set")
        val targetSdk = targetSdkVersion?.toInt() ?: error("scaler.targetSdkVersion not set")
        return AndroidSdkProperties(compileSdk, minSdk, targetSdk)
    }

    data class AndroidSdkProperties(
        val compileSdk: String,
        val minSdk: Int,
        val targetSdk: Int
    )

    public val compileSdkVersion: String?
        get() = versions.compileSdkVersion

    private val minSdkVersion: String?
        get() = versions.minSdkVersion

    private val targetSdkVersion: String?
        get() = versions.targetSdkVersion


    public fun latestCompileSdkWithSources(defaultValue: Int): Int =
        intProperty("scaler.latestCompileSdkWithSources", defaultValue = defaultValue)
    public companion object {

        private const val CACHED_PROVIDER_EXT_NAME = "scaler.properties.provider"

        public operator fun invoke(project: Project): ScalerProperties {
            return project.getOrCreateExtra(CACHED_PROVIDER_EXT_NAME, ::ScalerProperties)
        }
    }
}