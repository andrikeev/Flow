import org.gradle.api.Plugin
import org.gradle.api.Project

class KotlinKspConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.devtools.ksp")
            }
        }
    }
}
