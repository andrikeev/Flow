package flow.designsystem.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import flow.designsystem.platform.LocalPlatformType
import flow.designsystem.platform.PlatformType

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
