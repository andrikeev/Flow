package me.rutrackersearch.models.topic

import me.rutrackersearch.models.forum.Category

sealed interface Topic {
    val id: String
    val title: String
    val author: Author?
    val category: Category?
}

data class BaseTopic(
    override val id: String,
    override val title: String,
    override val author: Author? = null,
    override val category: Category? = null,
) : Topic

data class Torrent(
    override val id: String,
    override val title: String,
    override val author: Author? = null,
    override val category: Category? = null,
    val tags: String? = null,
    val status: TorrentStatus? = null,
    val date: Long? = null,
    val size: String? = null,
    val seeds: Int? = null,
    val leeches: Int? = null,
    val magnetLink: String? = null,
    val description: TorrentDescription? = null,
) : Topic
