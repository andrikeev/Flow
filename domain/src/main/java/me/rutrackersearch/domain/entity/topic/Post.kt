package me.rutrackersearch.domain.entity.topic

data class Post(
    val id: String,
    val author: Author,
    val date: String,
    val content: Content,
)
