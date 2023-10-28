package dev.danperez.gradle.handlers

import com.android.build.api.dsl.CommonExtension
import dev.danperez.gradle.ScalerVersionCatalog
import dev.danperez.gradle.newInstance
import dev.danperez.gradle.property
import dev.danperez.gradle.util.setDisallowChanges
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

/**
 * Handler for configuring different features in an Android project(app or library) including:
 * - Setting up Compose
 * - Getting the AndroidX Fragment Library
 * - Enabling Molecule
 * - Getting the AndroidX Navigation Component
 * - Getting the Retained Library
 *
 * This is for Android features only, not JVM features. Any feature that's only appropriate for Android
 * but not the JVM should go here.
 */
public abstract class AndroidFeaturesHandler @Inject constructor(
    private val scalerVersionCatalog: ScalerVersionCatalog,
    objects: ObjectFactory,
) {
    private val androidxFragmentEnabled: Property<Boolean> = objects.property<Boolean>().convention(false)
    private val retainedTypes: ListProperty<RetainedType> = objects.listProperty(RetainedType::class.java)
    private val navigationHandler = objects.newInstance<AndroidNavigationHandler>(scalerVersionCatalog)
    private val composeHandler = objects.newInstance<AndroidComposeHandler>(scalerVersionCatalog)
    private val moleculeEnabled = objects.property<Boolean>().convention(false)

    /**
     * Configures Compose in an Android App/Library.
     */
    fun compose(action: Action<AndroidComposeHandler>? = null) {
        composeHandler.enable()
        action?.execute(composeHandler)
    }

    /**
     * Adds the AndroidX Fragment as a dependency.
     */
    fun fragment() {
        androidxFragmentEnabled.setDisallowChanges(true)
    }

    /**
     * Adds Molecule.
     */
    fun molecule() {
        moleculeEnabled.setDisallowChanges(true)
    }

    /**
     * Adds the AndroidX Navigation Component as a dependency.
     */
    fun navigation(action: Action<AndroidNavigationHandler>? = null) {
        navigationHandler.enable()
        action?.execute(navigationHandler)
    }

    /**
     * Adds the Retained library but requires you to specify which artifacts you need by
     * supplying a [RetainedType] enum.
     */
    fun retained(vararg types: RetainedType) {
        retainedTypes.value(types.toList())
    }

    /**
     * Takes a project and configures this setup against a [com.android.build.api.dsl.LibraryExtension] or
     * [com.android.build.api.dsl.ApplicationExtension], which both extend from [com.android.build.api.dsl.CommonExtension].
     */
    internal fun configureProject(extension: CommonExtension<*,*,*,*,*>, project: Project) {
        // Compose
        if(composeHandler.enabled.get()) {
            composeHandler.configureProject(extension, project)
        }

        with(project) {

            // Fragment
            if(androidxFragmentEnabled.get()) {
                dependencies.add(
                    "implementation",
                    scalerVersionCatalog.fragment,
                )
            }

            // Molecule
            if(moleculeEnabled.get()) {
                pluginManager.apply("app.cash.molecule")
            }

            // Navigation
            navigationHandler.configureProject(project)

            // Retained
            retainedTypes.get().forEach {
                when (it) {
                    RetainedType.Activity -> {
                        dependencies.add(
                            "implementation",
                            scalerVersionCatalog.retainedActivity
                        )
                    }

                    RetainedType.Fragment -> {
                        dependencies.add(
                            "implementation",
                            scalerVersionCatalog.retainedFragment
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