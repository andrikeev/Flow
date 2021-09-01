package me.rutrackersearch.domain.entity.search

data class Suggest(
    val value: String,
    val substring: IntRange? = null,
)
