package flow.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import flow.designsystem.drawables.Icon
import kotlinx.coroutines.launch

@Composable
fun PagesScreen(
    pages: List<Page>,
    modifier: Modifier = Modifier,
    bottomBar: @Composable () -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    Scaffold(
        modifier = modifier,
        topBar = { appBarState ->
            TabAppBar(
                pages = pages,
                selectedPage = pagerState.currentPage,
                onSelectPage = { page ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(page = page)
                    }
                },
                appBarState = appBarState,
            )
        },
        bottomBar = bottomBar,
    ) { padding ->
        HorizontalPager(
            modifier = Modifier.padding(padding),
            pageCount = pages.size,
            state = pagerState,
        ) { page ->
            pages[page].content()
        }
    }
}

data class Page(
    val icon: Icon? = null,
    @StringRes val labelResId: Int? = null,
    val content: @Composable () -> Unit,
)
