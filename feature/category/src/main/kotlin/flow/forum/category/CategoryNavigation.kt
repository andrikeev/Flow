package flow.forum.category

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import flow.navigation.viewModel
import kotlinx.serialization.Serializable

@Serializable
data class CategoryRoute(val id: String) : NavKey

fun EntryProviderScope<NavKey>.addCategory(
    back: () -> Unit,
    openCategory: (id: String) -> Unit,
    openLogin: () -> Unit,
    openSearchInput: (query: String) -> Unit,
    openTopic: (id: String) -> Unit,
) {
    entry<CategoryRoute> { key ->
        CategoryScreen(
            viewModel = viewModel<CategoryViewModel>(key.id),
            back = back,
            openCategory = openCategory,
            openLogin = openLogin,
            openSearchInput = openSearchInput,
            openTopic = openTopic,
        )
    }
}
