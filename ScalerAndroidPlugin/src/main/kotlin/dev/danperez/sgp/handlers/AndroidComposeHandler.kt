package dev.danperez.sgp.handlers

import com.android.build.api.dsl.CommonExtension
import dev.danperez.sgp.ScalerVersionCatalog
import dev.danperez.sgp.property
import dev.danperez.sgp.util.setDisallowChanges
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

/**
 * Handler responsible for Configuring Compose in an Android project.
 */
public abstract class AndroidComposeHandler @Inject constructor(
    objects: ObjectFactory,
    private val scalerVersionCatalog: ScalerVersionCatalog,
) {

    internal val enabled: Property<Boolean> = objects.property<Boolean>().convention(false)
    private val useActivityArtifact: Property<Boolean> = objects.property<Boolean>().convention(false)
    private val includeTestArtifactEnabled: Property<Boolean> = objects.property<Boolean>().convention(false)
    private val bomVersion: Property<String> = objects.property<String>().convention("2023.03.00")

    fun activity() {
        useActivityArtifact.setDisallowChanges(true)
    }

    fun includeTestArtifact() {
        includeTestArtifactEnabled.setDisallowChanges(true)
    }

    internal fun enable(billOfMaterialsVersion: String?) {
        billOfMaterialsVersion?.let { bomVersion.setDisallowChanges(billOfMaterialsVersion) }
        enabled.setDisallowChanges(true)
    }

    internal fun configureProject(
        extension: CommonExtension<*, *, *, *, *>,
        project: Project,
    ) {
        require(enabled.get()) { "Internal Error: Attempting to configure Compose when it was never explicitly enabled." }

        with(project) {
            pluginManager.apply(scalerVersionCatalog.composeCompilerPlugin.get().pluginId)
            with(extension) {
                // Compose
                buildFeatures {
                    compose = true
                }
                composeOptions {
                    kotlinCompilerExtensionVersion = scalerVersionCatalog.composeCompiler.requiredVersion
                }
                dependencies.apply {
                    add("implementation", platform("androidx.compose:compose-bom:${bomVersion.get()}"))
                    add("implementation", "androidx.compose.ui:ui")
                    add("implementation", "androidx.compose.ui:ui-graphics")
                    add("implementation", "androidx.compose.ui:ui-tooling-preview")
                    add("implementation", "androidx.compose.material3:material3")

                    add("debugImplementation", "androidx.compose.ui:ui-tooling")
                    add("debugImplementation", "androidx.compose.ui:ui-test-manifest")

                    if(useActivityArtifact.get()) {
                        add("implementation", scalerVersionCatalog.composeUiActivity)
                    }

                    if(includeTestArtifactEnabled.get()) {
                        add("androidTestImplementation", platform("androidx.compose:compose-bom:${bomVersion.get()}"))
                        add("androidTestImplementation", "androidx.compose.ui:ui-test-junit4")
                    }
                }
            }
        }
    }
}