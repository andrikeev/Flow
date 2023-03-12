package me.rutrackersearch.app

import dagger.hilt.android.AndroidEntryPoint
import flow.designsystem.platform.PlatformType

@AndroidEntryPoint
class TvActivity : MainActivity() {
    override val deviceType: PlatformType = PlatformType.TV
}
