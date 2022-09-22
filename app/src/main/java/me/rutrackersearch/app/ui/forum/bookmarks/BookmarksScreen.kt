package me.rutrackersearch.app.ui.forum.bookmarks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import me.rutrackersearch.app.R
import me.rutrackersearch.app.ui.common.DynamicBox
import me.rutrackersearch.app.ui.common.Empty
import me.rutrackersearch.app.ui.common.FocusableLazyColumn
import me.rutrackersearch.app.ui.common.LazyColumn
import me.rutrackersearch.app.ui.common.Loading
import me.rutrackersearch.app.ui.common.dividedItems
import me.rutrackersearch.app.ui.common.focusableItems
import me.rutrackersearch.models.forum.Category
import me.rutrackersearch.models.forum.CategoryModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun BookmarksScreen(
    openCategory: (Category) -> Unit,
) {
    BookmarksScreen(
        viewModel = hiltViewModel(),
        openCategory = openCategory,
    )
}

@Composable
private fun BookmarksScreen(
    viewModel: BookmarksViewModel,
    openCategory: (Category) -> Unit,
) {
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is BookmarksSideEffect.OpenCategory -> openCategory(sideEffect.category)
        }
    }
    val state by viewModel.collectAsState()
    BookmarksScreen(state, viewModel::perform)
}

@Composable
private fun BookmarksScreen(
    state: BookmarksState,
    onAction: (BookmarksAction) -> Unit,
) {
    when (state) {
        is BookmarksState.Initial -> Loading()
        is BookmarksState.Empty -> Empty(
            titleRes = R.string.bookmarks_title,
            subtitleRes = R.string.bookmarks_subtitle,
            iconRes = R.drawable.ill_bookmarks,
        )
        is BookmarksState.BookmarksList -> DynamicBox(
            mobileContent = {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                ) {
                    dividedItems(
                        items = state.items,
                        key = { it.category.id },
                        contentType = { it.category::class },
                    ) { bookmark ->
                        Bookmark(
                            bookmark = bookmark,
                            onClick = { onAction(BookmarksAction.BookmarkClicked(bookmark)) },
                        )
                    }
                }
            },
            tvContent = {
                FocusableLazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(32.dp),
                ) {
                    focusableItems(
                        items = state.items,
                        key = { it.category.id },
                        contentType = { it.category::class },
                    ) { bookmark ->
                        Bookmark(
                            bookmark = bookmark,
                            onClick = { onAction(BookmarksAction.BookmarkClicked(bookmark)) },
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun Bookmark(
    bookmark: CategoryModel,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Bookmark,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null,
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 8.dp),
                    text = bookmark.category.name,
                )
            }
            if (bookmark.newTopicsCount > 0) {
                Text(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            shape = CircleShape,
                        )
                        .padding(8.dp),
                    text = "+${bookmark.newTopicsCount}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
