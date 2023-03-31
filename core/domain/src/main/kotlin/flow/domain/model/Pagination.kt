package flow.domain.model

data class Pagination(
    val loadedPages: IntRange = IntRange.EMPTY,
    val totalPages: Int = 0,
)
