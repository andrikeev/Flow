package me.rutrackersearch.app.ui.parcel

import android.os.Parcel
import kotlinx.parcelize.Parceler

object OptionalLongParceler : OptionalParceler<Long>(LongParceler)

object LongParceler : Parceler<Long> {

    override fun create(parcel: Parcel): Long {
        return parcel.readLong()
    }

    override fun Long.write(parcel: Parcel, flags: Int) {
        parcel.writeLong(this)
    }
}
