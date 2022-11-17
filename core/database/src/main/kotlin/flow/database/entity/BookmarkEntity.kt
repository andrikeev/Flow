package flow.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import flow.models.forum.Category

@Entity(tableName = "Bookmark")
data class BookmarkEntity(
    @PrimaryKey val id: String,
    val timestamp: Long,
    val category: Category,
    val topics: List<String> = emptyList(),
    val newTopics: List<String> = emptyList(),
) {
    companion object {
        fun of(category: Category): BookmarkEntity {
            return BookmarkEntity(
                id = category.id,
                timestamp = System.currentTimeMillis(),
                category = category,
            )
        }
    }
}
