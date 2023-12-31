# Scaler Android Plugin
This project applies my common configurations for Android side projects, inspired by [slack-gradle-plugin][1].

# Features
* Provides a DSL for spinning up Android App and Library modules
```kotlin
// App Modules
plugins {
    id("com.android.application") // Scaler reacts to an the com.android.application plugin being being used, so you still must apply it
    id("org.jetbrains.kotlin.android")
    id("dev.danperez.scaler") // Use the Scaler Gradle Plugin
}

scaler {
    android {
        app(applicationId = "dev.danperez.app", namespace = "dev.danperez.app")
        features {
            compose() // Configure Jetpack Compose
            fragment() // Adds the AndroidX Fragment
            molecule() // Configures Molecule
            navigation() // Adds the AndroidX Navigation Component
            okhttp() // Adds OkHttp
            retained(RetainedType.Fragment, RetainedType.Activity) // Adds the Retained library
        }
    }
    features {
        dagger(useDaggerCompiler = true) // Enables Dagger + Anvil, disables factory generation when using the Dagger compiler
    }
}
```
```kotlin
// Library Modules
plugins {
    id("com.android.library") // Same as before - Scaler currently reacts to the com.android.library plugin being being used
    id("org.jetbrains.kotlin.android")
    id("dev.danperez.scaler")
}

scaler {
    android {
        library(namespace = "dev.danperez.library")
        features {
            compose()
        }
    }
    features {
        dagger()
    }
}
```

# Version Catalog
Scaler requires using a version catalog so it can look up artifacts. You supply the coordinates, and scaler will take care of the rest.
Currently Scaler needs the following definitions to work, defined in ScalerVersionCatalog.kt:
```toml
[versions]
# Required
composeCompiler = "X.X.X"
okhtp = "X.X.X"
scaler-compilersdkVersion = "X"
scaler-minsdkVersion = "X"
scaler-targetsdkVersion = "X"

# Optional, for this example only
anvil = "X.X.X"
dagger = "X.X.X"
fragment = "X.X.X"
retained = "X.X.X"
retrofit = "X.X.X"

# You can use 'version.ref' or 'version' in your module versions.
[libraries]
anvil-annotations = { module = "com.squareup.anvil:annotations", version.ref = "anvil" }
anvil-annotations-optional = { module = "com.squareup.anvil:annotations-optional", version.ref = "anvil" }
dagger-api = { module = "com.google.dagger:dagger", version.ref = "dagger" }
dagger-compiler = { module = "com.google.dagger:dagger-compiler", version.ref = "dagger" }
fragment = { module = "androidx.fragment:fragment-ktx", version.ref = "fragment"}
navigation-fragment = { module = "androidx.navigation:navigation-fragment-ktx", version.ref = "navigation"}
navigation-ui = { module = "androidx.navigation:navigation-ui-ktx", version.ref = "navigation"}
okhttp = { module = "com.squareup.okhttp3:okhttp", version.ref = "okhttp" }
retained-activity = { module = "dev.marcellogalhardo:retained-activity", version.ref = "retained" }
retained-fragment = { module = "dev.marcellogalhardo:retained-fragment", version.ref = "retained" }
retrofit = { module = "com.squareup.retrofit:retrofit", version.ref = "retrofit" }
retrofit-ktx-converter = { module = "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter", version = "1.0.0" }
```

# Plugins
Scaler provides two plugins, a base plugin for the root module and another plugin that provides the Scaler DSL. 

The base plugin analyzes your `libs.versions.toml`  file and identifies any missing definitions,
preventing version errors in your app and library modules. The other plugin is the DSL plugin.

To start using Scaler, put the following on your root `build.gradle.kts` file:
```kotlin
plugins {
    id("dev.danperez.scaler.base") version "0.24.0" // Base Plugin, only for the root project
    id("dev.danperez.scaler") version "0.24.0" apply false // DSL Plugin, for all the other projects
}
```
Then, in your non-root `build.gradle.kts` files, you can start using Scaler with just the following definition:
```kotlin
plugins {
    id("dev.danperez.scaler")
}
```

That's it. Gradle should print out warnings if you are missing any definitions in your `libs.versions.toml` and you
should have access to the DSL wherever the DSL plugin is applied.

# Installation
You can use this plugin by configuring Github Packages in Gradle with the following `maven {}` call in `settings.gradle.kts`:
```kotlin
pluginManagement {
    repositories {
        maven {
            name = "scaler-gradle-plugin"
            url = uri("https://maven.pkg.github.com/danielPerez97/Scaler")
            credentials {
                val keystoreFile = file("keystore.properties") // Do not check this file into version control since it will contain sensitive information
                val keystoreProperties = java.util.Properties()
                keystoreProperties.load(java.io.FileInputStream(keystoreFile))
                username = keystoreProperties.getProperty("githubUser") ?: error("No username")
                password = keystoreProperties.getProperty("githubToken") ?: error("No token")
            }
        }
    }
}
```
If you want to build and test locally, you can use the following `includeBuild()` in your projects `settings.gradle.kts` file in conjunction with the above:

```kotlin
// settings.gradle.kts

includeBuild("C:\\Path\\To\\Local\\Scaler\\Clone")
```

# Future
Scaler Gradle Plugin is heavily inspired by [slack-gradle-plugin][1], but it is my own spin on their concept that applies
the tools I use the most in my own projects. You are welcome to use Scaler Gradle Plugin, but I do not recommend it for 
production. If you are trying to build something similar for your own project or company, you are welcome to view the code for 
ideas on how you might achieve the same or reach out to me.

While suggestions and bug fixes are appreciated, I will not add any functionality I don't personally use or wouldn't be likely
to use in the future to avoid an unnecessary maintenance cost.

[1]: https://github.com/slackhq/slack-gradle-plugin
