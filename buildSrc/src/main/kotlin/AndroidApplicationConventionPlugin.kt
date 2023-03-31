import com.android.build.api.dsl.ApplicationExtension
import flow.conventions.configureAndroidCompose
import flow.conventions.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<ApplicationExtension> {
                defaultConfig.targetSdk = 33
                configureKotlinAndroid(this)
                configureAndroidCompose(this)
            }
        }
    }
}
