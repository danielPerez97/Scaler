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


# Usage
Currently Scaler isn't published anywhere, but you can use it locally by using `includeBuild()` in your projects settings.gradle.kts file:

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