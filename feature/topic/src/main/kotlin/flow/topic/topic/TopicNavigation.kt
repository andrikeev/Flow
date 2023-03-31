package flow.topic.topic

import androidx.lifecycle.SavedStateHandle
import flow.models.topic.Topic
import flow.navigation.NavigationController
import flow.navigation.model.NavigationGraphBuilder
import flow.navigation.model.buildRoute
import flow.navigation.require
import flow.navigation.ui.NavigationAnimations
import flow.navigation.viewModel
import flow.ui.parcel.TopicWrapper

private const val TopicKey = "topic"
private const val TopicRoute = "topic"

context(NavigationGraphBuilder)
fun addTopic(
    back: () -> Unit,
    openLogin: () -> Unit,
    animations: NavigationAnimations,
) = addDestination(
    route = buildRoute(TopicRoute),
    animations = animations,
) {
    TopicScreen(
        viewModel = viewModel(),
        back = back,
        openLogin = openLogin,
    )
}

context(NavigationGraphBuilder, NavigationController)
fun openTopic(topic: Topic) {
    navigate(buildRoute(TopicRoute)) {
        putParcelable(TopicKey, TopicWrapper(topic))
    }
}

internal val SavedStateHandle.topic: Topic get() = require<TopicWrapper>(TopicKey).topic
