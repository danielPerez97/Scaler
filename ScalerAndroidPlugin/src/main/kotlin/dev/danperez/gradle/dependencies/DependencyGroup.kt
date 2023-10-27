package dev.danperez.gradle.dependencies

import java.util.Locale

public abstract class DependencyGroup(
    internal val group: String,
    gradleProperty: String? = null,
    internal val bomArtifact: String? = null,
): DependencyCollection {

    internal val groupGradleProperty by lazy {
        gradleProperty ?: this::class.simpleName!!.lowercase(Locale.US)
    }

    internal fun artifact(
        artifact: String? = null,
        groupOverride: String = group,
        gradleProperty: String? = null
    ): DependencyDelegate {
        val property = gradleProperty ?: groupGradleProperty
        return DependencyDelegate(
            groupOverride,
            artifact,
            gradleProperty = "${DependencyCollection.GRADLE_PROPERTY_PREFIX}$property",
            isBomManaged = bomArtifact != null
        )
    }
}