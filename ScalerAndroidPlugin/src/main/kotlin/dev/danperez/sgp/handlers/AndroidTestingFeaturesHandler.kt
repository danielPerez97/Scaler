package dev.danperez.sgp.handlers

import com.android.build.api.dsl.CommonExtension
import dev.danperez.sgp.ScalerVersionCatalog
import dev.danperez.sgp.property
import dev.danperez.sgp.util.setDisallowChanges
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

abstract class AndroidTestingFeaturesHandler @Inject constructor(
    private val scalerVersionCatalog: ScalerVersionCatalog,
    objects: ObjectFactory,
) {

    private val useJunit4: Property<Boolean> = objects.property<Boolean>().convention(false)
    private val useEspresso: Property<Boolean> = objects.property<Boolean>().convention(false)
    private val useComposeTesting: Property<Boolean> = objects.property<Boolean>().convention(false)

    fun junit4() {
        useJunit4.setDisallowChanges(true)
    }

    fun espresso() {
        useEspresso.setDisallowChanges(true)
    }

    fun compose() {
        useEspresso.setDisallowChanges(true)
    }

    // TODO: Get this artifacts into [ScalerVersionCatalog]
    internal fun configureProject(extension: CommonExtension<*, *, *, *, *>, project: Project) {
        with(project.dependencies) {
            if(useJunit4.get()) {
                add("testImplementation", "junit:junit:4.13.2")
                add("androidTestImplementation", "androidx.test.ext:junit:1.1.5")
            }

            if(useEspresso.get()) {
                add("androidTestImplementation", "androidx.test.espresso:espresso-core:3.5.1")
            }

            if(useComposeTesting.get()) {
                add("implementation", platform("androidx.compose:compose-bom:2023.03.00"))
                add("androidTestImplementation", "androidx.compose.ui:ui-test-junit4")
            }
        }
    }
}