package me.rutrackersearch.app.ui.topic.topic

import me.rutrackersearch.app.ui.common.PageResult
import me.rutrackersearch.models.topic.Post
import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.models.topic.TopicModel

data class TopicState(
    val topic: TopicModel<Topic>,
    val page: Int = 0,
    val pages: Int = 0,
    val content: PageResult<List<Post>> = PageResult.Loading(),
)
