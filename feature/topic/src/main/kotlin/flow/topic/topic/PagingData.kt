package flow.topic.topic

import flow.ui.component.LoadStates

data class PagingData<T>(
    val items: List<T>,
    val loadStates: LoadStates,
)
