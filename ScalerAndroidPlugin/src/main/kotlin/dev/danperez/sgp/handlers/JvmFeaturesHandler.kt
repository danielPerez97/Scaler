package dev.danperez.sgp.handlers

import dev.danperez.gradle.ScalerVersionCatalog
import dev.danperez.gradle.newInstance
import dev.danperez.gradle.property
import dev.danperez.gradle.util.setDisallowChanges
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

public abstract class JvmFeaturesHandler @Inject constructor(
    private val scalerVersionCatalog: ScalerVersionCatalog,
    objects: ObjectFactory
) {
    // Dagger Features
    internal val daggerHandler = objects.newInstance<DaggerHandler>(scalerVersionCatalog)
    private val retrofitHandler = objects.newInstance<RetrofitHandler>(scalerVersionCatalog)
    private val useOkHttp = objects.property<Boolean>().convention(false)
    private val useKotlinXSerialization = objects.property<Boolean>().convention(false)

    /**
     * Enables dagger for this project.
     *
     * @param action optional block for extra configuration, such as anvil generators or android.
     */
    fun dagger(useDaggerCompiler: Boolean = false, action: Action<DaggerHandler>? = null) {
        daggerHandler.enabled.setDisallowChanges(true)
        daggerHandler.useDaggerCompiler.setDisallowChanges(useDaggerCompiler)
    }

    fun kotlinXSerialization() {
        useKotlinXSerialization.setDisallowChanges(true)
    }

    /**
     * Adds OkHttp as a dependency
     *
     * @param applyBom Applies the Bill Of Materials OkHttp provides if consumers want to add additional dependencies not provided
     * by Scaler Gradle Plugin
     */
    fun okHttp() {
        useOkHttp.setDisallowChanges(true)
    }

    /**
     * Adds Retrofit as a dependency
     */
    fun retrofit(action: Action<RetrofitHandler>? = null) {
        retrofitHandler.useRetrofit.setDisallowChanges(true)
        action?.execute(retrofitHandler)
    }

    internal fun configureProject(project: Project) {

        with(project) {
            // Dagger
            daggerHandler.configureDagger(project)

            // KotlinX Serialization
            if(useKotlinXSerialization.get()) {
                // Apply the plugin
                pluginManager.apply(scalerVersionCatalog.kotlinXSerialization.get().pluginId)

                // Apply the JSON Library
                dependencies.add("implementation", scalerVersionCatalog.kotlinXSerializationJson)
            }

            // OkHttp
            if(useOkHttp.get()) {
                with(dependencies) {
                    // Add OkHttp
                    add("implementation", scalerVersionCatalog.okhttp)
                }
            }

            // Retrofit
            if(retrofitHandler.useRetrofit.get()) {
                retrofitHandler.configureProject(project)
            }
        }
    }
}