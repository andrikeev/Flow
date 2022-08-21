package me.rutrackersearch.app.ui.args

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import me.rutrackersearch.app.ui.parcel.CategoryParceler
import me.rutrackersearch.models.forum.Category

@Parcelize
@TypeParceler<Category, CategoryParceler>()
class CategoryWrapper(val category: Category) : Parcelable

fun Category.wrap(): Pair<String, Parcelable> = Key to CategoryWrapper(this)

fun SavedStateHandle.requireCategory(): Category {
    return require<CategoryWrapper>(Key).category
}

private const val Key = "category"
