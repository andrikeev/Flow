package flow.designsystem.platform

import androidx.compose.runtime.staticCompositionLocalOf

enum class PlatformType {
    MOBILE, TV
}

val LocalPlatformType = staticCompositionLocalOf<PlatformType> {
    error("no PlatformType provided")
}
