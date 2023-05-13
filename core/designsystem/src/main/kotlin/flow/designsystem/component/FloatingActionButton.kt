package flow.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.AppTheme

@Composable
fun ScrollBackFloatingActionButton(modifier: Modifier = Modifier) {
    val scrollState = LocalScrollState.current
    AnimatedVisibility(
        visible = scrollState.canScrollUp,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Surface(
            modifier = modifier.size(AppTheme.sizes.default),
            onClick = scrollState::scrollUp,
            color = AppTheme.colors.primaryContainer,
            shape = AppTheme.shapes.large,
            shadowElevation = AppTheme.elevations.medium,
            content = {
                Icon(
                    modifier = Modifier.padding(AppTheme.spaces.medium),
                    icon = FlowIcons.ScrollToTop,
                    contentDescription = null, //TODO: add contentDescription
                )
            },
        )
    }
}

@Composable
@NonRestartableComposable
fun AddCommentFloatingActionButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) = Surface(
    modifier = modifier.size(AppTheme.sizes.default),
    onClick = onClick,
    color = AppTheme.colors.primaryContainer,
    shape = AppTheme.shapes.large,
    shadowElevation = AppTheme.elevations.medium,
    content = {
        Icon(
            modifier = Modifier.padding(AppTheme.spaces.medium),
            icon = FlowIcons.Comment,
            contentDescription = null, //TODO: add contentDescription
        )
    },
)

@ThemePreviews
@Composable
private fun ScrollBackFloatingActionButtonPreview() {
    AddCommentFloatingActionButton {}
}
