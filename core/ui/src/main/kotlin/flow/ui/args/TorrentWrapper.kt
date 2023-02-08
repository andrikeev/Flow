package flow.ui.args

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import flow.models.topic.Torrent
import flow.ui.parcel.TorrentParceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler

@Parcelize
@TypeParceler<Torrent, TorrentParceler>()
class TorrentWrapper(val torrent: Torrent) : Parcelable

fun Torrent.wrap(): Pair<String, Parcelable> = Key to TorrentWrapper(this)

fun SavedStateHandle.requireTorrent(): Torrent = require<TorrentWrapper>(Key).torrent

private const val Key = "torrent"
