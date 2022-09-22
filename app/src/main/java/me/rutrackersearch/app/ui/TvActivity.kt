package me.rutrackersearch.app.ui

import dagger.hilt.android.AndroidEntryPoint
import me.rutrackersearch.app.ui.platform.PlatformType

@AndroidEntryPoint
class TvActivity : MainActivity() {
    override val deviceType: PlatformType = PlatformType.TV
}
