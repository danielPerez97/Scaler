package dev.danperez.gradle.dependencies


internal object ScalerDependencies: DependencySet()
{
    internal val javaxInject: Any by artifact("javax.inject", "javax.inject")

    object Anvil: DependencyGroup("com.squareup.anvil", "anvil") {
        internal val annotations by artifact()
        val compiler: Any by artifact()
    }

    internal object Dagger: DependencyGroup("com.google.dagger") {
        val compiler: Any by artifact("dagger-compiler")
        val dagger: Any by artifact()
    }
}