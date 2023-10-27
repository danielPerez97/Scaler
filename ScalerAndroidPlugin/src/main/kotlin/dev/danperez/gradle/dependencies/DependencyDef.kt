package dev.danperez.gradle.dependencies

public data class DependencyDef(
    val group: String,
    val artifact: String,
    val comments: String? = null,
    val ext: String? = null,
    val gradleProperty: String,
    val isBomManaged: Boolean = false
) {
    val coordinates: Map<String, String> =
        mutableMapOf<String, String>().apply {
            put("group", group)
            put("name", artifact)
            ext?.let { put("ext", it) }
        }
    val identifier: String = "$group:$artifact"
    val extSuffix: String
        get() = ext?.let { "@$it" }.orEmpty()
}