package dev.danperez.sgp.handlers.extensions

import org.gradle.api.provider.Property

public abstract class ScalerAndroidAppExtension {
    internal abstract val applicationId: Property<String>
    internal abstract val namespace: Property<String>
}