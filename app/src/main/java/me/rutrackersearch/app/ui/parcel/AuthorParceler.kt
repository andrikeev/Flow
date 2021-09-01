package me.rutrackersearch.app.ui.parcel

import android.os.Parcel
import kotlinx.parcelize.Parceler
import me.rutrackersearch.domain.entity.topic.Author

object OptionalAuthorParceler : OptionalParceler<Author>(AuthorParceler)

object AuthorParceler : Parceler<Author> {

    override fun create(parcel: Parcel): Author {
        return Author(
            id = parcel.read(OptionalStringParceler),
            name = parcel.read(StringParceler),
            avatarUrl = parcel.read(OptionalStringParceler),
        )
    }

    override fun Author.write(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(avatarUrl)
    }
}
