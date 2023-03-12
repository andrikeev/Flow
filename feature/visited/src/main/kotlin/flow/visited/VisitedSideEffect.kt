package flow.visited

import flow.models.topic.Topic
import flow.models.topic.Torrent

internal sealed interface VisitedSideEffect {
    data class OpenTopic(val topic: Topic) : VisitedSideEffect
    data class OpenTorrent(val torrent: Torrent) : VisitedSideEffect
}
