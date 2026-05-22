package flow.conventions

import com.android.build.api.dsl.CommonExtension

/**
 * Configure base Android properties.
 */
internal fun configureAndroidCommon(commonExtension: CommonExtension) {
    commonExtension.apply {
        compileSdk = 35

        defaultConfig.apply {
            minSdk = 21
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
