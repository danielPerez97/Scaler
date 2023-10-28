package dev.danperez.gradle.handlers

import com.android.build.api.dsl.CommonExtension
import dev.danperez.gradle.property
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

public abstract class AndroidComposeHandler @Inject constructor(
    objects: ObjectFactory,
) {

    internal val enabled: Property<Boolean> = objects.property<Boolean>().convention(false)
    private val useActivityArtifact: Property<Boolean> = objects.property<Boolean>().convention(false)
    private val includeTestArtifactEnabled: Property<Boolean> = objects.property<Boolean>().convention(false)

    fun includeTestArtifact() {
        includeTestArtifactEnabled.set(true)
    }

    internal fun enable() {
        enabled.set(true)
    }

    internal fun configureProject(
        extension: CommonExtension<*, *, *, *, *>,
        project: Project,
        versionCatalog: VersionCatalog
    ) {
        require(enabled.get()) { "Internal Error: Attempting to configure Compose when it was never explicitly enabled." }

        with(project) {
            with(extension) {
                // Compose
                logger.lifecycle("Compose enabled")
                buildFeatures {
                    compose = true
                }
                composeOptions {
                    kotlinCompilerExtensionVersion =
                        versionCatalog.findVersion("composeCompiler").get().requiredVersion
                }
                dependencies.apply {
                    add("implementation", platform("androidx.compose:compose-bom:2023.03.00"))
                    add("implementation", "androidx.compose.ui:ui")
                    add("implementation", "androidx.compose.ui:ui-graphics")
                    add("implementation", "androidx.compose.ui:ui-tooling-preview")
                    add("implementation", "androidx.compose.material3:material3")

                    add("debugImplementation", "androidx.compose.ui:ui-tooling")
                    add("debugImplementation", "androidx.compose.ui:ui-test-manifest")

                    if(useActivityArtifact.get()) {
                        add("implementation", "androidx.activity:activity-compose:1.7.2")
                    }

                    if(includeTestArtifactEnabled.get()) {
                        add("androidTestImplementation", "androidx.compose.ui:ui-test-junit4")
                    }
                }
            }
        }
    }
}