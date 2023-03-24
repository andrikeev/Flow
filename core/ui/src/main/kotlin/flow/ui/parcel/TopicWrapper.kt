package flow.ui.parcel

import android.os.Parcel
import android.os.Parcelable
import flow.models.topic.BaseTopic
import flow.models.topic.Topic
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler

@Parcelize
@TypeParceler<Topic, TopicParceler>()
class TopicWrapper(val topic: Topic) : Parcelable

private object TopicParceler : Parceler<Topic> {
    override fun create(parcel: Parcel) = BaseTopic(
        id = parcel.requireString(),
        title = parcel.requireString(),
        author = parcel.read(OptionalAuthorParceler),
        category = parcel.read(CategoryParceler),
    )

    override fun Topic.write(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.write(author, OptionalAuthorParceler, flags)
        parcel.write(category, OptionalCategoryParceler, flags)
    }
}
