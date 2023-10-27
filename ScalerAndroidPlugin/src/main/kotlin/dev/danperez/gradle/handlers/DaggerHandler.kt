package dev.danperez.gradle.handlers

import dev.danperez.gradle.ScalerExtensionMarker
import dev.danperez.gradle.property
import dev.danperez.gradle.util.setDisallowChanges
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

@ScalerExtensionMarker
public abstract class DaggerHandler @Inject constructor(objects: ObjectFactory) {
    internal val enabled: Property<Boolean> = objects.property<Boolean>().convention(false)
    internal val useDaggerCompiler: Property<Boolean> = objects.property<Boolean>().convention(false)
    internal val disableAnvil: Property<Boolean> = objects.property<Boolean>().convention(false)
    internal val runtimeOnly: Property<Boolean> = objects.property<Boolean>().convention(false)
    internal val alwaysEnableAnvilComponentMerging: Property<Boolean> = objects.property<Boolean>().convention(false)

    public fun disableAnvil() {
        disableAnvil.setDisallowChanges(true)
    }

    internal fun computeConfig(): DaggerConfig? {
        if (!enabled.get()) return null
        val runtimeOnly = runtimeOnly.get()
        val enableAnvil = !runtimeOnly && !disableAnvil.get()
        var anvilFactories = true
        var anvilFactoriesOnly = false
        val useDaggerCompiler = useDaggerCompiler.get()
        val alwaysEnableAnvilComponentMerging =
            !runtimeOnly && alwaysEnableAnvilComponentMerging.get()

        if (useDaggerCompiler) {
            anvilFactories = false
            anvilFactoriesOnly = false
        }

        return DaggerConfig(
            runtimeOnly,
            enableAnvil,
            anvilFactories,
            anvilFactoriesOnly,
            useDaggerCompiler,
            alwaysEnableAnvilComponentMerging,
        )
    }

    internal data class DaggerConfig(
        val runtimeOnly: Boolean,
        val enableAnvil: Boolean,
        var anvilFactories: Boolean,
        var anvilFactoriesOnly: Boolean,
        val useDaggerCompiler: Boolean,
        val alwaysEnableAnvilComponentMerging: Boolean,
    )
}