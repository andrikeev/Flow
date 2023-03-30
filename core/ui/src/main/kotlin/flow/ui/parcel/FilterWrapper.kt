package flow.ui.parcel

import android.os.Parcel
import android.os.Parcelable
import flow.models.search.Filter
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler

@Parcelize
@TypeParceler<Filter, FilterParceler>()
class FilterWrapper(val filter: Filter) : Parcelable

internal object FilterParceler : Parceler<Filter> {
    override fun create(parcel: Parcel) = Filter(
        query = parcel.read(OptionalStringParceler),
        sort = parcel.requireEnum(),
        order = parcel.requireEnum(),
        period = parcel.requireEnum(),
        author = parcel.read(OptionalAuthorParceler),
        categories = parcel.readList(CategoryParceler),
    )

    override fun Filter.write(parcel: Parcel, flags: Int) {
        parcel.writeString(query)
        parcel.writeEnum(sort)
        parcel.writeEnum(order)
        parcel.writeEnum(period)
        parcel.write(author, OptionalAuthorParceler, flags)
        parcel.writeList(categories, CategoryParceler, flags)
    }
}
