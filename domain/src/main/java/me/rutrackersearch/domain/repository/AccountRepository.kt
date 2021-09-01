package me.rutrackersearch.domain.repository

import kotlinx.coroutines.flow.Flow
import me.rutrackersearch.domain.entity.user.Account

interface AccountRepository {
    fun observeAccount(): Flow<Account?>
    suspend fun saveAccount(account: Account)
    suspend fun clear()
}
