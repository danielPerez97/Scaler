package dev.danperez.gradle

import com.android.build.api.dsl.CommonExtension
import com.squareup.anvil.plugin.AnvilExtension
import dev.danperez.gradle.compose.configureComposeCompiler
import dev.danperez.gradle.dependencies.ScalerDependencies
import dev.danperez.gradle.handlers.AndroidFeaturesHandler
import dev.danperez.gradle.handlers.DaggerHandler
import dev.danperez.gradle.util.setDisallowChanges
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

@DslMarker
public annotation class ScalerExtensionMarker

@ScalerExtensionMarker
public abstract class ScalerExtension @Inject constructor(
    objects: ObjectFactory,
    private val scalerProperties: ScalerProperties,
    val versionCatalog: VersionCatalog,
) {
    internal val androidHandler = objects.newInstance<AndroidHandler>(scalerProperties)
    internal val featuresHandler = objects.newInstance<FeaturesHandler>(
        scalerProperties,
        versionCatalog
    )
    private var androidExtension: CommonExtension<*, *, *, *, *>? = null
        set(value) {
            field = value
            androidHandler.setAndroidExtension(value)
            featuresHandler.setAndroidExtension(value)
        }


    public fun android(action: Action<AndroidHandler>) {
        action.execute(androidHandler)
    }

    public fun features(action: Action<FeaturesHandler>) {
        action.execute(featuresHandler)
    }

    internal fun applyTo(project: Project) {
        val logVerbose = scalerProperties.scalerExtensionVerbose

        project.afterEvaluate {
            featuresHandler.applyTo(this)

            // Dagger is configured first. If Dagger's compilers are present,
            // everything else needs to also use kapt!

            var kaptRequired = false
            var naptRequired = false
            val allowDaggerKsp = scalerProperties.allowNapt
            val allowAnvilKsp = allowDaggerKsp && scalerProperties.allowAnvilKsp
            val allowNapt = scalerProperties.allowNapt

            /** Marks this project as needing KSP code gen. */
            fun markKspNeeded(source: String) {
                if (logVerbose) {
                    logger.lifecycle(
                        """
                            [KSP Config]
                            project = $path
                            KSP source = $source
                            """
                            .trimIndent()
                    )
                }

                if (!isUsingKsp) {
                    // Apply KSP for them
                    pluginManager.apply("com.google.devtools.ksp")
                }
            }

            /** Marks this project as needing kapt code gen. */
            fun markKaptNeeded(source: String) {
                if (allowNapt) {
                    naptRequired = true
                    // Apply napt for them
                    pluginManager.apply("com.sergei-lapin.napt")
                } else {
                    kaptRequired = true
                    // Apply kapt for them
                    pluginManager.apply("org.jetbrains.kotlin.kapt")
                }
                if (logVerbose) {
                    logger.lifecycle(
                        """
                            [kapt/napt Config]
                            project = $path
                            source = $source
                            """
                            .trimIndent()
                    )
                }
            }

            fun aptConfiguration(): String {
                return if (isKotlin && !naptRequired) {
                    "kapt"
                } else {
                    "annotationProcessor"
                }
            }


            val daggerConfig = featuresHandler.daggerHandler.computeConfig()
            if (daggerConfig != null) {
                dependencies.add(
                    "implementation",
                    versionCatalog.findLibrary("dagger-api").get()
                )
//                    dependencies.add("implementation", ScalerDependencies.javaxInject)

                if (daggerConfig.runtimeOnly) {
                    dependencies.add(
                        "compileOnly",
                        versionCatalog.findLibrary("anvil-annotations").get()
                    )
                }

                if (logVerbose) {
                    logger.lifecycle(
                        """
                            [Dagger Config]
                            project = $path
                            daggerConfig = $daggerConfig
                            """
                            .trimIndent()
                    )
                }

                if (daggerConfig.enableAnvil) {
//                    it.dependencies.add("compileOnly", versionCatalog.findLibrary("anvil-annotations").get())
                    pluginManager.apply("com.squareup.anvil")
                    configure<AnvilExtension> {
                        generateDaggerFactories.setDisallowChanges(daggerConfig.anvilFactories)
                        generateDaggerFactoriesOnly.setDisallowChanges(daggerConfig.anvilFactoriesOnly)
                    }

//                    val runtimeProjects =
//                        scalerProperties.anvilRuntimeProjects?.splitToSequence(";")?.toSet()
//                            .orEmpty()
//
//                    for (runtimeProject in runtimeProjects) {
//                        it.dependencies.add("implementation", it.project(runtimeProject))
//                    }

//                    val generatorProjects = buildSet<Any> {
//                        addAll(
//                            scalerProperties.anvilGeneratorProjects
//                                ?.splitToSequence(";")
//                                ?.map(it::project)
//                                .orEmpty()
//                        )
//                        addAll(featuresHandler.daggerHandler.anvilGenerators)
//                    }
//                    for (generator in generatorProjects) {
//                        it.dependencies.add("anvil", generator)
//                    }
                }

                if (!daggerConfig.runtimeOnly && daggerConfig.useDaggerCompiler) {
                    if (allowDaggerKsp && (!daggerConfig.enableAnvil || allowAnvilKsp)) {
                        markKspNeeded("Dagger compiler")
                        dependencies.add("ksp", ScalerDependencies.Dagger.compiler)
                    } else {
                        markKaptNeeded("Dagger compiler")
                        dependencies.add(aptConfiguration(), ScalerDependencies.Dagger.compiler)
                    }
                }
            }

            // At the very end we check if kapt is enabled and disable anvil component merging if needed
            // https://github.com/square/anvil#incremental-kotlin-compilation-breaks-compiler-plugins
            if (
                kaptRequired &&
                daggerConfig?.enableAnvil == true &&
                !daggerConfig.alwaysEnableAnvilComponentMerging
            ) {
                configure<AnvilExtension> { disableComponentMerging.setDisallowChanges(true) }
            }
        }
    }
}

