package dev.danperez.gradle

import dev.danperez.gradle.handlers.AndroidHandler
import dev.danperez.gradle.handlers.FeaturesHandler
import org.gradle.api.Action
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

@DslMarker
public annotation class ScalerExtensionMarker

@ScalerExtensionMarker
public abstract class ScalerExtension @Inject constructor(
    objects: ObjectFactory,
    scalerProperties: ScalerProperties,
    versionCatalog: VersionCatalog,
) {
    internal val androidHandler = objects.newInstance<AndroidHandler>(scalerProperties)
    internal val featuresHandler = objects.newInstance<FeaturesHandler>(
        scalerProperties,
        versionCatalog
    )


    public fun android(action: Action<AndroidHandler>) {
        action.execute(androidHandler)
    }

    public fun features(action: Action<FeaturesHandler>) {
        action.execute(featuresHandler)
    }
}