package me.rutrackersearch.app.ui.parcel

import android.os.Parcel
import kotlinx.parcelize.Parceler

object OptionalIntParceler : OptionalParceler<Int>(IntParceler)

object IntParceler : Parceler<Int> {

    override fun create(parcel: Parcel): Int {
        return parcel.readInt()
    }

    override fun Int.write(parcel: Parcel, flags: Int) {
        parcel.writeInt(this)
    }
}
