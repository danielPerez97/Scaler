package dev.danperez.gradle.handlers

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import dev.danperez.gradle.ScalerExtensionMarker
import dev.danperez.gradle.configure
import dev.danperez.gradle.property
import dev.danperez.gradle.util.setDisallowChanges
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

@ScalerExtensionMarker
public abstract class AndroidFeaturesHandler @Inject constructor(
    objects: ObjectFactory,
) {
    private var androidExtension: CommonExtension<*, *, *, *, *>? = null
    internal val composeEnabled: Property<Boolean> = objects.property<Boolean>().convention(false)
    internal val navigationEnabled: Property<Boolean> = objects.property<Boolean>().convention(false)
    internal val retainedTypes: ListProperty<RetainedType> = objects.listProperty(RetainedType::class.java)

    internal fun setAndroidExtension(androidExtension: CommonExtension<*, *, *, *, *>?) {
        this.androidExtension = androidExtension
    }

    fun compose() {
        composeEnabled.setDisallowChanges(true)
    }

    fun navigation() {
        navigationEnabled.setDisallowChanges(true)
    }

    fun retained(vararg types: RetainedType) {
        retainedTypes.value(types.toList())
//        retainedTypes.setDisallowChanges(true)
    }

    internal fun computeNavigationConfig(): NavigationConfig {
        return NavigationConfig(
            enabled = navigationEnabled.get(),
            useUiLibrary = true,
        )
    }

    internal fun configureProject(project: Project, versionCatalog: VersionCatalog) {
        with(project) {
            // Compose
            if (composeEnabled.get()) {
                configure<LibraryExtension> {
                    logger.lifecycle("Compose enabled")
                    buildFeatures {
                        compose = true
                    }
                    composeOptions {
                        kotlinCompilerExtensionVersion =
                            versionCatalog.findVersion("composeCompiler").get().requiredVersion
                    }
                }
                dependencies.apply {
                    add("implementation", platform("androidx.compose:compose-bom:2023.03.00"))
                    add("implementation", "androidx.compose.ui:ui")
                    add("implementation", "androidx.compose.ui:ui-graphics")
                    add("implementation", "androidx.compose.ui:ui-tooling-preview")
                    add("implementation", "androidx.compose.material3:material3")
                }
            }

            // Navigation
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

            // Retained
            retainedTypes.get().forEach {
                when (it) {
                    RetainedType.Activity -> {
                        dependencies.add(
                            "implementation",
                            versionCatalog.findLibrary("retained-activity").get()
                        )
                    }

                    RetainedType.Fragment -> {
                        dependencies.add(
                            "implementation",
                            versionCatalog.findLibrary("retained-fragment").get()
                        )
                    }
                }
            }
        }
    }

    class NavigationConfig(
        val enabled: Boolean,
        val useUiLibrary: Boolean,
    )

    enum class RetainedType {
        Activity,
        Fragment
    }
}