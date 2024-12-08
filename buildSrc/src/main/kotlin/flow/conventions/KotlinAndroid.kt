package flow.conventions

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion

/**
 * Configure base Kotlin with Android options
 */
internal fun configureKotlinAndroid(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    commonExtension.apply {
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }
}
