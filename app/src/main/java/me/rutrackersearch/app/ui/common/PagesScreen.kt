package me.rutrackersearch.app.ui.common

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@Composable
fun PagesScreen(
    modifier: Modifier = Modifier,
    pages: List<Page>
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    val tabAppBarScrollBehavior = rememberTabAppBarScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(tabAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TabAppBar(
                pages = pages,
                selectedPage = pagerState.currentPage,
                onSelectPage = { page ->
                    coroutineScope.launch {
                        pagerState.scrollToPage(page = page)
                    }
                },
                scrollBehavior = tabAppBarScrollBehavior,
            )
        }
    ) {
        HorizontalPager(
            modifier = Modifier.padding(it),
            count = pages.size,
            state = pagerState
        ) { page ->
            pages[page].content()
        }
    }
}

data class Page(
    val icon: ImageVector? = null,
    @StringRes val labelResId: Int,
    val content: @Composable () -> Unit,
)
