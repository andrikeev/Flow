package flow.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ForumMetadata(
    @PrimaryKey val id: Int = 1,
    val lastUpdatedTimestamp: Long,
)
