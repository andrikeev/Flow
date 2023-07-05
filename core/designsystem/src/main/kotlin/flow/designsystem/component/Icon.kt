package flow.designsystem.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import flow.designsystem.R
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.drawables.Icon

@Composable
fun Icon(
    icon: Icon,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) = when (icon) {
    is Icon.DrawableResourceIcon -> Icon(
        modifier = modifier,
        painter = painterResource(id = icon.id),
        contentDescription = contentDescription,
        tint = tint,
    )

    is Icon.ImageVectorIcon -> Icon(
        modifier = modifier,
        imageVector = icon.imageVector,
        contentDescription = contentDescription,
        tint = tint,
    )
}

@Composable
fun ExpandCollapseIcon(
    expanded: Boolean,
    modifier: Modifier = Modifier,
) {
    val rotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f)
    Icon(
        modifier = modifier.rotate(rotation),
        icon = FlowIcons.Expand,
        contentDescription = stringResource(
            if (expanded) {
                R.string.designsystem_content_description_state_expanded
            } else {
                R.string.designsystem_content_description_state_collapsed
            },
        ),
    )
}

@Composable
fun DropDownExpandCollapseIcon(
    expanded: Boolean,
    modifier: Modifier = Modifier,
) {
    val rotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f)
    Icon(
        modifier = modifier.rotate(rotation),
        icon = FlowIcons.DropDownExpand,
        contentDescription = stringResource(
            if (expanded) {
                R.string.designsystem_content_description_state_expanded
            } else {
                R.string.designsystem_content_description_state_collapsed
            },
        ),
    )
}

@Composable
@NonRestartableComposable
fun SearchIcon() = Icon(
    icon = FlowIcons.Search,
    contentDescription = stringResource(R.string.designsystem_action_search),
)
