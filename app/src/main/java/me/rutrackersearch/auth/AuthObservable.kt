package me.rutrackersearch.auth

import kotlinx.coroutines.flow.Flow

interface AuthObservable {
    fun observeAuthStatusChanged(): Flow<Boolean>
    val authorised: Boolean
    val token: String?
}
