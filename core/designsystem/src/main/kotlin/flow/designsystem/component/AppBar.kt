package flow.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import flow.designsystem.theme.Elevation

@Composable
fun AppBar(
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    title: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    appBarState: AppBarState = rememberAppBarState(),
) {
    AppBarContainer(
        modifier = modifier,
        appBarState = appBarState,
    ) {
        FlowTopAppBar(
            title = title,
            navigationIcon = navigationIcon,
            actions = actions,
        )
    }
}

@Composable
fun ExpandableAppBar(
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    title: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    expandableContent: @Composable () -> Unit = {},
    isExpanded: Boolean,
    appBarState: AppBarState = rememberAppBarState(),
) {
    AppBarContainer(
        modifier = modifier,
        appBarState = appBarState,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            FlowTopAppBar(
                title = title,
                navigationIcon = navigationIcon,
                actions = actions,
            )
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top),
                content = { expandableContent() },
            )
        }
    }
}

@Composable
fun TabAppBar(
    modifier: Modifier = Modifier,
    pages: List<Page>,
    selectedPage: Int,
    onSelectPage: (Int) -> Unit,
    appBarState: AppBarState = rememberAppBarState(),
) {
    AppBarContainer(
        modifier = modifier,
        appBarState = appBarState,
    ) {
        TabRow(
            modifier = Modifier.statusBarsPadding(),
            selectedTabIndex = selectedPage,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ) {
            pages.forEachIndexed { index, page ->
                Tab(
                    selected = selectedPage == index,
                    onClick = { onSelectPage(index) },
                    text = { Text(stringResource(page.labelResId)) },
                    icon = page.icon?.let {
                        { Icon(imageVector = page.icon, contentDescription = null) }
                    },
                )
            }
        }
    }
}

@Composable
private fun AppBarContainer(
    modifier: Modifier = Modifier,
    appBarState: AppBarState,
    content: @Composable () -> Unit,
) {
    val elevation by animateDpAsState(
        if (appBarState.elevated) {
            Elevation.small
        } else {
            Elevation.zero
        }
    )
    Surface(
        modifier = modifier,
        tonalElevation = elevation,
        shadowElevation = elevation,
        content = content,
    )
}

@Composable
private fun FlowTopAppBar(
    navigationIcon: @Composable () -> Unit = {},
    title: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        modifier = Modifier.statusBarsPadding(),
        title = title,
        navigationIcon = navigationIcon,
        actions = actions,
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface,
        ),
    )
}

@Stable
interface AppBarScrollBehavior {
    val state: AppBarState
    val nestedScrollConnection: NestedScrollConnection
}

@Stable
class AppBarState(initialElevation: Boolean) {
    var elevated by mutableStateOf(initialElevation)

    companion object {
        val Saver: Saver<AppBarState, *> = listSaver(
            save = { listOf(it.elevated) },
            restore = {
                AppBarState(initialElevation = it[0])
            }
        )
    }
}

@Composable
fun rememberAppBarState(): AppBarState {
    return rememberSaveable(saver = AppBarState.Saver) {
        AppBarState(false)
    }
}

object AppBarDefaults {

    private val Height: Dp = 64.dp

    @Composable
    fun appBarScrollBehavior(): AppBarScrollBehavior {
        val appBarState = rememberAppBarState()
        val pinnedScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        val heightOffsetLimit = with(LocalDensity.current) { -(Height.toPx()) }
        SideEffect {
            if (pinnedScrollBehavior.state.heightOffsetLimit != heightOffsetLimit) {
                pinnedScrollBehavior.state.heightOffsetLimit = heightOffsetLimit
            }
        }
        return DelegateAppBarScrollBehavior(appBarState, pinnedScrollBehavior)
    }

    private class DelegateAppBarScrollBehavior(
        override val state: AppBarState,
        private val scrollBehavior: TopAppBarScrollBehavior,
    ) : AppBarScrollBehavior {
        override val nestedScrollConnection = object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                scrollBehavior.nestedScrollConnection.onPostScroll(consumed, available, source)
                state.elevated = scrollBehavior.state.overlappedFraction > 0.01f
                return Offset.Zero
            }
        }
    }
}
