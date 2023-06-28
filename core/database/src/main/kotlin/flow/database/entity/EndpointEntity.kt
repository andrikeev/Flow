package flow.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Endpoint")
data class EndpointEntity(
    @PrimaryKey val id: String,
    val type: String,
    val host: String,
)
