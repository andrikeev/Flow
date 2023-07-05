package flow.data.converters

import flow.database.entity.BookmarkEntity
import flow.database.entity.ForumCategoryEntity
import flow.models.Page
import flow.models.forum.Category
import flow.models.forum.Forum
import flow.models.forum.ForumCategory
import flow.models.forum.ForumItem
import flow.network.dto.forum.CategoryDto
import flow.network.dto.forum.CategoryPageDto
import flow.network.dto.forum.ForumDto

internal fun ForumDto.toForum() = Forum(
    children = children.mapIndexed { index, rootCategory ->
        ForumCategory(
            id = rootCategory.id ?: "c-$index",
            name = rootCategory.name,
            children = rootCategory.children.orEmpty().map(CategoryDto::toForumCategory),
        )
    },
)

private fun CategoryDto.toForumCategory(): ForumCategory = ForumCategory(
    id = requireNotNull(id),
    name = name,
    children = children.orEmpty().map(CategoryDto::toForumCategory),
)

internal fun CategoryPageDto.toCategoryPage() = Page(
    page = page,
    pages = pages,
    items = children.orEmpty().map { ForumItem.Category(it.toCategory()) } +
        topics.orEmpty().map { ForumItem.Topic(it.toTopic()) },
)

internal fun CategoryDto.toCategory(): Category = Category(requireNotNull(id), name)

internal fun Category.toBookmarkEntity(): BookmarkEntity = BookmarkEntity(
    id = id,
    timestamp = System.currentTimeMillis(),
    category = this,
)

internal fun ForumCategoryEntity.toCategory(): Category = Category(id, name)
