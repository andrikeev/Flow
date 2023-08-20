import com.android.build.gradle.LibraryExtension
import flow.conventions.StaticAnalysisConventionPlugin
import flow.conventions.configureAndroidCommon
import flow.conventions.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply(StaticAnalysisConventionPlugin::class.java)
            }

            extensions.configure<LibraryExtension> {
                configureAndroidCommon(this)
                configureKotlinAndroid(this)
            }
        }
    }
}
