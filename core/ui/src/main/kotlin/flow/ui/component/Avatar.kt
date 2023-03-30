package flow.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import flow.designsystem.component.ThemePreviews
import flow.designsystem.theme.FlowTheme
import flow.ui.R

@Composable
fun Avatar(url: String?) = RemoteImage(
    modifier = Modifier
        .padding(vertical = 8.dp)
        .size(48.dp)
        .clip(CircleShape),
    src = url,
    placeholder = painterResource(R.drawable.ic_avatar),
    contentDescription = null,
    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
)

@ThemePreviews
@Composable
private fun Avatar_Preview() {
    FlowTheme { Surface { Avatar(null) } }
}
