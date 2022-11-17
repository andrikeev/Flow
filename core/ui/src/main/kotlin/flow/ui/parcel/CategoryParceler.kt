package flow.ui.parcel

import android.os.Parcel
import flow.models.forum.Category
import kotlinx.parcelize.Parceler

object OptionalCategoryParceler : OptionalParceler<Category>(CategoryParceler)

object CategoryParceler : Parceler<Category> {

    override fun create(parcel: Parcel): Category {
        return Category(
            id = parcel.read(StringParceler),
            name = parcel.read(StringParceler),
        )
    }

    override fun Category.write(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
    }
}
