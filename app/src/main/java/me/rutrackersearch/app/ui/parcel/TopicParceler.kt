package me.rutrackersearch.app.ui.parcel

import android.os.Parcel
import kotlinx.parcelize.Parceler
import me.rutrackersearch.models.topic.BaseTopic
import me.rutrackersearch.models.topic.Topic

object TopicParceler : Parceler<Topic> {
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
