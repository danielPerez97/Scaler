package dev.danperez.sgp.handlers

import dev.danperez.sgp.ScalerVersionCatalog
import dev.danperez.sgp.property
import dev.danperez.sgp.util.setDisallowChanges
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

public abstract class RetrofitHandler @Inject constructor(
    private val scalerVersionCatalog: ScalerVersionCatalog,
    objects: ObjectFactory,
) {
    internal val useRetrofit = objects.property<Boolean>().convention(false)
    private val useKotlinXSerializationConverter = objects.property<Boolean>().convention(false)
    private val useScalarsConverter = objects.property<Boolean>().convention(false)

    /**
     * Adds the kotlinx.serialization converter as a dependency
     *
     * See: https://github.com/JakeWharton/retrofit2-kotlinx-serialization-converter
     */
    fun kotlinXSerializationConverter() {
        useKotlinXSerializationConverter.setDisallowChanges(true)
    }

    fun scalarsConverter() {
        useScalarsConverter.setDisallowChanges(true)
    }

    internal fun configureProject(project: Project) {
        with(project) {
            // Retrofit
            if (useRetrofit.get()) {
                dependencies.add("implementation", scalerVersionCatalog.retrofit)

                // KotlinX Serialization Converter
                if (useKotlinXSerializationConverter.get()) {
                    dependencies.add(
                        "implementation",
                        scalerVersionCatalog.retrofitKotlinXSerialization,
                    )
                }

                // Scalars Converter
                if(useScalarsConverter.get()) {
                    dependencies.add(
                        "implementation",
                        scalerVersionCatalog.retrofitConverterScalars,
                    )
                }
            }
        }
    }
}