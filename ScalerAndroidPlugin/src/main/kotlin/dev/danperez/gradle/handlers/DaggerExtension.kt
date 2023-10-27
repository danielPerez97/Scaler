package dev.danperez.gradle.handlers

import dev.danperez.gradle.property
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

abstract class DaggerExtension @Inject constructor(objects: ObjectFactory) {
    public val compilerEnabled: Property<Boolean> = objects.property(Boolean::class.java)
        .convention(false)

    public val annotationsEnabled: Property<Boolean> = objects.property(Boolean::class.java)
        .convention(false)

    public val anvilEnabled: Property<Boolean> = objects.property(Boolean::class.java)
        .convention(false)
}