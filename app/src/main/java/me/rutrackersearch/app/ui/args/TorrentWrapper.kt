package me.rutrackersearch.app.ui.args

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import me.rutrackersearch.app.ui.parcel.TorrentParceler
import me.rutrackersearch.models.topic.Torrent

@Parcelize
@TypeParceler<Torrent, TorrentParceler>()
class TorrentWrapper(val torrent: Torrent) : Parcelable

fun Torrent.wrap(): Pair<String, Parcelable> = Key to TorrentWrapper(this.copy(description = null))

fun SavedStateHandle.requireTorrent(): Torrent = require<TorrentWrapper>(Key).torrent

private const val Key = "torrent"
