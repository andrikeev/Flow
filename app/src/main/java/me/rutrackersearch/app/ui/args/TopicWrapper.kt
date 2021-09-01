package me.rutrackersearch.app.ui.args

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import me.rutrackersearch.app.ui.parcel.TopicParceler
import me.rutrackersearch.domain.entity.topic.Topic

@Parcelize
@TypeParceler<Topic, TopicParceler>()
class TopicWrapper(val topic: Topic) : Parcelable

fun Topic.wrap(): Pair<String, Parcelable> = Key to TopicWrapper(this)

fun SavedStateHandle.requireTopic(): Topic = require<TopicWrapper>(Key).topic

private const val Key = "topic"
