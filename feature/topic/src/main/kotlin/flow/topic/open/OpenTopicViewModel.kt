package flow.topic.open

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.domain.usecase.GetTopicUseCase
import flow.logger.api.LoggerFactory
import flow.models.topic.BaseTopic
import flow.models.topic.Content
import flow.models.topic.PostContent
import flow.models.topic.Torrent
import flow.models.topic.isValid
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
internal class OpenTopicViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTopicUseCase: GetTopicUseCase,
    loggerFactory: LoggerFactory,
) : ViewModel(), ContainerHost<OpenTopicState, Unit> {
    private val logger = loggerFactory.get("OpenTopicViewModel")
    private val id = savedStateHandle.id
    private val showComments = savedStateHandle.showComments

    override val container: Container<OpenTopicState, Unit> = container(
        initialState = OpenTopicState.Loading,
        onCreate = { loadTopic() },
    )

    fun retry() = loadTopic()

    private fun loadTopic() {
        logger.d { "Start loading topic with id=$id (showComments=$showComments)" }
        intent { reduce { OpenTopicState.Loading } }
        viewModelScope.launch {
            runCatching { coroutineScope { getTopicUseCase(id) } }
                .onSuccess { topic ->
                    logger.d { "Topic loaded: $topic" }
                    intent {
                        if (showComments) {
                            reduce { OpenTopicState.Topic(topic.title) }
                        } else {
                            when (topic) {
                                is BaseTopic -> reduce { OpenTopicState.Topic(topic.title) }
                                is Torrent -> reduce {
                                    OpenTopicState.Torrent(
                                        title = topic.title,
                                        posterImage = topic.posterUrl,
                                        author = topic.author,
                                        category = topic.category,
                                        status = topic.status,
                                        date = topic.date,
                                        size = topic.size,
                                        seeds = topic.seeds,
                                        leeches = topic.leeches,
                                        magnetLink = topic.magnetLink,
                                        description = topic.description,
                                        showMagnetLink = topic.status.isValid(),
                                        showTorrentFile = topic.status.isValid(),
                                    )
                                }
                            }
                        }
                    }
                }
                .onFailure { error ->
                    logger.e(error) { "Error loading topic with id=$id" }
                    intent { reduce { OpenTopicState.Error(error) } }
                }
        }
    }

    private val Torrent.posterUrl: String?
        get() = description?.content?.torrentMainImage()?.src

    private fun Content.torrentMainImage(): PostContent.TorrentMainImage? {
        return when (this) {
            is PostContent.TorrentMainImage -> this
            is PostContent.Default -> children.firstNotNullOfOrNull { it.torrentMainImage() }
            else -> null
        }
    }
}
