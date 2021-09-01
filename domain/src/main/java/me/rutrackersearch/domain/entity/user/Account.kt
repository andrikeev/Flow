package me.rutrackersearch.domain.entity.user

data class Account(
    val id: String,
    val name: String,
    val password: String,
    val token: String,
    val avatarUrl: String?,
)
