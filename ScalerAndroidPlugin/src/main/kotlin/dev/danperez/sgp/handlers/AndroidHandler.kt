package dev.danperez.sgp.handlers

import dev.danperez.gradle.ScalerVersionCatalog
import dev.danperez.gradle.handlers.extensions.ScalerAndroidAppExtension
import dev.danperez.gradle.handlers.extensions.ScalerAndroidLibraryExtension
import dev.danperez.gradle.newInstance
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

/**
 * Handler for configuring a project with a 'com.android.library' or 'com.android.application'
 * module.
 *
 * For libraries, it's possible to use the `library(namespace = "my.namespace")` function,
 * and for an application, you would use `app(applicationId = "my.id", namespace = "my.namespace")`.
 *
 * This will set up a module with Scaler's base properties which are loaded from a `libs.versions.toml`
 * file.
 */
public abstract class AndroidHandler @Inject constructor(
    objects: ObjectFactory,
    scalerVersionCatalog: ScalerVersionCatalog,
) {
    internal val libraryHandler = objects.newInstance<ScalerAndroidLibraryExtension>()
    internal val appHandler = objects.newInstance<ScalerAndroidAppExtension>()

    @Suppress("MemberVisibilityCanBePrivate")
    internal val featuresHandler = objects.newInstance<AndroidFeaturesHandler>(scalerVersionCatalog)

    public fun app(
        applicationId: String,
        namespace: String,
        action: Action<ScalerAndroidAppExtension>? = null
    ) {
        if (appHandler.applicationId.orNull != null) {
            throw GradleException("You cannot define both app{} and library{}.")
        }
        appHandler.applicationId.set(applicationId)
        appHandler.namespace.set(namespace)
        action?.execute(appHandler)
    }

    public fun library(namespace: String, action: Action<ScalerAndroidLibraryExtension>? = null) {
        if (appHandler.applicationId.orNull != null) {
            throw GradleException("You cannot define both app{} and library{}.")
        }
        libraryHandler.namespace.set(namespace)
        action?.execute(libraryHandler)
    }

    public fun features(action: Action<AndroidFeaturesHandler>) {
        action.execute(featuresHandler)
    }
}