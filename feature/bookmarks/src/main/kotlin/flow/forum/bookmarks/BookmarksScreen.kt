package flow.forum.bookmarks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import flow.designsystem.component.Body
import flow.designsystem.component.BodyLarge
import flow.designsystem.component.Icon
import flow.designsystem.component.LazyList
import flow.designsystem.component.Surface
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.AppTheme
import flow.models.forum.CategoryModel
import flow.navigation.viewModel
import flow.ui.component.emptyItem
import flow.ui.component.loadingItem
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun BookmarksScreen(
    openCategory: (String) -> Unit,
) = BookmarksScreen(
    viewModel = viewModel(),
    openCategory = openCategory,
)

@Composable
private fun BookmarksScreen(
    viewModel: BookmarksViewModel,
    openCategory: (String) -> Unit,
) {
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is BookmarksSideEffect.OpenCategory -> openCategory(sideEffect.categoryId)
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
    contentPadding = PaddingValues(vertical = AppTheme.spaces.large),
) {
    when (state) {
        is BookmarksState.Initial -> loadingItem()
        is BookmarksState.Empty -> emptyItem(
            titleRes = R.string.forum_screen_bookmarks_empty_title,
            subtitleRes = R.string.forum_screen_bookmarks_empty_subtitle,
            imageRes = R.drawable.ill_bookmarks,
        )

        is BookmarksState.BookmarksList -> items(
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
) = Surface(
    modifier = Modifier.padding(
        horizontal = AppTheme.spaces.mediumLarge,
        vertical = AppTheme.spaces.mediumSmall,
    ),
    onClick = onClick,
    shape = AppTheme.shapes.large,
    tonalElevation = AppTheme.elevations.small,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppTheme.spaces.large)
            .defaultMinSize(minHeight = AppTheme.sizes.default),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            icon = FlowIcons.BookmarkChecked,
            tint = AppTheme.colors.primary,
            contentDescription = null,
        )
        BodyLarge(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = AppTheme.spaces.large),
            text = bookmark.category.name,
        )
        if (bookmark.newTopicsCount > 0) {
            Body(
                modifier = Modifier
                    .background(
                        color = AppTheme.colors.primary,
                        shape = AppTheme.shapes.circle,
                    )
                    .padding(AppTheme.spaces.medium),
                text = "+${bookmark.newTopicsCount}",
                color = AppTheme.colors.onPrimaryContainer,
            )
        }
    }
}
