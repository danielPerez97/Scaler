package dev.danperez.gradle.handlers

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import dev.danperez.gradle.configure
import dev.danperez.gradle.property
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

public abstract class AndroidComposeHandler @Inject constructor(
    objects: ObjectFactory,
) {

    private val enabled: Property<Boolean> = objects.property<Boolean>().convention(false)

    internal fun enable() {
        enabled.set(true)
    }

    internal fun configureProject(extension: CommonExtension<*, *, *, *, *>, project: Project, versionCatalog: VersionCatalog) {
        with(project) {
            with(extension) {
                // Compose
                if (enabled.get()) {
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
                    }
                }
            }
        }
    }
}