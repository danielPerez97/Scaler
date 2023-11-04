package dev.danperez.sgp.handlers.extensions

import org.gradle.api.provider.Property

public abstract class ScalerAndroidLibraryExtension {
    internal abstract val namespace: Property<String>
}