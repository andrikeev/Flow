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
import flow.designsystem.R
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.AppTheme
import flow.designsystem.theme.FlowTheme

@Composable
@NonRestartableComposable
fun AppBar(
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    title: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    appBarState: AppBarState = rememberAppBarState(),
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
    appBarState: AppBarState = rememberAppBarState(),
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
    appBarState: AppBarState = rememberAppBarState(),
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
private fun AppBarContainer(
    modifier: Modifier = Modifier,
    appBarState: AppBarState,
    content: @Composable BoxScope.() -> Unit,
) {
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
interface AppBarScrollBehavior {
    val state: AppBarState
    val nestedScrollConnection: NestedScrollConnection
}

@Stable
class AppBarState internal constructor(initialElevation: Boolean) {
    var elevated by mutableStateOf(initialElevation)
        internal set

    companion object {
        val Saver: Saver<AppBarState, *> = listSaver(save = { listOf(it.elevated) }, restore = {
            AppBarState(initialElevation = it[0])
        })
    }
}

@Composable
fun rememberAppBarState(): AppBarState {
    return rememberSaveable(saver = AppBarState.Saver) {
        AppBarState(false)
    }
}

object AppBarDefaults {

    val Height: Dp = 64.dp

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
                source: NestedScrollSource,
            ): Offset {
                scrollBehavior.nestedScrollConnection.onPostScroll(consumed, available, source)
                state.elevated = scrollBehavior.state.overlappedFraction > 0.01f
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
            appBarState = AppBarState(params.elevated),
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
            appBarState = AppBarState(params.elevated),
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
            appBarState = AppBarState(params.elevated),
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
