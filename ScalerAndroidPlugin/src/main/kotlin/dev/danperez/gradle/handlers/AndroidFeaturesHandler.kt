package dev.danperez.gradle.handlers

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import dev.danperez.gradle.ScalerExtensionMarker
import dev.danperez.gradle.configure
import dev.danperez.gradle.newInstance
import dev.danperez.gradle.property
import dev.danperez.gradle.util.setDisallowChanges
import org.gradle.api.Action
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
    private val androidxFragmentEnabled: Property<Boolean> = objects.property<Boolean>().convention(false)
    private val composeEnabled: Property<Boolean> = objects.property<Boolean>().convention(false)
    private val retainedTypes: ListProperty<RetainedType> = objects.listProperty(RetainedType::class.java)
    private val navigationHandler = objects.newInstance<AndroidNavigationHandler>()

    internal fun setAndroidExtension(androidExtension: CommonExtension<*, *, *, *, *>?) {
        this.androidExtension = androidExtension
    }

    fun compose() {
        composeEnabled.setDisallowChanges(true)
    }

    fun fragment() {
        androidxFragmentEnabled.setDisallowChanges(true)
    }

    fun navigation(action: Action<AndroidNavigationHandler>? = null) {
        navigationHandler.enable()
        action?.execute(navigationHandler)
    }

    fun retained(vararg types: RetainedType) {
        retainedTypes.value(types.toList())
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

            // Fragment
            if(androidxFragmentEnabled.get()) {
                dependencies.add(
                    "implementation",
                    versionCatalog.findLibrary("fragment").get()
                )
            }

            // Navigation
            navigationHandler.configureProject(project, versionCatalog)

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

    enum class RetainedType {
        Activity,
        Fragment,
    }
}