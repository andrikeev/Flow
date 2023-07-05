package flow.conventions

import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class StaticAnalysisConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.diffplug.spotless")
            }

            extensions.configure<SpotlessExtension> {
                kotlin {
                    target("**/*.kt")
                    targetExclude("$buildDir/**/*.kt")
                    ktlint()
                }

                kotlinGradle {
                    target("*.gradle.kts")
                    ktlint()
                }
            }
        }
    }
}
