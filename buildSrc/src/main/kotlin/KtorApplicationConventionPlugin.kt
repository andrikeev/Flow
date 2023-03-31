import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import java.io.File

class KtorApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("flow.kotlin.library")
                apply("org.gradle.application")
                apply("io.ktor.plugin")
            }
        }
    }
}
