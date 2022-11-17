package flow.ui.args

import androidx.lifecycle.SavedStateHandle

inline fun <reified T> SavedStateHandle.require(key: String): T {
    return checkNotNull(get(key)) { "required value '$key' is null or missing" }
}
