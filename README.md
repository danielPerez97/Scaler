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
            navigation() // Add's the AndroidX Navigation Component
            compose() // Configures Compose
            retained(RetainedType.Activity, RetainedType.Fragment) // Adds the retained library for Activities and Fragments
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

# Future
Scaler Gradle Plugin is heavily inspired by [slack-gradle-plugin][1], but it is my own spin on their concept that applies
the tools I use the most in my own projects. You are welcome to use Scaler Gradle Plugin, but I do not recommend it for 
production. If you are trying to build something similar for your own project or company, you are welcome to view the code for 
ideas on how you might achieve the same or reach out to me.

While suggestions and bug fixes are appreciated, I will not add any functionality I don't personally use or wouldn't be likely
to use in the future to avoid an unnecessary maintenance cost.

[1]: https://github.com/slackhq/slack-gradle-plugin