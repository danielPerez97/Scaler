package dev.danperez.sgp.handlers

import com.squareup.anvil.plugin.AnvilExtension
import dev.danperez.gradle.ScalerVersionCatalog
import dev.danperez.gradle.configure
import dev.danperez.gradle.property
import dev.danperez.gradle.util.setDisallowChanges
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject


public abstract class DaggerHandler @Inject constructor(
    private val scalerVersionCatalog: ScalerVersionCatalog,
    objects: ObjectFactory,
) {
    internal val enabled: Property<Boolean> = objects.property<Boolean>().convention(false)
    internal val useDaggerCompiler: Property<Boolean> = objects.property<Boolean>().convention(false)
    internal val disableAnvil: Property<Boolean> = objects.property<Boolean>().convention(false)
    internal val runtimeOnly: Property<Boolean> = objects.property<Boolean>().convention(false)
    internal val alwaysEnableAnvilComponentMerging: Property<Boolean> = objects.property<Boolean>().convention(false)

    public fun disableAnvil() {
        disableAnvil.setDisallowChanges(true)
    }

    private fun computeConfig(): DaggerConfig? {
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

    internal fun configureDagger(
        project: Project,
    ) {
        with(project) {
            val daggerConfig = computeConfig()

            logger.lifecycle(
                """
                [Dagger Configuration]
                $daggerConfig
            """.trimIndent()
            )

            if (daggerConfig != null) {
                // Add Dagger Annotations
                dependencies.add("implementation", scalerVersionCatalog.daggerApi)

                if (daggerConfig.enableAnvil) {
                    // Add Anvil and Anvil Optional Annotations
                    pluginManager.apply("com.squareup.anvil")
                    dependencies.add(
                        "implementation",
                        scalerVersionCatalog.anvilAnnotationsOptional
                    )

                    if (daggerConfig.anvilFactories) {
                        configure<AnvilExtension> {
                            generateDaggerFactories.set(true)
                        }
                    }
                }

                if (!daggerConfig.runtimeOnly && daggerConfig.useDaggerCompiler) {
                    pluginManager.apply("org.jetbrains.kotlin.kapt")
                    dependencies.add("kapt", scalerVersionCatalog.daggerCompiler)
                }
            }
        }
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