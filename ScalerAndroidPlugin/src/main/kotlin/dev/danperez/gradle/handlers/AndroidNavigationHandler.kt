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
    private val enabled: Property<Boolean> = objects.property<Boolean>().convention(false)

    internal fun enable() {
        enabled.setDisallowChanges(true)
    }

    internal fun configureProject(project: Project, versionCatalog: VersionCatalog) {
        with(project) {

            if (enabled.get()) {
                // Fragment
                dependencies.add(
                    "implementation",
                    versionCatalog.findLibrary("navigation-fragment").get()
                )

                // UI
                dependencies.add(
                    "implementation",
                    versionCatalog.findLibrary("navigation-ui").get()
                )
            }
        }
    }
}