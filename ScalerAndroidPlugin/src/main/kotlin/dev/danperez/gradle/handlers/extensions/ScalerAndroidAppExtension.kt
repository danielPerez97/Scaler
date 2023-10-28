package dev.danperez.gradle.handlers.extensions

import dev.danperez.gradle.ScalerExtensionMarker
import org.gradle.api.provider.Property

@ScalerExtensionMarker
public abstract class ScalerAndroidAppExtension {
    internal abstract val applicationId: Property<String>
    internal abstract val namespace: Property<String>
}