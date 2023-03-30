package flow.forum.category

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import flow.models.forum.Category
import flow.models.search.Filter
import flow.models.topic.Topic
import flow.models.topic.Torrent
import flow.navigation.NavigationController
import flow.navigation.model.NavigationGraphBuilder
import flow.navigation.ui.NavigationAnimations
import flow.ui.args.require
import flow.ui.parcel.CategoryWrapper

private val NavigationGraphBuilder.CategoryRoute: String
    get() = route("Category")
private const val CategoryKey = "Category"

data class CategoryNavigation(
    val addCategory: NavigationGraphBuilder.(
        back: () -> Unit,
        openCategory: (Category) -> Unit,
        openSearchInput: (Filter) -> Unit,
        openTopic: (Topic) -> Unit,
        openTorrent: (Torrent) -> Unit,
        animations: NavigationAnimations,
    ) -> Unit,
    val openCategory: NavigationController.(category: Category) -> Unit,
)

fun NavigationGraphBuilder.buildCategoryNavigation() = CategoryNavigation(
    addCategory = NavigationGraphBuilder::addCategory,
    openCategory = { category ->
        navigate(CategoryRoute) {
            putCategory(category)
        }
    },
)

fun NavigationGraphBuilder.addCategory(
    back: () -> Unit,
    openCategory: (Category) -> Unit,
    openSearchInput: (Filter) -> Unit,
    openTopic: (Topic) -> Unit,
    openTorrent: (Torrent) -> Unit,
    animations: NavigationAnimations,
) = addDestination(
    route = CategoryRoute,
    content = {
        CategoryScreen(
            back = back,
            openCategory = openCategory,
            openSearchInput = openSearchInput,
            openTopic = openTopic,
            openTorrent = openTorrent,
        )
    },
    animations = animations,
)

private fun Bundle.putCategory(category: Category) {
    putParcelable(CategoryKey, CategoryWrapper(category))
}

internal val SavedStateHandle.category: Category
    get() = require<CategoryWrapper>(CategoryKey).category
