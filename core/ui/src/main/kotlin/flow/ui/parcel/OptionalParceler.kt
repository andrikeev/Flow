package flow.ui.parcel

import android.os.Parcel
import kotlinx.parcelize.Parceler

open class OptionalParceler<T>(private val parceler: Parceler<T>) : Parceler<T?> {

    override fun create(parcel: Parcel): T? {
        return if (parcel.readInt() == 1) {
            parceler.create(parcel)
        } else {
            null
        }
    }

    override fun T?.write(parcel: Parcel, flags: Int) {
        if (this != null) {
            parcel.writeInt(1)
            with(parceler) {
                write(parcel, flags)
            }
        } else {
            parcel.writeInt(0)
        }
    }
}
