package dev.danperez.gradle

import dev.danperez.gradle.handlers.AndroidHandler
import dev.danperez.gradle.handlers.FeaturesHandler
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

public abstract class ScalerExtension @Inject constructor(
    objects: ObjectFactory,
    scalerVersionCatalog: ScalerVersionCatalog,
) {
    internal val androidHandler = objects.newInstance<AndroidHandler>(scalerVersionCatalog)
    internal val featuresHandler = objects.newInstance<FeaturesHandler>()


    public fun android(action: Action<AndroidHandler>) {
        action.execute(androidHandler)
    }

    public fun features(action: Action<FeaturesHandler>) {
        action.execute(featuresHandler)
    }
}