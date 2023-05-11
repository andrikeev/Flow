package flow.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import flow.designsystem.R
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.AppTheme
import flow.designsystem.theme.FlowTheme
import flow.designsystem.utils.RunOnFirstComposition

@Composable
@NonRestartableComposable
fun AppBar(
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    title: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    appBarState: PinnedAppBarState = rememberAppBarState(),
) = AppBarContainer(
    modifier = modifier,
    appBarState = appBarState,
) {
    AppBar(
        title = title,
        navigationIcon = navigationIcon,
        actions = actions,
    )
}

@Composable
@NonRestartableComposable
fun ExpandableAppBar(
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    title: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    expanded: Boolean,
    appBarState: PinnedAppBarState = rememberAppBarState(),
    expandableContent: @Composable () -> Unit = {},
) = AppBarContainer(
    modifier = modifier,
    appBarState = appBarState,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        AppBar(
            title = title,
            navigationIcon = navigationIcon,
            actions = actions,
        )
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top),
            content = { expandableContent() },
        )
    }
}

@Composable
@NonRestartableComposable
fun TabAppBar(
    modifier: Modifier = Modifier,
    pages: List<Page>,
    selectedPage: Int,
    onSelectPage: (Int) -> Unit,
    appBarState: PinnedAppBarState = rememberAppBarState(),
) = AppBarContainer(
    modifier = modifier,
    appBarState = appBarState,
) {
    TabRow(
        modifier = Modifier.statusBarsPadding(),
        selectedTabIndex = selectedPage,
        containerColor = Color.Transparent,
        contentColor = AppTheme.colors.onSurface,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedPage]),
                color = AppTheme.colors.primary,
            )
        },
    ) {
        pages.forEachIndexed { index, page ->
            Tab(
                selected = selectedPage == index,
                onClick = { onSelectPage(index) },
                text = page.labelResId?.let {
                    { Text(stringResource(page.labelResId)) }
                },
                icon = page.icon?.let {
                    { Icon(icon = page.icon, contentDescription = page.labelResId?.let { stringResource(it) }) }
                },
            )
        }
    }
}

@Composable
internal fun AppBarContainer(
    modifier: Modifier = Modifier,
    appBarState: PinnedAppBarState = rememberAppBarState(),
    content: @Composable BoxScope.() -> Unit,
) {
    val systemUiController = rememberSystemUiController()
    val darkIcons = !AppTheme.colors.isDark
    RunOnFirstComposition {
        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = darkIcons,
            transformColorForLightContent = { Color.Transparent },
        )
    }
    val elevation by animateDpAsState(
        targetValue = if (appBarState.elevated) {
            AppTheme.elevations.medium
        } else {
            AppTheme.elevations.zero
        },
        label = "AppBarContainer_Elevation",
    )
    Surface(
        modifier = modifier,
        tonalElevation = elevation,
        shadowElevation = elevation,
        content = content,
    )
}

@Composable
@NonRestartableComposable
private fun AppBar(
    navigationIcon: @Composable () -> Unit = {},
    title: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) = TopAppBar(
    modifier = Modifier.statusBarsPadding(),
    navigationIcon = navigationIcon,
    title = title,
    actions = actions,
    colors = TopAppBarDefaults.smallTopAppBarColors(
        containerColor = Color.Transparent,
        scrolledContainerColor = Color.Transparent,
        navigationIconContentColor = AppTheme.colors.onSurface,
        titleContentColor = AppTheme.colors.onSurface,
        actionIconContentColor = AppTheme.colors.onSurface,
    ),
)

@Stable
interface AppBarBehavior {
    val appBarState: AppBarState
    val nestedScrollConnection: NestedScrollConnection
}

@Stable
interface PinnedAppBarBehavior: AppBarBehavior {
    override val appBarState: PinnedAppBarState
}

@Stable
interface AppBarState

@Stable
class PinnedAppBarState internal constructor(initialElevation: Boolean): AppBarState {
    var elevated by mutableStateOf(initialElevation)
        internal set

    companion object {
        val Saver: Saver<PinnedAppBarState, *> = listSaver(save = { listOf(it.elevated) }, restore = {
            PinnedAppBarState(initialElevation = it[0])
        })
    }
}

