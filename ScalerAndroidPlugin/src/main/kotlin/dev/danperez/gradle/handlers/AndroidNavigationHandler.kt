package dev.danperez.gradle.handlers

import dev.danperez.gradle.ScalerVersionCatalog
import dev.danperez.gradle.property
import dev.danperez.gradle.util.setDisallowChanges
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

/**
 * Handler responsible for configuring the AndroidX Navigation Component in a library or app module.
 *
 * If enabled, it will apply the UI and Fragment libraries.
 */
public abstract class AndroidNavigationHandler @Inject constructor(
    private val scalerVersionCatalog: ScalerVersionCatalog,
    objects: ObjectFactory
)
{
    private val enabled: Property<Boolean> = objects.property<Boolean>().convention(false)

    internal fun enable() {
        enabled.setDisallowChanges(true)
    }

    internal fun configureProject(project: Project) {
        with(project) {

            if (enabled.get()) {
                // Fragment
                dependencies.add(
                    "implementation",
                    scalerVersionCatalog.navigationFragment
                )

                // UI
                dependencies.add(
                    "implementation",
                    scalerVersionCatalog.navigationUi
                )
            }
        }
    }
}