package flow.domain.usecase

import flow.common.mapInstanceOf
import flow.models.forum.ForumItem
import flow.models.topic.Topic

internal fun List<ForumItem>.topics(): List<Topic> = mapInstanceOf(ForumItem.Topic::topic)
internal fun List<ForumItem>.topicsIds(): List<String> = topics().map(Topic::id)
