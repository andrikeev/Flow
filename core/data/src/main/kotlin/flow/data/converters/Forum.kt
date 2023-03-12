package flow.data.converters

import flow.database.entity.BookmarkEntity
import flow.models.Page
import flow.models.forum.Category
import flow.models.forum.ForumItem
import flow.models.forum.ForumTree
import flow.models.forum.ForumTreeGroup
import flow.models.forum.ForumTreeRootGroup
import flow.network.dto.ResultDto
import flow.network.dto.forum.CategoryDto
import flow.network.dto.forum.CategoryPageDto
import flow.network.dto.forum.ForumDto

internal fun ResultDto<ForumDto>.toForumTree(): ForumTree {
    require(this is ResultDto.Data)
    return ForumTree(
        children = value.children.map { rootCategory ->
            ForumTreeRootGroup(
                name = rootCategory.name,
                children = rootCategory.children.orEmpty().map { category ->
                    ForumTreeGroup(category.toCategory(), emptyList())
                },
            )
        },
    )
}

internal fun ResultDto<CategoryPageDto>.toCategoryPage(): Page<ForumItem> {
    require(this is ResultDto.Data)
    return Page(
        page = value.page,
        pages = value.pages,
        items = value.children.orEmpty().map { ForumItem.Category(it.toCategory()) } +
                value.topics.orEmpty().map { ForumItem.Topic(it.toTopic()) },
    )
}

internal fun CategoryDto.toCategory(): Category = Category(requireNotNull(id), name)

internal fun Category.toEntity(): BookmarkEntity = BookmarkEntity(
    id = id,
    timestamp = System.currentTimeMillis(),
    category = this,
)
