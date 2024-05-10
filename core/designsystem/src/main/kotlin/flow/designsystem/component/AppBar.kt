package flow.designsystem.component

import androidx.activity.enableEdgeToEdge
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import flow.designsystem.R
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.AppTheme
import flow.designsystem.theme.FlowTheme
import flow.designsystem.utils.componentActivity
import flow.designsystem.utils.rememberSystemBarStyle

@Composable
@NonRestartableComposable
fun AppBar(
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    title: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    appBarState: AppBarState = rememberPinnedAppBarState(),
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
    appBarState: AppBarState = rememberPinnedAppBarState(),
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
            exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut(),
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
    appBarState: AppBarState = rememberPinnedAppBarState(),
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
            TabRowDefaults.SecondaryIndicator(
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
                    {
                        Icon(
                            icon = page.icon,
                            contentDescription = page.labelResId?.let { stringResource(it) },
                        )
                    }
                },
            )
        }
    }
}

@Composable
internal fun AppBarContainer(
    modifier: Modifier = Modifier,
    appBarState: AppBarState = rememberPinnedAppBarState(),
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
    val activity = LocalContext.current.componentActivity
    val systemBarStyle = rememberSystemBarStyle()
    LaunchedEffect(systemBarStyle) {
        activity.enableEdgeToEdge(statusBarStyle = systemBarStyle)
    }
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
    colors = TopAppBarDefaults.topAppBarColors(
        containerColor = Color.Transparent,
        scrolledContainerColor = Color.Transparent,
        navigationIconContentColor = AppTheme.colors.onSurface,
        titleContentColor = AppTheme.colors.onSurface,
        actionIconContentColor = AppTheme.colors.onSurface,
    ),
)

@Stable
interface AppBarState {
    val elevated: Boolean
}

@Stable
class PinnedAppBarState internal constructor(initialElevation: Boolean) : AppBarState {
    override var elevated by mutableStateOf(initialElevation)
        internal set

    companion object {
        val Saver: Saver<PinnedAppBarState, Boolean> = Saver(
            save = { it.elevated },
            restore = ::PinnedAppBarState,
        )
    }
}

@Composable
fun rememberPinnedAppBarState(initial: Boolean = false): PinnedAppBarState {
    return rememberSaveable(saver = PinnedAppBarState.Saver) {
        PinnedAppBarState(initial)
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
                    labelResId = R.string.designsystem_action_search,
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

private class ExpandableAppBarParamsProvider :
    CollectionPreviewParameterProvider<ExpandableAppBarParams>(
        ExpandableAppBarParams(
            title = "Expandable app bar title",
            expanded = false,
            elevated = false,
        ),
        ExpandableAppBarParams(
            title = "Expandable app bar title elevated",
            expanded = false,
            elevated = true,
        ),
        ExpandableAppBarParams(
            title = "Expandable app bar title expanded",
            expanded = true,
            elevated = false,
        ),
        ExpandableAppBarParams(
            title = "Expandable app bar title expanded, elevated",
            expanded = true,
            elevated = true,
        ),
    )
