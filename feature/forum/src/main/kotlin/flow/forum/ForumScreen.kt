package flow.forum

import androidx.compose.runtime.Composable
import flow.designsystem.component.Page
import flow.designsystem.component.PagesScreen
import flow.designsystem.drawables.FlowIcons
import flow.forum.bookmarks.BookmarksScreen
import flow.forum.root.RootForumScreen
import flow.models.forum.Category

@Composable
fun ForumScreen(
    openCategory: (Category) -> Unit,
) {
    PagesScreen(
        pages = listOf(
            Page(
                labelResId = R.string.forum_screen_title_forum,
                icon = FlowIcons.Forum,
            ) {
                RootForumScreen(openCategory = openCategory)
            },
            Page(
                labelResId = R.string.forum_screen_title_bookmarks,
                icon = FlowIcons.Bookmarks,
            ) {
                BookmarksScreen(openCategory = openCategory)
            }
        )
    )
}
