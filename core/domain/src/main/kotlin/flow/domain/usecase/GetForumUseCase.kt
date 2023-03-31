package flow.domain.usecase

import flow.data.api.repository.ForumRepository
import flow.dispatchers.api.Dispatchers
import flow.models.forum.Forum
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetForumUseCase @Inject constructor(
    private val ensureForumLoadUseCase: EnsureForumLoadUseCase,
    private val forumRepository: ForumRepository,
    private val dispatchers: Dispatchers,
) {
    suspend operator fun invoke(): Forum {
        return withContext(dispatchers.default) {
            ensureForumLoadUseCase()
            forumRepository.getForum()
        }
    }
}
