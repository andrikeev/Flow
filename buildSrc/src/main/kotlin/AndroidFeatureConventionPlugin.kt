import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("flow.android.library")
                apply("flow.android.hilt")
            }

            extensions.configure<LibraryExtension> {
                defaultConfig {
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }
            }

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            dependencies {
                add("implementation", project(":core:common"))
                add("implementation", project(":core:designsystem"))
                add("implementation", project(":core:domain"))
                add("implementation", project(":core:dispatchers"))
                add("implementation", project(":core:logger"))
                add("implementation", project(":core:models"))
                add("implementation", project(":core:navigation"))
                add("implementation", project(":core:testing"))
                add("implementation", project(":core:ui"))

                add("implementation", libs.findLibrary("androidx.lifecycle.viewModel.compose").get())
                add("implementation", libs.findLibrary("kotlinx.coroutines.android").get())

                add("implementation", libs.findBundle("orbit").get())

                add("testImplementation", project(":core:testing"))
                add("testImplementation", libs.findLibrary("orbit.test").get())
                add("androidTestImplementation", project(":core:testing"))
            }
        }
    }
}
