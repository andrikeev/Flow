package flow.topic

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import flow.models.search.Filter
import flow.navigation.viewModel
import kotlinx.serialization.Serializable

@Serializable
data class TopicRoute(val id: String) : NavKey

fun EntryProviderScope<NavKey>.addTopic(
    back: () -> Unit,
    openCategory: (id: String) -> Unit,
    openLogin: () -> Unit,
    openSearch: (filter: Filter) -> Unit,
) {
    entry<TopicRoute> { key ->
        TopicScreen(
            viewModel = viewModel<TopicViewModel>(key.id),
            back = back,
            openCategory = openCategory,
            openLogin = openLogin,
            openSearch = openSearch,
        )
    }
}
