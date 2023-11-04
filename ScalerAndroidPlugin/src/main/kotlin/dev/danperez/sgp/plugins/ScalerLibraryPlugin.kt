package dev.danperez.sgp.plugins

import dev.danperez.sgp.ScalerAndroidConfiguration
import dev.danperez.sgp.ScalerExtension
import dev.danperez.sgp.ScalerVersionCatalog
import dev.danperez.sgp.getVersionsCatalogOrNull
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * The entry point when the plugin is applied to a project.
 */
class ScalerLibraryPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val versionCatalog = project.getVersionsCatalogOrNull() ?: error("SGP requires use of version catalogs!")
        val scalerVersionCatalog = ScalerVersionCatalog(versionCatalog)
        val scalerExtension: ScalerExtension = project.extensions.create(
            "scaler",
            ScalerExtension::class.java,
            scalerVersionCatalog,
        )

        ScalerAndroidConfiguration(
            scalerVersionCatalog = scalerVersionCatalog,
            scalerExtension = scalerExtension,
        ).applyTo(project)
    }
}