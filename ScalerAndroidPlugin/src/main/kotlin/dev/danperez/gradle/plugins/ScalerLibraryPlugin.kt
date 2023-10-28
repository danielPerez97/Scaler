package dev.danperez.gradle.plugins

import dev.danperez.gradle.ScalerAndroidConfiguration
import dev.danperez.gradle.ScalerExtension
import dev.danperez.gradle.ScalerProperties
import dev.danperez.gradle.ScalerVersionCatalog
import dev.danperez.gradle.getVersionsCatalogOrNull
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * The entry point when the plugin is applied to a project.
 */
class ScalerLibraryPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val versionCatalog = project.getVersionsCatalogOrNull() ?: error("SGP requires use of version catalogs!")
        val scalerVersionCatalog = ScalerVersionCatalog(versionCatalog)
        val scalerProperties = ScalerProperties(project)
        val scalerExtension: ScalerExtension = project.extensions.create(
            "scaler",
            ScalerExtension::class.java,
            scalerVersionCatalog,
        )

        ScalerAndroidConfiguration(
            scalerProperties = scalerProperties,
            scalerVersionCatalog = scalerVersionCatalog,
            scalerExtension = scalerExtension,
        ).applyTo(project)
    }
}