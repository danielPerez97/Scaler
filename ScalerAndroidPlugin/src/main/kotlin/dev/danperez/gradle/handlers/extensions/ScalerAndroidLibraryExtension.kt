package dev.danperez.gradle.handlers.extensions

import org.gradle.api.provider.Property

public abstract class ScalerAndroidLibraryExtension {
    internal abstract val namespace: Property<String>
}