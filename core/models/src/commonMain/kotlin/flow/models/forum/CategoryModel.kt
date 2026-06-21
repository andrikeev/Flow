package flow.models.forum

data class CategoryModel(
    val category: Category,
    val isBookmark: Boolean = false,
    val newTopicsCount: Int = 0,
)
