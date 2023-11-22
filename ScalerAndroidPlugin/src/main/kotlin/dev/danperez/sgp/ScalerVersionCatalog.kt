package dev.danperez.sgp

import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionConstraint
import org.gradle.api.provider.Provider
import org.gradle.plugin.use.PluginDependency
import kotlin.jvm.optionals.getOrNull

class ScalerVersionCatalog(private val versionCatalog: VersionCatalog)
{

    // Versions
    internal val composeCompiler: VersionConstraint
        get() = versionCatalog.findVersionOrError("composeCompiler")

    internal val scalerCompilerSdkVersion: VersionConstraint
        get() = versionCatalog.findVersionOrError("scaler-compilersdkVersion")

    internal val scalerMinSdkVersion: VersionConstraint
        get() = versionCatalog.findVersionOrError("scaler-minsdkVersion")

    internal val scalerTargetSdkVersion: VersionConstraint
        get() = versionCatalog.findVersionOrError("scaler-targetsdkVersion")

    // Plugins
    internal val kotlinXSerialization: Provider<PluginDependency>
        get() = versionCatalog.findPluginOrError("kotlinx-serialization")

    // Libraries

    internal val anvilAnnotationsOptional: Provider<MinimalExternalModuleDependency>
        get() = versionCatalog.findLibraryOrError("anvil-annotations-optional")

    internal val composeUiActivity: Provider<MinimalExternalModuleDependency>
        get() = versionCatalog.findLibraryOrError("compose-ui-activity")

    internal val daggerApi: Provider<MinimalExternalModuleDependency>
        get() = versionCatalog.findLibraryOrError("dagger-api")

    internal val daggerCompiler: Provider<MinimalExternalModuleDependency>
        get() = versionCatalog.findLibraryOrError("dagger-compiler")

    internal val fragment: Provider<MinimalExternalModuleDependency>
        get() = versionCatalog.findLibraryOrError("fragment")

    internal val kotlinXSerializationJson: Provider<MinimalExternalModuleDependency>
        get() = versionCatalog.findLibraryOrError("kotlinx-serialization-json")
    internal val navigationFragment: Provider<MinimalExternalModuleDependency>
        get() = versionCatalog.findLibraryOrError("navigation-fragment")

    internal val navigationUi: Provider<MinimalExternalModuleDependency>
        get() = versionCatalog.findLibraryOrError("navigation-ui")

    internal val okhttp: Provider<MinimalExternalModuleDependency>
        get() = versionCatalog.findLibraryOrError("okhttp")

    internal val okhttpLoggingInterceptor: Provider<MinimalExternalModuleDependency>
        get() = versionCatalog.findLibraryOrError("okhttp-logging-interceptor")

    internal val retainedActivity: Provider<MinimalExternalModuleDependency>
        get() = versionCatalog.findLibraryOrError("retained-activity")

    internal val retainedFragment: Provider<MinimalExternalModuleDependency>
        get() = versionCatalog.findLibraryOrError("retained-fragment")

    internal val retrofit: Provider<MinimalExternalModuleDependency>
        get() = versionCatalog.findLibraryOrError("retrofit")

    internal val retrofitConverterScalars: Provider<MinimalExternalModuleDependency>
        get() = versionCatalog.findLibraryOrError("retrofit-converter-scalars")

    internal val retrofitKotlinXSerialization: Provider<MinimalExternalModuleDependency>
        get() = versionCatalog.findLibraryOrError("retrofit-ktx-converter")

    internal val timber: Provider<MinimalExternalModuleDependency>
        get() = versionCatalog.findLibraryOrError("timber")
}

private fun VersionCatalog.findVersionOrError(alias: String): VersionConstraint {
    return findVersion(alias).getOrNull() ?: versionError(alias)
}

private fun VersionCatalog.findPluginOrError(alias: String): Provider<PluginDependency> {
    return findPlugin(alias).getOrNull() ?: pluginError(alias)
}

private fun VersionCatalog.findLibraryOrError(alias: String): Provider<MinimalExternalModuleDependency> {
    return findLibrary(alias).getOrNull() ?: libraryError(alias)
}

inline fun versionError(alias: String): Nothing {
    throw AliasNotFoundException("'$alias' is required under [versions] in libs.versions.toml")
}

inline fun pluginError(alias: String): Nothing {
    throw AliasNotFoundException("'$alias' is required under [plugins] in libs.versions.toml")
}

inline fun libraryError(alias: String): Nothing {
    throw AliasNotFoundException("'$alias' is required under [libraries] in libs.versions.toml")
}

/**
 * Exception thrown when an alias is not defined in the root /gradle/libs.versions.toml.
 */
class AliasNotFoundException(message: String? = null, cause: Throwable? = null): Exception(message, cause)