package flow.conventions

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

/**
 * Configure Compose-specific options
 */
internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    with(pluginManager) {
        apply("org.jetbrains.kotlin.plugin.compose")
    }
    commonExtension.apply {
        dependencies {
            val composeBom = libs.findLibrary("androidx-compose-bom").get()
            add("implementation", platform(composeBom))
            add("androidTestImplementation", platform(composeBom))
        }
    }
    tasks.withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
            optIn.addAll(
                "androidx.compose.animation.ExperimentalAnimationApi",
                "androidx.compose.foundation.ExperimentalFoundationApi",
                "androidx.compose.foundation.layout.ExperimentalLayoutApi",
                "androidx.compose.runtime.ExperimentalComposeApi",
                "androidx.compose.ui.ExperimentalComposeUiApi",
                "androidx.compose.ui.text.ExperimentalTextApi",
            )
        }
    }
}
