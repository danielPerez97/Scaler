package dev.danperez.sgp.plugins

import dev.danperez.sgp.ScalerVersionCatalog
import dev.danperez.sgp.getVersionsCatalogOrNull
import dev.danperez.sgp.isRootProject
import org.gradle.api.Plugin
import org.gradle.api.Project
import kotlin.reflect.KVisibility
import kotlin.reflect.full.declaredMemberProperties

/**
 * Plugin that gets applied to the root Gradle project only. Currently, it only checks the version
 * catalog to see if there are any missing alias's.
 */
class ScalerBasePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        if(!project.isRootProject) {
            error("This plugin only works on the root project. Please only apply it in the projects root build.gradle/build.gradle.kts file.")
        }

        checkVersionCatalogForMissingModules(project)
    }

    /**
     * This function will check the version catalog for any missing alias's. It uses reflection to look up
     * every member of [ScalerVersionCatalog], then invokes it. If [ScalerVersionCatalog] does not have
     * an alias, it throws an exception on lookup which we catch here. Not every feature of Scaler will
     * break if an alias doesn't exist, but it's useful to have this check for projects that are interested
     * in having a fully-configured libs.versions.toml file so things don't begin to break.
     */
    private fun checkVersionCatalogForMissingModules(project: Project) {
        val versionCatalog = project.getVersionsCatalogOrNull() ?: error("SGP requires use of version catalogs!")
        val scalerVersionCatalog = ScalerVersionCatalog(versionCatalog)
        val properties = ScalerVersionCatalog::class.declaredMemberProperties
        properties.forEach {
            runCatching {
                if(it.visibility == KVisibility.PUBLIC || it.visibility == KVisibility.INTERNAL) {
                    it.invoke(scalerVersionCatalog)
                }
            }.onFailure {
                project.logger.quiet("An alias was not found in libs.versions.toml", it)
            }
        }
    }

}