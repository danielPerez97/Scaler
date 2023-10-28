package dev.danperez.gradle.handlers

import com.android.build.api.dsl.CommonExtension
import dev.danperez.gradle.ScalerExtension
import dev.danperez.gradle.ScalerExtensionMarker
import dev.danperez.gradle.ScalerProperties
import dev.danperez.gradle.newInstance
import dev.danperez.gradle.util.setDisallowChanges
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

@ScalerExtensionMarker
public abstract class FeaturesHandler @Inject constructor(
    objects: ObjectFactory,
    private val scalerProperties: ScalerProperties,
    versionCatalog: VersionCatalog,
) {
    // Dagger Features
    internal val daggerHandler = objects.newInstance<DaggerHandler>()

    /**
     * Enables dagger for this project.
     *
     * @param action optional block for extra configuration, such as anvil generators or android.
     */
    public fun dagger(useDaggerCompiler: Boolean = false, action: Action<DaggerHandler>? = null) {
        daggerHandler.enabled.setDisallowChanges(true)
        daggerHandler.useDaggerCompiler.setDisallowChanges(useDaggerCompiler)
    }

    /** @see [ScalerExtension.androidExtension] */
    private var androidExtension: CommonExtension<*, *, *, *, *>? = null
        set(value) {
            field = value
        }

    internal fun setAndroidExtension(androidExtension: CommonExtension<*, *, *, *, *>?) {
        this.androidExtension = androidExtension
    }

    internal fun applyTo(project: Project) {
    }
}