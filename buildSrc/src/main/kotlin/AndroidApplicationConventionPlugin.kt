import com.android.build.api.dsl.ApplicationExtension
import flow.conventions.StaticAnalysisConventionPlugin
import flow.conventions.configureAndroidCommon
import flow.conventions.configureAndroidCompose
import flow.conventions.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                apply("com.google.gms.google-services")
                apply("com.google.firebase.crashlytics")
                apply(StaticAnalysisConventionPlugin::class.java)
            }

            extensions.configure<ApplicationExtension> {
                configureAndroidCommon(this)
                configureKotlinAndroid(this)
                configureAndroidCompose(this)
            }

            tasks.withType<KotlinJvmCompile>().configureEach {
                compilerOptions {
                    freeCompilerArgs.addAll("-Xcontext-receivers")
                }
            }
        }
    }
}
