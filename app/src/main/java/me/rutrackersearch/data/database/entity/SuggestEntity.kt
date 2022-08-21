package me.rutrackersearch.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Suggest")
data class SuggestEntity(
    @PrimaryKey val id: Int,
    val timestamp: Long,
    val suggest: String,
) {
    companion object {
        fun of(value: String): SuggestEntity {
            return SuggestEntity(
                id = value.lowercase().hashCode(),
                timestamp = System.currentTimeMillis(),
                suggest = value,
            )
        }
    }
}
