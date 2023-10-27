package dev.danperez.gradle.handlers

import com.android.build.api.dsl.CommonExtension
import dev.danperez.gradle.ScalerExtensionMarker
import dev.danperez.gradle.property
import dev.danperez.gradle.util.setDisallowChanges
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

@ScalerExtensionMarker
public abstract class AndroidFeaturesHandler @Inject constructor(
    objects: ObjectFactory,
) {
    private var androidExtension: CommonExtension<*, *, *, *, *>? = null
    internal val navigationEnabled: Property<Boolean> = objects.property<Boolean>().convention(false)
    internal val composeEnabled: Property<Boolean> = objects.property<Boolean>().convention(false)

    internal fun setAndroidExtension(androidExtension: CommonExtension<*, *, *, *, *>?) {
        this.androidExtension = androidExtension
    }

    fun compose() {
        composeEnabled.setDisallowChanges(true)
    }

    fun navigation() {
        navigationEnabled.setDisallowChanges(true)
    }

    fun computeNavigationConfig(): NavigationConfig {
        return NavigationConfig(
            enabled = navigationEnabled.get(),
            useUiLibrary = true,
        )
    }

    class NavigationConfig(
        val enabled: Boolean,
        val useUiLibrary: Boolean,
    )
}