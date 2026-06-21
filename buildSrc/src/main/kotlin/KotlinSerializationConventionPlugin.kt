import flow.conventions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class KotlinSerializationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.plugin.serialization")
            }
            // In a Kotlin Multiplatform module the dependency belongs to the common
            // source set; in a plain JVM/Android module it goes to `implementation`.
            val configuration =
                if (pluginManager.hasPlugin("org.jetbrains.kotlin.multiplatform")) {
                    "commonMainImplementation"
                } else {
                    "implementation"
                }
            dependencies {
                add(configuration, libs.findLibrary("kotlinx.serialization.json").get())
            }
        }
    }
}