@ScalerExtensionMarker
public abstract class FeaturesHandler @Inject constructor(
    objects: ObjectFactory,
    private val scalerProperties: ScalerProperties,
    versionCatalog: VersionCatalog,
) {
    // Dagger Features
    internal val daggerHandler = objects.newInstance<DaggerHandler>()

    // Compose features
    internal val composeHandler = objects.newInstance<ComposeHandler>(
        scalerProperties,
        scalerProperties,
        versionCatalog
    )

    /**
     * Enables dagger for this project.
     *
     * @param action optional block for extra configuration, such as anvil generators or android.
     */
    public fun dagger(useDaggerCompiler: Boolean = false, action: Action<DaggerHandler>? = null) {
        daggerHandler.enabled.setDisallowChanges(true)
        daggerHandler.useDaggerCompiler.setDisallowChanges(useDaggerCompiler)
    }

    /** @see [ScalerExtension.androidExtension] */
    private var androidExtension: CommonExtension<*, *, *, *, *>? = null
        set(value) {
            field = value
            composeHandler.setAndroidExtension(value)
        }

    internal fun setAndroidExtension(androidExtension: CommonExtension<*, *, *, *, *>?) {
        this.androidExtension = androidExtension
    }

    internal fun applyTo(project: Project) {
        composeHandler.applyTo(project, scalerProperties)
    }
}

