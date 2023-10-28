package dev.danperez.gradle

import com.android.build.api.dsl.CommonExtension
import com.squareup.anvil.plugin.AnvilExtension
import dev.danperez.gradle.compose.configureComposeCompiler
import dev.danperez.gradle.dependencies.ScalerDependencies
import dev.danperez.gradle.handlers.AndroidFeaturesHandler
import dev.danperez.gradle.handlers.AndroidHandler
import dev.danperez.gradle.handlers.DaggerHandler
import dev.danperez.gradle.handlers.FeaturesHandler
import dev.danperez.gradle.util.setDisallowChanges
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
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