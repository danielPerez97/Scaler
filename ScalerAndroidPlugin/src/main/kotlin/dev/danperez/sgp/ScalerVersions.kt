package dev.danperez.sgp

import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionConstraint
import org.gradle.api.provider.Provider
import java.util.Optional

/**
 * A set of properties corresponding to *version* aliases in a [catalog]. The keys should be written
 * as they appear in the toml file.
 */
// TODO generate something to map these in the future? Or with reflection?
internal class ScalerVersions(val catalog: VersionCatalog) {
    val agp: String?
        get() = getOptionalValue("agp").orElse(null)

    val composeCompiler: String?
        get() = getOptionalValue("compose-compiler").orElse(null)

    val composeCompilerKotlinVersion: String?
        get() = getOptionalValue("compose-compiler-kotlinVersion").orElse(null)

    val compileSdkVersion: String?
        get() = getOptionalValue("scaler-compilersdkVersion").orElse(null)

    val minSdkVersion: String?
        get() = getOptionalValue("scaler-minsdkVersion").orElse(null)

    val targetSdkVersion: String?
        get() = getOptionalValue("scaler-targetsdkVersion").orElse(null)

    val detekt: String?
        get() = getOptionalValue("detekt").orElse(null)

    val gjf: String?
        get() = getOptionalValue("googleJavaFormat").orElse(null)

    val gson: String?
        get() = getOptionalValue("gson").orElse(null)

    val kotlin: String
        get() = getValue("kotlin")

    val ktlint: String?
        get() = getOptionalValue("ktlint").orElse(null)

    val ktfmt: String?
        get() = getOptionalValue("ktfmt").orElse(null)

    val sortDependencies: String?
        get() = getOptionalValue("sortDependencies").orElse(null)

    val objenesis: String?
        get() = getOptionalValue("objenesis").orElse(null)

    val jdk: Int
        get() = getValue("jdk").toInt()

    val jvmTarget: Int
        get() = getOptionalValue("jvmTarget").map { it.toInt() }.orElse(11)

    val composeJb: String?
        get() = getOptionalValue("compose-jb").orElse(null)

    val composeJbKotlinVersion: String?
        get() = getOptionalValue("compose-jb-kotlinVersion").orElse(null)

    val robolectric: String?
        get() = getOptionalValue("robolectric").orElse(null)

    val bundles = Bundles()

    inner class Bundles {
        val commonAnnotations: Optional<Provider<ExternalModuleDependencyBundle>> by lazy {
            catalog.findBundle("common-annotations")
        }
        val commonLint: Optional<Provider<ExternalModuleDependencyBundle>> by lazy {
            catalog.findBundle("common-lint")
        }
        val commonTest: Optional<Provider<ExternalModuleDependencyBundle>> by lazy {
            catalog.findBundle("common-test")
        }
    }

    internal fun getValue(key: String): String {
        return getOptionalValue(key).orElseThrow {
            IllegalStateException("No catalog version found for ${tomlKey(key)}")
        }
    }

    private fun getOptionalValue(key: String): Optional<String> {
        return catalog.findVersion(key).map(VersionConstraint::toString)
    }

    internal val boms: Set<Provider<MinimalExternalModuleDependency>> by lazy {
        catalog.libraryAliases
            .filter {
                // Library alias is as it appears in usage, not as it appears in the toml
                // So, "coroutines-bom" in the toml is "coroutines.bom" in usage
                it.endsWith(".bom")
            }
            .mapTo(LinkedHashSet()) { catalog.findLibrary(it).get() }
    }
}