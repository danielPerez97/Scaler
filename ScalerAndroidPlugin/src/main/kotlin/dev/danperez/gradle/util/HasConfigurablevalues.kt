package dev.danperez.gradle.util

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.SetProperty

/*
 * APIs adapted from `HasConfigurableValues.kt` in AGP. Copied for binary safety.
 */

internal fun ConfigurableFileCollection.fromDisallowChanges(vararg arg: Any) {
    from(*arg)
    disallowChanges()
}

internal fun <T> Property<T>.setDisallowChanges(value: T?) {
    set(value)
    disallowChanges()
}

internal fun <T> Property<T>.setDisallowChanges(value: Provider<out T>) {
    set(value)
    disallowChanges()
}

internal fun <T> ListProperty<T>.setDisallowChanges(value: Provider<out Iterable<T>>) {
    set(value)
    disallowChanges()
}

internal fun <T> ListProperty<T>.setDisallowChanges(value: Iterable<T>?) {
    set(value)
    disallowChanges()
}

internal fun <K, V> MapProperty<K, V>.setDisallowChanges(map: Provider<Map<K, V>>) {
    set(map)
    disallowChanges()
}

internal fun <K, V> MapProperty<K, V>.setDisallowChanges(map: Map<K, V>?) {
    set(map)
    disallowChanges()
}

internal fun <T> SetProperty<T>.setDisallowChanges(value: Provider<out Iterable<T>>) {
    set(value)
    disallowChanges()
}

internal fun <T> SetProperty<T>.setDisallowChanges(value: Iterable<T>?) {
    set(value)
    disallowChanges()
}

internal fun <T> ListProperty<T>.setDisallowChanges(
    value: Provider<out Iterable<T>>?,
    handleNullable: ListProperty<T>.() -> Unit
) {
    value?.let { set(value) } ?: handleNullable()
    disallowChanges()
}

internal fun <K, V> MapProperty<K, V>.setDisallowChanges(
    map: Provider<Map<K, V>>?,
    handleNullable: MapProperty<K, V>.() -> Unit
) {
    map?.let { set(map) } ?: handleNullable()
    disallowChanges()
}
