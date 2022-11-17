package flow.search.domain

import flow.dispatchers.api.Dispatchers
import flow.domain.usecase.GetForumTreeUseCase
import flow.models.forum.Category
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class GetCategoriesByGroupIdUseCase @Inject constructor(
    private val getForumTreeUseCase: GetForumTreeUseCase,
    private val dispatchers: Dispatchers,
) {
    suspend operator fun invoke(id: String): List<Category> {
        return withContext(dispatchers.default) {
            val forumTree = getForumTreeUseCase()
            mutableListOf<Category>().apply {
                forumTree.children.forEach { root ->
                    root.children.forEach { forum ->
                        if (forum.category.id == id) {
                            add(forum.category)
                            addAll(forum.children)
                            return@apply
                        }
                    }
                }
            }
        }
    }
}
