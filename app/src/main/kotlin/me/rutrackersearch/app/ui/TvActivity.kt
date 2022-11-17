package me.rutrackersearch.app.ui

import dagger.hilt.android.AndroidEntryPoint
import flow.designsystem.platform.PlatformType

@AndroidEntryPoint
class TvActivity : MainActivity() {
    override val deviceType: PlatformType = PlatformType.TV
}
