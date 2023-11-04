@file:OptIn(ExperimentalStdlibApi::class, ExperimentalStdlibApi::class,
    ExperimentalStdlibApi::class
)

package dev.danperez.sgp

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Action
import org.gradle.api.DomainObjectSet
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.provider.Property
import org.gradle.api.reflect.TypeOf
import org.gradle.api.tasks.TaskContainer
import kotlin.reflect.javaType
import kotlin.reflect.typeOf

private const val IS_ANDROID = "scaler.project.ext.isAndroid"
private const val IS_ANDROID_APPLICATION = "scaler.project.ext.isAndroidApplication"
private const val IS_ANDROID_LIBRARY = "scaler.project.ext.isAndroidLibrary"
private const val IS_USING_KSP = "scaler.project.ext.isUsingKsp"
private const val IS_KOTLIN = "scaler.project.ext.isKotlin"
private const val IS_KOTLIN_ANDROID = "scaler.project.ext.isKotlinAndroid"
private const val IS_KOTLIN_JVM = "scaler.project.ext.isKotlinJvm"

internal val Project.isRootProject: Boolean
    get() = rootProject === this

internal fun <T : Any> Project.getOrComputeExt(key: String, valueCalculator: () -> T): T {
    @Suppress("UNCHECKED_CAST")
    return (extensions.findByName(key) as? T)
        ?: run {
            val value = valueCalculator()
            extensions.add(key, value)
            return value
        }
}

internal inline fun <reified E : Any> ObjectFactory.domainObjectSet(): DomainObjectSet<E> {
    return domainObjectSet(E::class.java)
}

internal inline fun <reified T : Any> ObjectFactory.newInstance(vararg parameters: Any): T {
    return newInstance(T::class.java, *parameters)
}

internal inline fun <reified T : Any> ObjectFactory.property(): Property<T> {
    return property(T::class.java)
}

internal inline fun <reified T : Any> Project.configure(action: Action<T>) {
    extensions.getByType<T>().apply(action::execute)
}

@OptIn(ExperimentalStdlibApi::class)
internal inline fun <reified T> ExtensionContainer.findByType(): T? {
    // Gradle, Kotlin, and Java all have different notions of what a "type" is.
    // I'm sorry
    return findByType(TypeOf.typeOf(typeOf<T>().javaType))
}

internal inline fun <reified T : Task> TaskContainer.configureEach(noinline action: T.() -> Unit) {
    withType(T::class.java).configureEach(action)
}

internal val Project.isAndroidApplication: Boolean
    get() {
        return getOrComputeExt(IS_ANDROID_APPLICATION) { plugins.hasPlugin(AppPlugin::class.java) }
    }

internal val Project.isAndroidLibrary: Boolean
    get() {
        return getOrComputeExt(IS_ANDROID_LIBRARY) { plugins.hasPlugin(LibraryPlugin::class.java) }
    }

internal val Project.isAndroid: Boolean
    get() {
        return getOrComputeExt(IS_ANDROID) { isAndroidApplication || isAndroidLibrary }
    }

internal val Project.isKotlin: Boolean
    get() {
        return getOrComputeExt(IS_KOTLIN) { isKotlinAndroid || isKotlinJvm }
    }

internal val Project.isKotlinAndroid: Boolean
    get() {
        return getOrComputeExt(IS_KOTLIN_ANDROID) {
            project.pluginManager.hasPlugin("org.jetbrains.kotlin.android")
        }
    }

internal val Project.isKotlinJvm: Boolean
    get() {
        return getOrComputeExt(IS_KOTLIN_JVM) {
            project.pluginManager.hasPlugin("org.jetbrains.kotlin.jvm")
        }
    }

internal val Project.isUsingKsp: Boolean
    get() {
        return getOrComputeExt(IS_USING_KSP) {
            project.pluginManager.hasPlugin("com.google.devtools.ksp")
        }
    }

internal inline fun <reified T> ExtensionContainer.getByType(): T {
    // Gradle, Kotlin, and Java all have different notions of what a "type" is.
    // I'm sorry
    return getByType(TypeOf.typeOf(typeOf<T>().javaType))
}