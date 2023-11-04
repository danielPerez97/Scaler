package dev.danperez.sgp

import dev.danperez.gradle.handlers.AndroidHandler
import dev.danperez.gradle.handlers.JvmFeaturesHandler
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

public abstract class ScalerExtension @Inject constructor(
    objects: ObjectFactory,
    scalerVersionCatalog: ScalerVersionCatalog,
) {
    internal val androidHandler = objects.newInstance<AndroidHandler>(scalerVersionCatalog)
    internal val jvmFeaturesHandler = objects.newInstance<JvmFeaturesHandler>(scalerVersionCatalog)


    public fun android(action: Action<AndroidHandler>) {
        action.execute(androidHandler)
    }

    public fun features(action: Action<JvmFeaturesHandler>) {
        action.execute(jvmFeaturesHandler)
    }
}