package flow.designsystem.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import flow.designsystem.theme.AppTheme
import androidx.compose.material3.Scaffold as MaterialDesignScaffold
import androidx.compose.material3.SnackbarHostState as MaterialSnackbarHostState

@Composable
@NonRestartableComposable
fun Scaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable (AppBarState) -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = { Divider() },
    content: @Composable (PaddingValues) -> Unit,
) {
    val scrollState = rememberScrollState()
    val appBarState = rememberPinnedAppBarState()
    LaunchedEffect(scrollState.canScrollUp) {
        appBarState.elevated = scrollState.canScrollUp
    }
    CompositionLocalProvider(LocalScrollState provides scrollState) {
        MaterialDesignScaffold(
            modifier = modifier.consumeWindowInsets(WindowInsets.navigationBars),
            topBar = { topBar(appBarState) },
            bottomBar = bottomBar,
            floatingActionButton = floatingActionButton,
            containerColor = AppTheme.colors.background,
            contentColor = AppTheme.colors.onBackground,
            contentWindowInsets = DefaultWindowInset,
            content = content,
        )
    }
}

@Composable
fun Scaffold(
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    val snackbarState = remember { MaterialSnackbarHostState() }
    val delegateSnackbarState = remember { DelegateSnackbarHostState(snackbarState) }
    CompositionLocalProvider(LocalSnackbarHostState provides delegateSnackbarState) {
        MaterialDesignScaffold(
            modifier = Modifier.consumeWindowInsets(WindowInsets.navigationBars),
            content = content,
            bottomBar = bottomBar,
            snackbarHost = { SnackbarHost(snackbarState) },
            containerColor = AppTheme.colors.background,
            contentColor = AppTheme.colors.onBackground,
            contentWindowInsets = DefaultWindowInset,
        )
    }
}

@Composable
fun Scaffold(content: @Composable (PaddingValues) -> Unit) {
    val snackbarState = remember { MaterialSnackbarHostState() }
    val delegateSnackbarState = remember { DelegateSnackbarHostState(snackbarState) }
    CompositionLocalProvider(LocalSnackbarHostState provides delegateSnackbarState) {
        MaterialDesignScaffold(
            content = content,
            snackbarHost = { SnackbarHost(snackbarState) },
            containerColor = AppTheme.colors.background,
            contentColor = AppTheme.colors.onBackground,
            contentWindowInsets = DefaultWindowInset,
        )
    }
}

internal val DefaultWindowInset = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp)
