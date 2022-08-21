package me.rutrackersearch.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import me.rutrackersearch.auth.AuthObservable
import me.rutrackersearch.data.security.SecureStorageFactory
import me.rutrackersearch.data.utils.clear
import me.rutrackersearch.data.utils.edit
import me.rutrackersearch.domain.repository.AccountRepository
import me.rutrackersearch.models.user.Account
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepositoryImpl @Inject constructor(
    secureStorageFactory: SecureStorageFactory,
) : AccountRepository, AuthObservable {
    private val preferences = secureStorageFactory.getSharedPreferences("account")
    private val mutableAccount = MutableStateFlow(readAccount())

    override fun observeAccount(): Flow<Account?> = mutableAccount

    override suspend fun saveAccount(account: Account) {
        preferences.edit {
            putString(accountIdKey, account.id)
            putString(accountUsernameKey, account.name)
            putString(accountPasswordKey, account.password)
            putString(accountTokenKey, account.token)
            putString(accountAvatarKey, account.avatarUrl)
        }
        mutableAccount.emit(account)
    }

    override suspend fun clear() {
        preferences.clear()
        mutableAccount.emit(null)
    }

    override fun observeAuthStatusChanged(): Flow<Boolean> {
        return observeAccount()
            .map { account -> account != null }
            .distinctUntilChanged()
    }

    override val authorised: Boolean
        get() = mutableAccount.value != null

    override val token: String?
        get() = mutableAccount.value?.token

    private fun readAccount(): Account? {
        return try {
            val id = preferences.getString(accountIdKey, null)
            val username = preferences.getString(accountUsernameKey, null)
            val token = preferences.getString(accountTokenKey, null)
            val password = preferences.getString(accountPasswordKey, null)
            if (id != null && username != null && token != null && password != null) {
                Account(
                    id = id,
                    name = username,
                    token = token,
                    password = password,
                    avatarUrl = preferences.getString(accountAvatarKey, null),
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private companion object {
        const val accountIdKey = "account_id"
        const val accountUsernameKey = "account_username"
        const val accountPasswordKey = "account_password"
        const val accountTokenKey = "account_token"
        const val accountAvatarKey = "account_avatar_url"
    }
}
