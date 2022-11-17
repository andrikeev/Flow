package flow.ui.args

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import flow.models.forum.Category
import flow.ui.parcel.CategoryParceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler

@Parcelize
@TypeParceler<Category, CategoryParceler>()
class CategoryWrapper(val category: Category) : Parcelable

fun Category.wrap(): Pair<String, Parcelable> = Key to CategoryWrapper(this)

fun SavedStateHandle.requireCategory(): Category {
    return require<CategoryWrapper>(flow.ui.args.Key).category
}

private const val Key = "category"
