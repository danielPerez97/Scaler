package dev.danperez.gradle.plugins

import dev.danperez.gradle.ScalerAndroidConfiguration
import dev.danperez.gradle.ScalerExtension
import dev.danperez.gradle.ScalerProperties
import dev.danperez.gradle.getVersionsCatalogOrNull
import org.gradle.api.Plugin
import org.gradle.api.Project

class ScalerLibraryPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        val versionCatalog = project.getVersionsCatalogOrNull() ?: error("SGP requires use of version catalogs!")
        val scalerProperties = ScalerProperties(project)
        val scalerExtension: ScalerExtension = project.extensions.create(
                "scaler",
                ScalerExtension::class.java,
                scalerProperties,
                versionCatalog
            )

        ScalerAndroidConfiguration(
            scalerProperties = scalerProperties,
            versionCatalog = versionCatalog,
            scalerExtension = scalerExtension,
        ).applyTo(project)
    }
}