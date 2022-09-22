package me.rutrackersearch.app.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import me.rutrackersearch.app.ui.theme.surfaceColorAtElevation

@Composable
fun AppBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.smallTopAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    val scrollFraction by derivedStateOf { scrollBehavior?.scrollFraction ?: 0f }
    val appBarContainerColor by colors.containerColor(scrollFraction)
    Surface(
        modifier = modifier,
        color = appBarContainerColor
    ) {
        SmallTopAppBar(
            modifier = Modifier.statusBarsPadding(),
            title = title,
            navigationIcon = navigationIcon,
            actions = actions,
            colors = colors,
            scrollBehavior = scrollBehavior,
        )
    }
}

@Composable
fun ExpandableAppBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.smallTopAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    isExpanded: Boolean,
    expandableContent: @Composable () -> Unit = {},
) {
    val scrollFraction by derivedStateOf { scrollBehavior?.scrollFraction ?: 0f }
    val appBarContainerColor by colors.containerColor(scrollFraction)
    Surface(
        modifier = modifier,
        color = appBarContainerColor
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            SmallTopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = title,
                navigationIcon = navigationIcon,
                actions = actions,
                colors = colors,
                scrollBehavior = scrollBehavior,
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
    scrollBehavior: TabAppBarScrollBehavior? = null,
) {
    val scrolled by derivedStateOf { (scrollBehavior?.scrollOffset ?: 0f) < -0.01f }
    val elevation by animateDpAsState(
        targetValue = if (scrolled) {
            ContentElevation.small
        } else {
            ContentElevation.zero
        },
        animationSpec = tween(
            durationMillis = 500,
            easing = LinearOutSlowInEasing
        ),
    )
    val containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(elevation)
    Surface(
        modifier = modifier,
        color = containerColor
    ) {
        TabRow(
            modifier = Modifier.statusBarsPadding(),
            selectedTabIndex = selectedPage,
            containerColor = Color.Transparent,
        ) {
            pages.forEachIndexed { index, page ->
                Tab(
                    selected = selectedPage == index,
                    onClick = { onSelectPage(index) },
                    text = { Text(stringResource(page.labelResId)) },
                    icon = page.icon?.let {
                        { Icon(imageVector = page.icon, contentDescription = null) }
                    },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
fun TVAppBar(
    modifier: Modifier = Modifier,
    title: String,
    scrollBehavior: TabAppBarScrollBehavior,
    action: @Composable () -> Unit,
) {
    val elevation by animateDpAsState(
        targetValue = if ((scrollBehavior.scrollOffset) < -0.01f) {
            ContentElevation.small
        } else {
            ContentElevation.zero
        },
    )
    Surface(color = MaterialTheme.colorScheme.surfaceColorAtElevation(elevation)) {
        Row(
            modifier = modifier
                .padding(horizontal = 32.dp, vertical = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge,
            )
            action()
        }
    }
}

@Composable
fun rememberTabAppBarScrollBehavior(): TabAppBarScrollBehavior {
    var contentOffset by rememberSaveable { mutableStateOf(0f) }
    val scrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (consumed.y == 0f && available.y > 0f) {
                    // Reset the total offset to zero when scrolling all the way down. This will
                    // eliminate some float precision inaccuracies.
                    contentOffset = 0f
                } else {
                    contentOffset += consumed.y
                }
                return Offset.Zero
            }
        }
    }
    return TabAppBarScrollBehavior(contentOffset, scrollConnection)
}

class TabAppBarScrollBehavior(
    val scrollOffset: Float,
    val nestedScrollConnection: NestedScrollConnection,
)
