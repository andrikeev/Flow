package flow.forum.bookmarks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
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
import flow.designsystem.component.LazyList
import flow.designsystem.drawables.FlowIcons
import flow.forum.R
import flow.models.forum.Category
import flow.models.forum.CategoryModel
import flow.ui.component.dividedItems
import flow.ui.component.emptyItem
import flow.ui.component.loadingItem
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun BookmarksScreen(
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
) = LazyList(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(vertical = 8.dp),
) {
    when (state) {
        is BookmarksState.Initial -> loadingItem()
        is BookmarksState.Empty -> emptyItem(
            titleRes = R.string.forum_screen_bookmarks_empty_title,
            subtitleRes = R.string.forum_screen_bookmarks_empty_subtitle,
            imageRes = R.drawable.ill_bookmarks,
        )

        is BookmarksState.BookmarksList -> dividedItems(
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
                imageVector = FlowIcons.BookmarkChecked,
                tint = MaterialTheme.colorScheme.tertiary,
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
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape,
                        )
                        .padding(8.dp),
                    text = "+${bookmark.newTopicsCount}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}
