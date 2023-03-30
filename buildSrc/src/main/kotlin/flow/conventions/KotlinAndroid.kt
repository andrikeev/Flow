package flow.conventions

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion

/**
 * Configure base Kotlin with Android options
 */
internal fun configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *>,
) {
    commonExtension.apply {
        compileSdk = 33

        defaultConfig {
            minSdk = 21
            vectorDrawables {
                useSupportLibrary = true
            }
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }

        kotlinOptions {
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.coroutines.FlowPreview",
                "-opt-in=kotlin.Experimental",
            )
            jvmTarget = JavaVersion.VERSION_11.toString()
        }
    }
}
