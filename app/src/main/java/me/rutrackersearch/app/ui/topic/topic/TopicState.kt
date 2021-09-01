package me.rutrackersearch.app.ui.topic.topic

import me.rutrackersearch.app.ui.common.PageResult
import me.rutrackersearch.domain.entity.TopicModel
import me.rutrackersearch.domain.entity.topic.Post
import me.rutrackersearch.domain.entity.topic.Topic

data class TopicState(
    val topic: TopicModel<Topic>,
    val page: Int = 0,
    val pages: Int = 0,
    val content: PageResult<List<Post>> = PageResult.Loading(),
)
