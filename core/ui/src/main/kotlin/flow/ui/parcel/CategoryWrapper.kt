package flow.ui.parcel

import android.os.Parcelable
import flow.models.forum.Category
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler

@Parcelize
@TypeParceler<Category, CategoryParceler>()
class CategoryWrapper(val category: Category) : Parcelable
