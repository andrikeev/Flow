package flow.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Suggest")
data class SuggestEntity(
    @PrimaryKey val id: Int,
    val timestamp: Long,
    val suggest: String,
)
