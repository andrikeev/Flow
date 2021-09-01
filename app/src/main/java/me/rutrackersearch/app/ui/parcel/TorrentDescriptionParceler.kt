package me.rutrackersearch.app.ui.parcel

import android.os.Parcel
import kotlinx.parcelize.Parceler
import me.rutrackersearch.domain.entity.topic.PostContent
import me.rutrackersearch.domain.entity.topic.TorrentDescription

object OptionTorrentDescriptionParceler :
    OptionalParceler<TorrentDescription>(TorrentDescriptionParceler)

object TorrentDescriptionParceler : Parceler<TorrentDescription> {

    override fun create(parcel: Parcel): TorrentDescription {
        return TorrentDescription(PostContent.Default(emptyList()))
    }

    override fun TorrentDescription.write(parcel: Parcel, flags: Int) {
    }
}
