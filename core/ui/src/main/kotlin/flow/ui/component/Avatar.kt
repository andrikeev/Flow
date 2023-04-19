package flow.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import flow.designsystem.component.Surface
import flow.designsystem.component.ThemePreviews
import flow.designsystem.theme.AppTheme
import flow.designsystem.theme.FlowTheme
import flow.ui.R

@Composable
fun Avatar(url: String?) = RemoteImage(
    src = url,
    contentDescription = null,
    onLoading = { AvatarPlaceholder() },
    onSuccess = { painter ->
        Image(
            modifier = Modifier
                .padding(vertical = AppTheme.spaces.medium)
                .size(AppTheme.sizes.default)
                .clip(AppTheme.shapes.circle),
            painter = painter,
            contentDescription = null,
        )
    },
    onError = { AvatarPlaceholder() },
)

@Composable
private fun AvatarPlaceholder() {
    Image(
        modifier = Modifier
            .padding(vertical = AppTheme.spaces.medium)
            .size(AppTheme.sizes.default)
            .clip(AppTheme.shapes.circle),
        painter = painterResource(
            if (AppTheme.colors.isDark) {
                R.drawable.ic_avatar_dark
            } else {
                R.drawable.ic_avatar_light
            }
        ),
        contentDescription = null,
        colorFilter = ColorFilter.tint(color = AppTheme.colors.onPrimaryContainer),
    )
}


@ThemePreviews
@Composable
private fun Avatar_Preview() {
    FlowTheme { Surface { Avatar(null) } }
}
