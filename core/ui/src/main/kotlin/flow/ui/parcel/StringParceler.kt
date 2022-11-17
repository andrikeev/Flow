package flow.ui.parcel

import android.os.Parcel
import kotlinx.parcelize.Parceler

object OptionalStringParceler : OptionalParceler<String>(StringParceler)

object StringParceler : Parceler<String> {

    override fun create(parcel: Parcel): String {
        return parcel.requireString()
    }

    override fun String.write(parcel: Parcel, flags: Int) {
        parcel.writeString(this)
    }
}
