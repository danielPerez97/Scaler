package dev.danperez.sgp.gradle

import org.gradle.api.tasks.TaskContainer
import org.jetbrains.kotlin.gradle.internal.KaptGenerateStubsTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


/**
 * Configures [KotlinCompile] tasks with the given [action] but _ignores_ [KaptGenerateStubsTask]
 * types as they inherit arguments from standard tasks via
 * [KaptGenerateStubsTask.compileKotlinArgumentsContributor] and applying arguments to them could
 * result in duplicates.
 *
 * See
 * https://github.com/JetBrains/kotlin/blob/0e4e53786c1b0341befe8e71a5e6e0bc0e464370/libraries/tools/kotlin-gradle-plugin/src/common/kotlin/org/jetbrains/kotlin/gradle/internal/kapt/KaptGenerateStubsTask.kt#L111-L113
 */
internal fun TaskContainer.configureKotlinCompilationTask(
    includeKaptGenerateStubsTask: Boolean = false,
    action: KotlinCompilationTask<*>.() -> Unit
) {
    withType(KotlinCompilationTask::class.java)
        // Kapt stub gen is a special case because KGP sets it up to copy compiler args from the
        // standard kotlin compilation, which can lead to duplicates. SOOOO we skip configuration of
        // it here. Callers to this _can_ opt in to including it, but they must be explicit.
        .matching { includeKaptGenerateStubsTask || it !is KaptGenerateStubsTask }
        .configureEach { action() }
}
