package flow.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FavoriteSearch")
data class FavoriteSearchEntity(
    @PrimaryKey val id: Int,
)
