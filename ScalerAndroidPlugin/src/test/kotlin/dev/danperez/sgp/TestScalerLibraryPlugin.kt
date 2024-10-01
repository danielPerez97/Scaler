package dev.danperez.sgp

import dev.danperez.sgp.plugins.ScalerBasePlugin
import dev.danperez.sgp.plugins.ScalerLibraryPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TestScalerLibraryPlugin {

    @Test
    fun test() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("dev.danperez.sgp")

        assertTrue(project.plugins.getPlugin("dev.danperez.sgp.base") is ScalerBasePlugin)
        assertTrue(project.plugins.getPlugin("dev.danperez.sgp") is ScalerLibraryPlugin)
    }
}