package dev.danperez.sgp.handlers

import dev.danperez.sgp.ScalerVersionCatalog
import dev.danperez.sgp.handlers.extensions.ScalerAndroidAppExtension
import dev.danperez.sgp.handlers.extensions.ScalerAndroidLibraryExtension
import dev.danperez.sgp.newInstance
import dev.danperez.sgp.property
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
    internal val useMockFlavor = objects.property<Boolean>().convention(false)

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

//    public fun useMockFlavor() {
//        useMockFlavor.setDisallowChanges(true)
//    }
//
//    internal fun configureProject(extension: CommonExtension<*, *, *, *, *>, project: Project) {
//        // Configure Mock Flavor
//        with(extension) {
//            flavorDimensions += "offline"
//            if (useMockFlavor.get()) {
//                productFlavors {
//                    create("mock") {
//                        dimension = "offline"
//                        if(extension is ApplicationExtension) {
//                            with(this as ApplicationProductFlavor) {
//                                applicationIdSuffix = ".offline"
//                                versionNameSuffix = "-offline"
//                            }
//                        }
//                    }
//                    create("prod") {
//                        dimension = "offline"
//                    }
//                }
//            }
//        }
//
//        // Configure Features
//        featuresHandler.configureProject(extension, project)
//    }
}