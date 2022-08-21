package me.rutrackersearch.auth.models

data class Captcha(
    val id: String,
    val code: String,
    val url: String,
)
