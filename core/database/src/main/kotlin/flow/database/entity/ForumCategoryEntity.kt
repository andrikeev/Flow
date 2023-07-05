package flow.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ForumCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["parentId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class ForumCategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val parentId: String? = null,
    val orderIndex: Int,
)
