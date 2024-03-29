package flow.domain.usecase

import flow.data.api.repository.ForumRepository
import flow.dispatchers.api.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EnsureForumLoadUseCase @Inject constructor(
    private val refreshForumUseCase: RefreshForumUseCase,
    private val forumRepository: ForumRepository,
    private val dispatchers: Dispatchers,
) {
    suspend operator fun invoke() {
        withContext(dispatchers.default) {
            if (!forumRepository.isNotEmpty() || !forumRepository.isForumFresh()) {
                refreshForumUseCase.invoke()
            }
        }
    }
}
