package dev.danperez.sgp

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension

/**
 * We want the following conversions:
 * - `bugsnag-gradle` -> `bugsnagGradle`
 * - `bugsnag_gradle` -> `bugsnagGradle`
 * - `bugsnag.gradle` -> `bugsnag-gradle`
 *
 * This is because `bugsnag-gradle` is converted to a nesting `bugsnag.gradle` in version accessors
 * and `bugsnag.gradle` is converted to `bugsnagGradle`. We've historically done the opposite with
 * gradle property versions though and used -/_ as separators in a continuous word and `.` for
 * nesting.
 */
internal fun tomlKey(key: String): String =
    key.replace("-", "%").replace(".", "-").replace("%", ".").replace("_", ".").snakeToCamel()

internal fun String.snakeToCamel(upper: Boolean = false): String {
    return buildString {
        var capNext = upper
        for (c in this@snakeToCamel) {
            if (c == '_' || c == '-' || c == '.') {
                capNext = true
                continue
            } else {
                if (capNext) {
                    append(c.uppercaseChar())
                    capNext = false
                } else {
                    append(c)
                }
            }
        }
    }
}

internal fun Project.getVersionsCatalog(name: String = "libs"): VersionCatalog {
    return getVersionsCatalogOrNull(name) ?: error("No versions catalog found!")
}

internal fun Project.getVersionsCatalogOrNull(name: String = "libs"): VersionCatalog? {
    return try {
        project.extensions.getByType<VersionCatalogsExtension>().named(name)
    } catch (ignored: Exception) {
        null
    }
}