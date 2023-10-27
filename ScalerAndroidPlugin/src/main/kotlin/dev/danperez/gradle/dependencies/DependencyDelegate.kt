package dev.danperez.gradle.dependencies

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

private val UNINITIALIZED_VALUE = Any()

internal data class DependencyDelegate(
    private val group: String,
    private val artifact: String?,
    private val comments: String? = null,
    private val ext: String? = null,
    private val gradleProperty: String? = null,
    private val isBomManaged: Boolean = false
) : ReadOnlyProperty<DependencyCollection, Any> {

    private var _definitionRef: Any = UNINITIALIZED_VALUE

    override fun getValue(thisRef: DependencyCollection, property: KProperty<*>): Any {
        return getOrCreateDef(property).coordinates
    }

    @Synchronized
    fun getOrCreateDef(property: KProperty<*>): DependencyDef {
        if (_definitionRef === UNINITIALIZED_VALUE) {
            val resolvedArtifact = artifact ?: property.name
            val gradleProperty = gradleProperty ?: computeVersionGradleProperty(resolvedArtifact)
            _definitionRef =
                DependencyDef(
                    group = group,
                    artifact = resolvedArtifact,
                    comments = comments,
                    ext = ext,
                    gradleProperty = gradleProperty,
                    isBomManaged = isBomManaged
                )
        }
        return _definitionRef as DependencyDef
    }

    private fun computeVersionGradleProperty(resolvedArtifact: String): String {
        return "${DependencyCollection.GRADLE_PROPERTY_PREFIX}$resolvedArtifact"
    }
}