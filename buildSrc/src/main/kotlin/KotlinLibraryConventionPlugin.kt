import flow.conventions.StaticAnalysisConventionPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class KotlinLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("java-library")
                apply("org.jetbrains.kotlin.jvm")
                apply(StaticAnalysisConventionPlugin::class.java)
            }
        }
    }
}
