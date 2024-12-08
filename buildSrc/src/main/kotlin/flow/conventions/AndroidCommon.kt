package flow.conventions

import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.internal.dsl.DefaultConfig

/**
 * Configure base Android properties.
 */
internal fun configureAndroidCommon(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    commonExtension.apply {
        compileSdk = 35

        (defaultConfig as DefaultConfig).apply {
            minSdk = 21
            targetSdk = 35
            vectorDrawables {
                useSupportLibrary = true
            }
        }
        packaging {
            resources.excludes.addAll(
                listOf(
                    "META-INF/LICENSE.md",
                    "META-INF/LICENSE-notice.md",
                )
            )
        }
    }
}
