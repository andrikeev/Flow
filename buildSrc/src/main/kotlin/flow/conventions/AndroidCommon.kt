package flow.conventions

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.Packaging

/**
 * Configure base Android properties.
 */
internal fun configureAndroidCommon(commonExtension: CommonExtension) {
    commonExtension.apply {
        compileSdk = 36

        defaultConfig.apply {
            minSdk = 21
            vectorDrawables {
                useSupportLibrary = true
            }
        }
    }
}

/**
 * Configure resource excludes shared by Android application and library modules.
 * [Packaging] is not exposed by [CommonExtension], so this is applied on the
 * concrete application/library extensions.
 */
internal fun Packaging.configureExcludes() {
    resources.excludes.addAll(
        listOf(
            "META-INF/LICENSE.md",
            "META-INF/LICENSE-notice.md",
        ),
    )
}
