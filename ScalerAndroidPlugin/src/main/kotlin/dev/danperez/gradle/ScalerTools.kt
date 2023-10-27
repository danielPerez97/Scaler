package dev.danperez.gradle

import dev.danperez.gradle.ScalerTools.Companion.SERVICE_NAME
import dev.danperez.gradle.util.setDisallowChanges
import org.gradle.api.Project
import org.gradle.api.logging.Logging
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.api.services.BuildServiceRegistration

public abstract class ScalerTools : BuildService<ScalerTools.Parameters>
{

    public companion object {
        public const val SERVICE_NAME: String = "ScalerTools"

        internal fun register(
            project: Project,
        ): Provider<ScalerTools> {
            return project.gradle.sharedServices
                .registerIfAbsent(SERVICE_NAME, ScalerTools::class.java) {
                    parameters.offline.setDisallowChanges(project.gradle.startParameter.isOffline)
                    parameters.cleanRequested.setDisallowChanges(
                        project.gradle.startParameter.taskNames.any { it.equals("clean", ignoreCase = true) }
                    )
                    parameters.configurationCacheEnabled.setDisallowChanges(
                        project.provider { project.gradle.startParameter.isConfigurationCacheRequested }
                    )
                }//.apply { get().apply { globalConfig = GlobalConfig(project) } }
        }
    }

    public interface Parameters : BuildServiceParameters {
        public val offline: Property<Boolean>
        public val cleanRequested: Property<Boolean>
        public val configurationCacheEnabled: Property<Boolean>
    }
}

public fun Project.scalerTools(): ScalerTools {
    return scalerToolsProvider().get()
}

@Suppress("UNCHECKED_CAST")
public fun Project.scalerToolsProvider(): Provider<ScalerTools> {
    return (project.gradle.sharedServices.registrations.getByName(SERVICE_NAME)
            as BuildServiceRegistration<ScalerTools, ScalerTools.Parameters>)
        .service
}