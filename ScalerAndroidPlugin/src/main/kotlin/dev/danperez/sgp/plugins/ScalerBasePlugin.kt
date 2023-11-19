package dev.danperez.sgp.plugins

import dev.danperez.sgp.ScalerVersionCatalog
import dev.danperez.sgp.isRootProject
import org.gradle.api.Plugin
import org.gradle.api.Project
import kotlin.reflect.full.memberProperties

class ScalerBasePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        if(!project.isRootProject) {
            error("This plugin only works on the root project. Please only apply it in the projects root build.gradle/build.gradle.kts file.")
        }

        val properties = ScalerVersionCatalog::class.memberProperties
        properties.forEach {
            try {
                project.logger.lifecycle("Checking ${it.name} in ScalerVersionCatalog")
            } catch (e: Exception) {
                project.logger.error("Library not found", e)
            }
        }
    }

}