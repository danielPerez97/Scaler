package dev.danperez.gradle

import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionConstraint
import org.gradle.api.provider.Provider
import org.gradle.plugin.use.PluginDependency

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
        get() = versionCatalog.findLibrary("anvil-annotations-optional").get()
    internal val daggerApi: Provider<MinimalExternalModuleDependency>
        get() = versionCatalog.findLibrary("dagger-api").get()

    internal val daggerCompiler: Provider<MinimalExternalModuleDependency>
        get() = versionCatalog.findLibrary("dagger-compiler").get()

    internal val fragment: Provider<MinimalExternalModuleDependency>
        get() = versionCatalog.findLibrary("fragment").get()

    internal val kotlinXSerializationJson: Provider<MinimalExternalModuleDependency>
        get() = versionCatalog.findLibrary("kotlinx-serialization-json").get()
    internal val navigationFragment: Provider<MinimalExternalModuleDependency>
        get() = versionCatalog.findLibrary("navigation-fragment").get()

    internal val navigationUi: Provider<MinimalExternalModuleDependency>
        get() = versionCatalog.findLibrary("navigation-ui").get()

    internal val okhttp: Provider<MinimalExternalModuleDependency>
        get() = versionCatalog.findLibrary("okhttp").get()

    internal val retainedActivity: Provider<MinimalExternalModuleDependency>
        get() = versionCatalog.findLibrary("retained-activity").get()

    internal val retainedFragment: Provider<MinimalExternalModuleDependency>
        get() = versionCatalog.findLibrary("retained-fragment").get()

    internal val retrofit: Provider<MinimalExternalModuleDependency>
        get() = versionCatalog.findLibrary("retrofit").get()

    internal val retrofitKotlinXSerialization: Provider<MinimalExternalModuleDependency>
        get() = versionCatalog.findLibrary("retrofit-ktx-converter").get()
}