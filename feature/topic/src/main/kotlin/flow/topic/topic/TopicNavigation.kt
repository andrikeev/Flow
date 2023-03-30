package flow.topic.topic

import androidx.lifecycle.SavedStateHandle
import flow.models.topic.Topic
import flow.navigation.NavigationController
import flow.navigation.model.NavigationGraphBuilder
import flow.navigation.ui.NavigationAnimations
import flow.navigation.viewModel
import flow.ui.args.require
import flow.ui.parcel.TopicWrapper

private const val TopicKey = "Topic"

private val NavigationGraphBuilder.TopicRoute
    get() = route("Topic")

data class TopicNavigation(
    val addTopic: NavigationGraphBuilder.(
        back: () -> Unit,
        openLogin: () -> Unit,
        animations: NavigationAnimations,
    ) -> Unit,
    val openTopic: NavigationController.(Topic) -> Unit,
)

fun NavigationGraphBuilder.buildTopicNavigation() = TopicNavigation(
    addTopic = NavigationGraphBuilder::addTopic,
    openTopic = { topic ->
        navigate(TopicRoute) {
            putParcelable(TopicKey, TopicWrapper(topic))
        }
    },
)

private fun NavigationGraphBuilder.addTopic(
    back: () -> Unit,
    openLogin: () -> Unit,
    animations: NavigationAnimations,
) = addDestination(
    route = TopicRoute,
    animations = animations,
) {
    TopicScreen(
        viewModel = viewModel(),
        back = back,
        openLogin = openLogin,
    )
}

internal val SavedStateHandle.topic: Topic get() = require<TopicWrapper>(TopicKey).topic
