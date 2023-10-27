package dev.danperez.gradle.dependencies

public abstract class DependencySet: DependencyCollection {
    internal fun artifact(
        group: String,
        artifact: String? = null,
        gradleProperty: String? = null
    ): DependencyDelegate {
        return DependencyDelegate(
            group = group,
            artifact = artifact,
            gradleProperty = gradleProperty?.let { "${DependencyCollection.GRADLE_PROPERTY_PREFIX}$it" }
        )
    }
}