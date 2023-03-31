package flow.domain.model

data class PagingData<T>(
    val data: T?,
    val loadStates: LoadStates,
    val pagination: Pagination,
)
