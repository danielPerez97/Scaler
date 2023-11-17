package dev.danperez.sgp.handlers

import dev.danperez.sgp.ScalerVersionCatalog
import dev.danperez.sgp.property
import dev.danperez.sgp.util.setDisallowChanges
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

abstract class OkHttpHandler @Inject constructor(
    private val scalerVersionCatalog: ScalerVersionCatalog,
    objects: ObjectFactory
) {
    val useOkHttp = objects.property<Boolean>().convention(false)
    private val useLoggingInterceptor = objects.property<Boolean>().convention(false)
    private val loggingInterceptorConfigurationName = objects.property<String>()

    fun loggingInterceptor(configurationName: String = "implementation") {
        useLoggingInterceptor.setDisallowChanges(true)
        loggingInterceptorConfigurationName.setDisallowChanges(configurationName)
    }

    fun configureProject(project: Project) {
        if(useOkHttp.get())
        {
            with(project.dependencies) {

                // Add OkHttp
                add("implementation", scalerVersionCatalog.okhttp)

                // Add the logging-interceptor artifact
                if(useLoggingInterceptor.get()) {
                    add(loggingInterceptorConfigurationName.get(), scalerVersionCatalog.okhttpLoggingInterceptor)
                }
            }
        }
    }
}