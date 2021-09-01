package me.rutrackersearch.app.ui.platform

import androidx.compose.runtime.staticCompositionLocalOf

enum class PlatformType {
    MOBILE, TV
}

val LocalPlatformType = staticCompositionLocalOf<PlatformType> {
    error("no PlatformType provided")
}
