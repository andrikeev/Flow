package flow.testing.repository

import flow.auth.api.AuthRepository
import flow.models.user.Account
import flow.models.user.AuthState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class TestAuthRepository : AuthRepository {
    private val mutableStateFlow = MutableStateFlow<AuthState>(AuthState.Unauthorized)
    private var mutableAccount: Account? = null

    override fun getToken(): String? = mutableAccount?.token

    override fun isAuthorized(): Boolean = mutableAccount != null

    override fun observeAuthState(): Flow<AuthState> = mutableStateFlow

    override suspend fun saveAccount(account: Account) {
        mutableAccount = account
        mutableStateFlow.value = AuthState.Authorized(account.name, account.avatarUrl)
    }

    override suspend fun getAccount(): Account? = mutableAccount

    override suspend fun clear() {
        mutableAccount = null
        mutableStateFlow.value = AuthState.Unauthorized
    }

    companion object {
        val TestAccount = Account(
            id = "123",
            name = "Test User",
            password = "qwerty",
            token = "xz#abrag/dvkmapo3",
            avatarUrl = null,
        )
    }
}
