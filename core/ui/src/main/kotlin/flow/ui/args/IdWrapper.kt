package flow.ui.args

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import kotlinx.parcelize.Parcelize

@Parcelize
class IdWrapper(val id: String) : Parcelable

fun String?.wrapId(): Pair<String, Parcelable> = KeyId to IdWrapper(this.orEmpty())

fun String?.wrapPid(): Pair<String, Parcelable> = KeyPid to IdWrapper(this.orEmpty())

fun SavedStateHandle.requireId(): String {
    return require<IdWrapper>(KeyId).id
}

fun SavedStateHandle.requirePid(): String {
    return require<IdWrapper>(KeyPid).id
}

private const val KeyId = "id"
private const val KeyPid = "pid"
