package flow.ui.args

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import flow.models.topic.Topic
import flow.ui.parcel.TopicParceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler

@Parcelize
@TypeParceler<Topic, TopicParceler>()
class TopicWrapper(val topic: Topic) : Parcelable

fun Topic.wrap(): Pair<String, Parcelable> = Key to TopicWrapper(this)

fun SavedStateHandle.requireTopic(): Topic = require<TopicWrapper>(Key).topic

private const val Key = "topic"
