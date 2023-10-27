package dev.danperez.gradle

import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

internal class ScalerGradlePlugin: Plugin<Project>
{
    override fun apply(project: Project) {
        project.plugins.withType(LibraryPlugin::class.java) {

        }
    }

}