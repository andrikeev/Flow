package flow.data.impl.repository

import flow.data.api.repository.ForumRepository
import flow.data.converters.toCategory
import flow.database.dao.ForumCategoryDao
import flow.database.dao.ForumMetadataDao
import flow.database.entity.ForumCategoryEntity
import flow.database.entity.ForumMetadata
import flow.logger.api.LoggerFactory
import flow.models.forum.Category
import flow.models.forum.Forum
import flow.models.forum.ForumCategory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ForumRepositoryImpl @Inject constructor(
    private val forumCategoryDao: ForumCategoryDao,
    private val forumMetadataDao: ForumMetadataDao,
    loggerFactory: LoggerFactory,
) : ForumRepository {
    private val logger = loggerFactory.get("ForumRepositoryImpl")

    private var inMemoryForum: Forum? = null

    override suspend fun isNotEmpty(): Boolean = forumCategoryDao.isForumStored()

    override suspend fun isForumFresh(maxAgeInDays: Int): Boolean {
        val metadata = forumMetadataDao.getMetadata()
        return metadata?.let {
            val ageInMillis = System.currentTimeMillis() - it.lastUpdatedTimestamp
            val ageInDays = TimeUnit.MILLISECONDS.toDays(ageInMillis)
            ageInDays < maxAgeInDays
        } ?: false
    }

    override suspend fun storeForum(forum: Forum) {
        val flattenedCategories = mutableListOf<ForumCategoryEntity>()
        flattenCategories(forum.children, flattenedCategories)
        forumCategoryDao.deleteAll()
        forumCategoryDao.insertAll(flattenedCategories)
        val timestamp = System.currentTimeMillis()
        val metadata = ForumMetadata(lastUpdatedTimestamp = timestamp)
        forumMetadataDao.insertOrUpdate(metadata)
    }

    override suspend fun getForum(): Forum {
        if (inMemoryForum == null) {
            val topLevelCategories = forumCategoryDao.getTopLevelCategories()
            val categories = topLevelCategories.map { category ->
                buildCategoryTree(category)
            }
            inMemoryForum = Forum(children = categories)
        }
        return inMemoryForum as Forum
    }

    override suspend fun getCategory(id: String): Category? {
        logger.d { "getCategory: id=$id" }
        return forumCategoryDao.get(id)?.toCategory()
    }

    private fun forumCategoryToEntity(
        category: ForumCategory,
        parentId: String? = null,
        orderIndex: Int,
    ) = ForumCategoryEntity(
        id = category.id,
        name = category.name,
        parentId = parentId,
        orderIndex = orderIndex,
    )

    private fun forumEntityToCategory(
        entity: ForumCategoryEntity,
        children: List<ForumCategory>,
    ) = ForumCategory(
        id = entity.id,
        name = entity.name,
        children = children,
    )

    private fun flattenCategories(
        categories: List<ForumCategory>,
        flattened: MutableList<ForumCategoryEntity>,
        parentId: String? = null,
    ) {
        categories.forEachIndexed { index, category ->
            flattened.add(forumCategoryToEntity(category, parentId, index))
            flattenCategories(category.children, flattened, category.id)
        }
    }

    private suspend fun buildCategoryTree(entity: ForumCategoryEntity): ForumCategory {
        val children = forumCategoryDao.getChildren(entity.id)
            .sortedBy(ForumCategoryEntity::orderIndex)
            .map { child -> buildCategoryTree(child) }
        return forumEntityToCategory(entity, children)
    }
}
