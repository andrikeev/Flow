package flow.data.converters

import flow.database.entity.BookmarkEntity
import flow.database.entity.ForumCategoryEntity
import flow.models.Page
import flow.models.forum.Category
import flow.models.forum.Forum
import flow.models.forum.ForumCategory
import flow.models.forum.ForumItem
import flow.models.forum.ForumSection
import flow.network.dto.forum.CategoryDto
import flow.network.dto.forum.CategoryPageDto
import flow.network.dto.forum.ForumDto
import flow.network.dto.forum.SectionDto
import flow.network.dto.topic.ForumTopicDto

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
    items = children.orEmpty().map(CategoryDto::toForumItem) +
        topics.orEmpty().map(ForumTopicDto::toForumItem) +
        sections.orEmpty().map(SectionDto::toForumItem),
)

internal fun CategoryDto.toCategory(): Category = Category(requireNotNull(id), name)

private fun CategoryDto.toForumItem(): ForumItem = ForumItem.Category(toCategory())
private fun SectionDto.toForumItem(): ForumItem = ForumItem.Section(ForumSection(name, topics))
private fun ForumTopicDto.toForumItem(): ForumItem = ForumItem.Topic(toTopic())

internal fun Category.toBookmarkEntity(): BookmarkEntity = BookmarkEntity(
    id = id,
    timestamp = System.currentTimeMillis(),
    category = this,
)

internal fun ForumCategoryEntity.toCategory(): Category = Category(id, name)
