package flow.ui.parcel

import android.os.Parcel
import android.os.Parcelable
import flow.models.topic.Torrent
import flow.models.topic.TorrentStatus
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler

@Parcelize
@TypeParceler<Torrent, TorrentParceler>()
class TorrentWrapper(val torrent: Torrent) : Parcelable

private object TorrentParceler : Parceler<Torrent> {
    override fun create(parcel: Parcel) = Torrent(
        id = parcel.requireString(),
        title = parcel.requireString(),
        author = parcel.read(OptionalAuthorParceler),
        category = parcel.read(CategoryParceler),
        tags = parcel.read(OptionalStringParceler),
        status = parcel.readEnum<TorrentStatus>(),
        date = parcel.read(OptionalLongParceler),
        size = parcel.read(OptionalStringParceler),
        seeds = parcel.read(OptionalIntParceler),
        leeches = parcel.read(OptionalIntParceler),
        magnetLink = parcel.read(OptionalStringParceler),
    )

    override fun Torrent.write(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.write(author, OptionalAuthorParceler, flags)
        parcel.write(category, OptionalCategoryParceler, flags)
        parcel.write(tags, OptionalStringParceler, flags)
        parcel.writeEnum(status)
        parcel.write(date, OptionalLongParceler, flags)
        parcel.write(size, OptionalStringParceler, flags)
        parcel.write(seeds, OptionalIntParceler, flags)
        parcel.write(leeches, OptionalIntParceler, flags)
        parcel.write(magnetLink, OptionalStringParceler, flags)
    }
}
