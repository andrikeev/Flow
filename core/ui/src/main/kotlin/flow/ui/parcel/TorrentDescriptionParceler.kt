package flow.ui.parcel

import android.os.Parcel
import flow.models.topic.PostContent
import flow.models.topic.TorrentDescription
import kotlinx.parcelize.Parceler

object OptionTorrentDescriptionParceler :
    OptionalParceler<TorrentDescription>(TorrentDescriptionParceler)

object TorrentDescriptionParceler : Parceler<TorrentDescription> {

    override fun create(parcel: Parcel): TorrentDescription {
        return TorrentDescription(PostContent.Default(emptyList()))
    }

    override fun TorrentDescription.write(parcel: Parcel, flags: Int) {
    }
}
