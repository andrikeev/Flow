package flow.domain.usecase

import flow.common.mapInstanceOf
import flow.models.forum.Category
import flow.models.forum.ForumItem
import flow.models.topic.Topic

fun List<ForumItem>.categories(): List<Category> = mapInstanceOf(ForumItem.Category::category)

fun List<ForumItem>.topics(): List<Topic> = mapInstanceOf(ForumItem.Topic::topic)

fun List<ForumItem>.topicsIds(): List<String> = topics().map(Topic::id)