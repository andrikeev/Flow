package flow.topic.topic

import flow.models.topic.Post
import flow.models.topic.Topic
import flow.models.topic.TopicModel
import flow.ui.component.PageResult

data class TopicState(
    val topic: TopicModel<Topic>,
    val page: Int = 0,
    val pages: Int = 0,
    val content: PageResult<List<Post>> = PageResult.Loading(),
)
