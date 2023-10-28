package dev.danperez.gradle.handlers

import dev.danperez.gradle.ScalerExtensionMarker
import dev.danperez.gradle.ScalerProperties
import dev.danperez.gradle.handlers.extensions.ScalerAndroidAppExtension
import dev.danperez.gradle.handlers.extensions.ScalerAndroidLibraryExtension
import dev.danperez.gradle.newInstance
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

@ScalerExtensionMarker
public abstract class AndroidHandler @Inject constructor(
    objects: ObjectFactory,
    private val scalerProperties: ScalerProperties,
) {
    internal val libraryHandler = objects.newInstance<ScalerAndroidLibraryExtension>()
    internal val appHandler = objects.newInstance<ScalerAndroidAppExtension>()

    @Suppress("MemberVisibilityCanBePrivate")
    internal val featuresHandler = objects.newInstance<AndroidFeaturesHandler>()

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