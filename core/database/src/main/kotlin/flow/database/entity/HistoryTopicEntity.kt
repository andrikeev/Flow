package flow.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import flow.models.forum.Category
import flow.models.topic.Author
import flow.models.topic.BaseTopic
import flow.models.topic.Topic
import flow.models.topic.Torrent
import flow.models.topic.TorrentStatus

@Entity(tableName = "HistoryTopic")
data class HistoryTopicEntity(
    @PrimaryKey val id: String,
    val timestamp: Long,
    val title: String,
    val author: Author?,
    val category: Category?,
    val tags: String? = null,
    val status: TorrentStatus? = null,
    val date: Long? = null,
    val size: String? = null,
    val seeds: Int? = null,
    val leeches: Int? = null,
    val magnetLink: String? = null,
) {
    fun toTopic(): Topic {
        return if (tags == null && status == null && size == null && seeds == null && leeches == null) {
            BaseTopic(
                id = id,
                title = title,
                author = author,
                category = category,
            )
        } else {
            Torrent(
                id = id,
                title = title,
                author = author,
                category = category,
                tags = tags,
                status = status,
                date = date,
                size = size,
                seeds = seeds,
                leeches = leeches,
                magnetLink = magnetLink,
            )
        }
    }

    companion object {
        fun of(topic: Topic): HistoryTopicEntity {
            val timestamp = System.currentTimeMillis()
            return when (topic) {
                is BaseTopic -> HistoryTopicEntity(
                    id = topic.id,
                    timestamp = timestamp,
                    title = topic.title,
                    author = topic.author,
                    category = topic.category,
                )

                is Torrent -> HistoryTopicEntity(
                    id = topic.id,
                    timestamp = timestamp,
                    title = topic.title,
                    author = topic.author,
                    category = topic.category,
                    tags = topic.tags,
                    status = topic.status,
                    date = topic.date,
                    size = topic.size,
                    seeds = topic.seeds,
                    leeches = topic.leeches,
                    magnetLink = topic.magnetLink,
                )
            }
        }
    }
}
