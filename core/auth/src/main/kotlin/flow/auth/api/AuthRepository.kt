package flow.auth.api

import flow.models.user.Account
import flow.models.user.AuthState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getToken(): String?
    fun isAuthorized(): Boolean
    fun observeAuthState(): Flow<AuthState>
    suspend fun saveAccount(account: Account)
    suspend fun getAccount(): Account?
    suspend fun clear()
}
