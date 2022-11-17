package flow.auth.impl

import flow.auth.api.AuthRepository
import flow.models.user.Account
import flow.models.user.AuthState
import flow.securestorage.SecureStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AuthRepositoryImpl @Inject constructor(
    private val secureStorage: SecureStorage,
) : AuthRepository {
    private val mutableAuthState = MutableStateFlow(initAuthState())

    override fun isAuthorized(): Boolean = mutableAuthState.value is AuthState.Authorized

    override fun getToken(): String? = secureStorage.getAccount()?.token

    override fun observeAuthState(): Flow<AuthState> = mutableAuthState.asStateFlow()

    override suspend fun saveAccount(account: Account) {
        secureStorage.saveAccount(account)
        mutableAuthState.emit(AuthState.Authorized(account.name, account.avatarUrl))
    }

    override suspend fun getAccount(): Account? = secureStorage.getAccount()

    override suspend fun clear() {
        secureStorage.clearAccount()
        mutableAuthState.emit(AuthState.Unauthorized)
    }

    private fun initAuthState(): AuthState {
        val account = secureStorage.getAccount()
        return if (account != null) {
            AuthState.Authorized(account.name, account.avatarUrl)
        } else {
            AuthState.Unauthorized
        }
    }
}
