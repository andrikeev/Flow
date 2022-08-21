package me.rutrackersearch.models.topic

data class Post(
    val id: String,
    val author: Author,
    val date: String,
    val content: Content,
)
