package me.rutrackersearch.domain.usecase

import me.rutrackersearch.models.user.Account
import me.rutrackersearch.domain.repository.AccountRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(account: Account) {
        accountRepository.saveAccount(account)
    }
}
