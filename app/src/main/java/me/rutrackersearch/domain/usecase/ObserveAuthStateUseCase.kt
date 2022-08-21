package me.rutrackersearch.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.rutrackersearch.models.user.Account
import me.rutrackersearch.models.user.AuthState
import me.rutrackersearch.models.user.AuthState.Authorized
import me.rutrackersearch.models.user.AuthState.Unauthorized
import me.rutrackersearch.domain.repository.AccountRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ObserveAuthStateUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(): Flow<AuthState> {
        return accountRepository.observeAccount()
            .map(::mapToState)
    }

    private fun mapToState(account: Account?): AuthState {
        return account?.let(::Authorized) ?: Unauthorized
    }
}
