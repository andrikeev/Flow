package flow.search.result.domain

import flow.dispatchers.api.Dispatchers
import flow.domain.usecase.GetForumUseCase
import flow.models.forum.Category
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class GetCategoriesByGroupIdUseCase @Inject constructor(
    private val getForumUseCase: GetForumUseCase,
    private val dispatchers: Dispatchers,
) {
    suspend operator fun invoke(id: String): List<Category> {
        return withContext(dispatchers.default) {
            val forum = getForumUseCase()
            mutableListOf<Category>().apply {
                forum.children.forEach { root ->
                    root.children.forEach { forum ->
                        if (forum.id == id) {
                            add(Category(forum.id, forum.name))
                            addAll(forum.children.map { Category(it.id, it.name) })
                            return@apply
                        }
                    }
                }
            }
        }
    }
}
