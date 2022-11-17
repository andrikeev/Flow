package flow.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import flow.designsystem.drawables.FlowIcons
import kotlinx.coroutines.launch

@Composable
fun ScrollBackFloatingActionButton(scrollState: LazyListState) {
    val coroutineScope = rememberCoroutineScope()
    val showButton by remember { derivedStateOf { scrollState.firstVisibleItemIndex > 1 } }
    AnimatedVisibility(
        visible = showButton,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut(),
    ) {
        FloatingActionButton(
            modifier = Modifier.size(40.dp),
            onClick = { coroutineScope.launch { scrollState.scrollToItem(0) } },
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.secondary,
            elevation = FloatingActionButtonDefaults.loweredElevation(),
        ) {
            Icon(
                imageVector = FlowIcons.ScrollToTop,
                contentDescription = null,
            )
        }
    }
}
