package flow.testing.repository

import flow.data.api.BookmarksRepository
import flow.models.forum.Category
import flow.models.forum.CategoryModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class TestBookmarksRepository : BookmarksRepository {
    private val mutableBookmarks = MutableStateFlow<List<CategoryModel>>(emptyList())
    private val mutableNewTopics = MutableStateFlow<Map<String, List<String>>>(emptyMap())

    override fun observeBookmarks(): Flow<List<CategoryModel>> = mutableBookmarks

    override fun observeIds(): Flow<List<String>> =
        mutableBookmarks.map { categoryModels -> categoryModels.map(CategoryModel::category).map(Category::id) }

    override fun observeNewTopics(): Flow<List<String>> =
        mutableNewTopics.map { it.map(Map.Entry<String, List<String>>::value).flatten() }

    override fun observeNewTopics(id: String): Flow<List<String>> =
        mutableNewTopics.map { it[id].orEmpty() }

    override suspend fun add(category: Category) {
        mutableBookmarks.value = mutableBookmarks.value.plus(CategoryModel(category, true))
    }

    override suspend fun remove(category: Category) {
        mutableBookmarks.value = mutableBookmarks.value.minus(CategoryModel(category, true))
    }

    override suspend fun clear() {
        mutableBookmarks.value = emptyList()
    }

    override suspend fun markVisited(id: String) {
        mutableNewTopics.value = mutableNewTopics.value.minus(id)
    }
}
