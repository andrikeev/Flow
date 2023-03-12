package flow.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import flow.models.forum.Category
import flow.models.search.Order
import flow.models.search.Period
import flow.models.search.Sort
import flow.models.topic.Author

@Entity(tableName = "Search")
data class SearchHistoryEntity(
    @PrimaryKey val id: Int,
    val timestamp: Long,
    val query: String?,
    val sort: Sort,
    val order: Order,
    val period: Period,
    val author: Author?,
    val categories: List<Category>?,
)
