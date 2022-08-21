package me.rutrackersearch.models.search

data class Suggest(
    val value: String,
    val substring: IntRange? = null,
)