@Composable
fun rememberAppBarState(initial: Boolean = false): PinnedAppBarState {
    return rememberSaveable(saver = PinnedAppBarState.Saver) {
        PinnedAppBarState(initial)
    }
}

object AppBarDefaults {

    val Height: Dp = 64.dp

    @Composable
    fun appBarScrollBehavior(): PinnedAppBarBehavior {
        val appBarState = rememberAppBarState()
        val pinnedScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        val heightOffsetLimit = with(LocalDensity.current) { -(Height.toPx()) }
        SideEffect {
            if (pinnedScrollBehavior.state.heightOffsetLimit != heightOffsetLimit) {
                pinnedScrollBehavior.state.heightOffsetLimit = heightOffsetLimit
            }
        }
        return DelegatePinnedAppBarBehavior(appBarState, pinnedScrollBehavior)
    }

    private class DelegatePinnedAppBarBehavior(
        override val appBarState: PinnedAppBarState,
        private val scrollBehavior: TopAppBarScrollBehavior,
    ) : PinnedAppBarBehavior {
        override val nestedScrollConnection = object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                scrollBehavior.nestedScrollConnection.onPostScroll(consumed, available, source)
                appBarState.elevated = scrollBehavior.state.overlappedFraction > 0.01f
                return Offset.Zero
            }
        }
    }
}

@ThemePreviews
@Composable
private fun AppBarPreview(@PreviewParameter(AppBarParamsProvider::class) params: AppBarParams) {
    FlowTheme {
        AppBar(
            navigationIcon = { BackButton {} },
            title = { Text(params.title) },
            actions = {
                SearchButton {}
                FavoriteButton(favorite = true) {}
            },
            appBarState = PinnedAppBarState(params.elevated),
        )
    }
}

@ThemePreviews
@Composable
private fun ExpandableAppBarPreview(
    @PreviewParameter(ExpandableAppBarParamsProvider::class) params: ExpandableAppBarParams,
) {
    FlowTheme {
        ExpandableAppBar(
            navigationIcon = { BackButton {} },
            title = { Text(params.title) },
            actions = {
                SearchButton {}
                IconButton(onClick = {}) { ExpandCollapseIcon(expanded = params.expanded) }
            },
            expanded = params.expanded,
            appBarState = PinnedAppBarState(params.elevated),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppTheme.spaces.large),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = "Title")
                Text(text = "Subtitle")
                TextButton(text = "Button", onClick = {})
            }
        }
    }
}

@ThemePreviews
@Composable
private fun TabBarPreview(@PreviewParameter(AppBarParamsProvider::class) params: AppBarParams) {
    FlowTheme {
        TabAppBar(
            pages = listOf(
                Page(
                    icon = FlowIcons.AppIcon,
                    labelResId = R.string.designsystem_hint_search,
                    content = {},
                ),
                Page(
                    icon = null,
                    labelResId = R.string.designsystem_action_login,
                    content = {},
                ),
                Page(
                    icon = FlowIcons.Favorite,
                    labelResId = null,
                    content = {},
                ),
            ),
            selectedPage = 1,
            onSelectPage = {},
            appBarState = PinnedAppBarState(params.elevated),
        )
    }
}

private data class AppBarParams(
    val title: String,
    val elevated: Boolean,
)

private class AppBarParamsProvider : CollectionPreviewParameterProvider<AppBarParams>(
    AppBarParams("App bar title", false),
    AppBarParams("App bar title elevated", true),
)

private data class ExpandableAppBarParams(
    val title: String,
    val expanded: Boolean,
    val elevated: Boolean,
)

private class ExpandableAppBarParamsProvider : CollectionPreviewParameterProvider<ExpandableAppBarParams>(
    ExpandableAppBarParams(title = "Expandable app bar title", expanded = false, elevated = false),
    ExpandableAppBarParams(title = "Expandable app bar title elevated", expanded = false, elevated = true),
    ExpandableAppBarParams(title = "Expandable app bar title expanded", expanded = true, elevated = false),
    ExpandableAppBarParams(title = "Expandable app bar title expanded, elevated", expanded = true, elevated = true),
)