@ScalerExtensionMarker
@Suppress("UnnecessaryAbstractClass")
public abstract class ComposeHandler
@Inject
constructor(
    objects: ObjectFactory,
    globalScalerProperties: ScalerProperties,
    private val scalerProperties: ScalerProperties,
    versionCatalog: VersionCatalog
) {

    private val composeBundleAlias =
        globalScalerProperties.defaultComposeAndroidBundleAlias?.let { alias ->
            versionCatalog.findBundle(alias).orElse(null)
        }
    private val composeCompilerVersion by lazy {
        scalerProperties.versions.composeCompiler
            ?: error("Missing `compose-compiler` version in catalog")
    }
    internal val enabled = objects.property<Boolean>().convention(false)
    internal val multiplatform = objects.property<Boolean>().convention(false)

    /** @see [AndroidHandler.androidExtension] */
    private var androidExtension: CommonExtension<*, *, *, *, *>? = null

    internal fun setAndroidExtension(androidExtension: CommonExtension<*, *, *, *, *>?) {
        this.androidExtension = androidExtension
    }

    internal fun enable(multiplatform: Boolean) {
        enabled.setDisallowChanges(true)
        this.multiplatform.setDisallowChanges(multiplatform)
        if (!multiplatform) {
            val extension =
                checkNotNull(androidExtension) {
                    "ComposeHandler must be configured with an Android extension before it can be enabled. Did you apply the Android gradle plugin?"
                }
            extension.apply {
                buildFeatures { compose = true }
                composeOptions {
                    kotlinCompilerExtensionVersion = composeCompilerVersion
                    // Disable live literals by default
                    useLiveLiterals = scalerProperties.composeEnableLiveLiterals
                }
            }
        }
    }

    internal fun applyTo(project: Project, scalerProperties: ScalerProperties) {
        if (enabled.get()) {
            val isMultiplatform = multiplatform.get()
            if (isMultiplatform) {
                project.pluginManager.apply("org.jetbrains.compose")
            } else {
                composeBundleAlias?.let { project.dependencies.add("implementation", it) }
            }
            project.configureComposeCompiler(scalerProperties, isMultiplatform)
        }
    }
}

@ScalerExtensionMarker
public abstract class AndroidHandler @Inject constructor(
    objects: ObjectFactory,
    private val scalerProperties: ScalerProperties,
) {
    internal val libraryHandler = objects.newInstance<ScalerAndroidLibraryExtension>()
    internal val appHandler = objects.newInstance<ScalerAndroidAppExtension>()

    @Suppress("MemberVisibilityCanBePrivate")
    internal val featuresHandler = objects.newInstance<AndroidFeaturesHandler>()

    /** @see [ScalerExtension.androidExtension] */
    private var androidExtension: CommonExtension<*, *, *, *, *>? = null
        set(value) {
            field = value
            featuresHandler.setAndroidExtension(value)
        }

    internal fun setAndroidExtension(androidExtension: CommonExtension<*, *, *, *, *>?) {
        this.androidExtension = androidExtension
    }

    public fun features(action: Action<AndroidFeaturesHandler>) {
        action.execute(featuresHandler)
    }

    public fun app(applicationId: String, namespace: String, action: Action<ScalerAndroidAppExtension>? = null) {
        if(appHandler.applicationId.orNull != null) {
            throw GradleException("You cannot define both app{} and library{}.")
        }
        appHandler.applicationId.set(applicationId)
        appHandler.namespace.set(namespace)
        action?.execute(appHandler)
    }

    public fun library(namespace: String, action: Action<ScalerAndroidLibraryExtension>? = null) {
        if(appHandler.applicationId.orNull != null) {
            throw GradleException("You cannot define both app{} and library{}.")
        }
        libraryHandler.namespace.set(namespace)
        action?.execute(libraryHandler)
    }

    public fun app(applicationId: String, action: Action<ScalerAndroidAppExtension>) {
        if(appHandler.applicationId.orNull != null) {
            throw GradleException("You cannot define both app{} and library{}.")
        }
        appHandler.applicationId.set(applicationId)
        action.execute(appHandler)
    }

    internal fun applyTo(project: Project) {
        // Dirty but necessary since the extension isn't configured yet when we call this
        project.afterEvaluate {
//            if (featuresHandler.robolectric.getOrElse(false)) {
//                checkNotNull(slackProperties.versions.robolectric) {
//                    "Robolectric support requested in ${project.path} but no version was specified in the version catalog."
//                }
//                project.dependencies.apply {
//                    // For projects using robolectric, we want to make sure they include robolectric-core to
//                    // ensure robolectric uses our custom dependency resolver and config (which just need
//                    // to be on the classpath).
//                    add("testImplementation", SlackDependencies.Testing.Robolectric.annotations)
//                    add("testImplementation", SlackDependencies.Testing.Robolectric.robolectric)
//                    add("testImplementation", slackProperties.robolectricCoreProject)
//                }
//            }
        }
    }
}

@ScalerExtensionMarker
public abstract class ScalerAndroidLibraryExtension {
    internal abstract val namespace: Property<String>
}

@ScalerExtensionMarker
public abstract class ScalerAndroidAppExtension {
    internal abstract val applicationId: Property<String>
    internal abstract val namespace: Property<String>
}