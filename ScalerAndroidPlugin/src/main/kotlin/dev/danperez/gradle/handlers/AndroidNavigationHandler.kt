package dev.danperez.gradle.handlers

import dev.danperez.gradle.ScalerExtensionMarker
import dev.danperez.gradle.property
import dev.danperez.gradle.util.setDisallowChanges
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

@ScalerExtensionMarker
public abstract class AndroidNavigationHandler @Inject constructor(
    objects: ObjectFactory
)
{
    private val navigationEnabled: Property<Boolean> = objects.property<Boolean>().convention(false)
    private val activity: Property<Boolean> = objects.property<Boolean>().convention(false)
    private val fragment: Property<Boolean> = objects.property<Boolean>().convention(false)

    fun fragment() {
        fragment.setDisallowChanges(true)
    }

    fun activity() {
        activity.setDisallowChanges(true)
    }

    internal fun configureProject(project: Project, versionCatalog: VersionCatalog) {
        with(project) {
            val navConfig = computeNavigationConfig()
            if (navConfig.enabled) {
                dependencies.add(
                    "implementation",
                    versionCatalog.findLibrary("navigation-fragment").get()
                )
                // Use navigation-ui
                if (navConfig.useUiLibrary) {
                    dependencies.add(
                        "implementation",
                        versionCatalog.findLibrary("navigation-fragment").get()
                    )
                }
            }
        }
    }

    private fun computeNavigationConfig(): NavigationConfig {
        return NavigationConfig(
            enabled = navigationEnabled.get(),
            useUiLibrary = true,
        )
    }

    private class NavigationConfig(
        val enabled: Boolean,
        val useUiLibrary: Boolean,
    )
}