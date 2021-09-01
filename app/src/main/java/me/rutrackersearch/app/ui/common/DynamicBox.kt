package me.rutrackersearch.app.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import me.rutrackersearch.app.ui.platform.LocalPlatformType
import me.rutrackersearch.app.ui.platform.PlatformType

@Stable
@Composable
fun DynamicBox(
    mobileContent: @Composable () -> Unit,
    tvContent: @Composable () -> Unit,
) {
    when (LocalPlatformType.current) {
        PlatformType.MOBILE -> mobileContent()
        PlatformType.TV -> tvContent()
    }
}
