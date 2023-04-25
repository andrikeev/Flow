package flow.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.AppTheme
import kotlinx.coroutines.launch

@Composable
fun ScrollBackFloatingActionButton(
    scrollState: LazyListState,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val showButton by remember { derivedStateOf { scrollState.firstVisibleItemIndex > 1 } }
    AnimatedVisibility(
        visible = showButton,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Surface(
            modifier = modifier.size(AppTheme.sizes.default),
            onClick = { coroutineScope.launch { scrollState.scrollToItem(0) } },
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
