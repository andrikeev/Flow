package flow.designsystem.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Scaffold as MaterialDesignScaffold

@Composable
fun Scaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) = MaterialDesignScaffold(
    modifier = modifier,
    topBar = topBar,
    floatingActionButton = floatingActionButton,
    containerColor = MaterialTheme.colorScheme.background,
    contentColor = MaterialTheme.colorScheme.onBackground,
    contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
    content = content,
)

@Composable
fun Scaffold(
    content: @Composable (PaddingValues) -> Unit,
    snackbarHost: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
) = MaterialDesignScaffold(
    content = content,
    bottomBar = bottomBar,
    snackbarHost = snackbarHost,
    containerColor = MaterialTheme.colorScheme.background,
    contentColor = MaterialTheme.colorScheme.onBackground,
    contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
)

@Composable
fun Scaffold(
    snackbarHost: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) = MaterialDesignScaffold(
    content = content,
    snackbarHost = snackbarHost,
    containerColor = MaterialTheme.colorScheme.background,
    contentColor = MaterialTheme.colorScheme.onBackground,
    contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
)
