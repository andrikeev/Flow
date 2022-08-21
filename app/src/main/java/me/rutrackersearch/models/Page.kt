package me.rutrackersearch.models

data class Page<T>(
    val items: List<T>,
    val page: Int,
    val pages: Int,
)
