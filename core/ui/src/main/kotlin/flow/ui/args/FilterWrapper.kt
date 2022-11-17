package flow.ui.args

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import flow.models.search.Filter
import flow.ui.parcel.FilterParceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler

@Parcelize
@TypeParceler<Filter, FilterParceler>()
class FilterWrapper(val filter: Filter) : Parcelable

fun Filter.wrap(): Pair<String, Parcelable> = Key to FilterWrapper(this)

fun SavedStateHandle.requireFilter(): Filter {
    return require<FilterWrapper>(flow.ui.args.Key).filter
}

private const val Key = "filter"
