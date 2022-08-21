package me.rutrackersearch.app.ui.args

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import me.rutrackersearch.app.ui.parcel.FilterParceler
import me.rutrackersearch.models.search.Filter

@Parcelize
@TypeParceler<Filter, FilterParceler>()
class FilterWrapper(val filter: Filter) : Parcelable

fun Filter.wrap(): Pair<String, Parcelable> = Key to FilterWrapper(this)

fun SavedStateHandle.requireFilter(): Filter {
    return require<FilterWrapper>(Key).filter
}

private const val Key = "filter"
