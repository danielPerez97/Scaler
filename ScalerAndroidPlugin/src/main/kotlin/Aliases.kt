import dev.danperez.gradle.AndroidHandler
import dev.danperez.gradle.ScalerAndroidLibraryExtension
import dev.danperez.gradle.ScalerExtension
import dev.danperez.gradle.findByType
import org.gradle.api.Action
import org.gradle.api.Project

//public fun Project.scaler(body: ScalerExtension.() -> Unit) {
//    extensions.findByType<ScalerExtension>()?.let(body) ?: error("Scaler extension not found.")
//}


//public fun Project.scalerAndroid(action: Action<AndroidHandler>) {
//    scaler { android(action) }
//}
//
//public fun Project.scalerAndroidLibrary(namespace: String, action: Action<ScalerAndroidLibraryExtension>) {
//    scaler {
//        android {
//            with(it) {
//                library(namespace, action)
//            }
//        }
//    }
//}