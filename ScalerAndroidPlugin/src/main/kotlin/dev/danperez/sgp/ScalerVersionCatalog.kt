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
        get() = versionCatalog.findVersion("composeCompiler").get()

    internal val scalerCompilerSdkVersion: VersionConstraint
        get() = versionCatalog.findVersion("scaler-compilersdkVersion").get()

    internal val scalerMinSdkVersion: VersionConstraint
        get() = versionCatalog.findVersion("scaler-minsdkVersion").get()

    internal val scalerTargetSdkVersion: VersionConstraint
        get() = versionCatalog.findVersion("scaler-targetsdkVersion").get()

    // Plugins
    internal val kotlinXSerialization: Provider<PluginDependency>
        get() = versionCatalog.findPlugin("kotlinx-serialization").get()

    // Libraries

    internal val anvilAnnotationsOptional: Provider<MinimalExternalModuleDependency>
        get() = versionCatalog.findLibraryOrError("anvil-annotations-optional")
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
}

private fun VersionCatalog.findLibraryOrError(alias: String): Provider<MinimalExternalModuleDependency> {
    return findLibrary(alias).getOrNull() ?: error("Please add $alias to your libs.versions.toml")
}