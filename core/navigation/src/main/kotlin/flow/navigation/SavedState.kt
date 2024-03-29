package flow.navigation

import androidx.lifecycle.SavedStateHandle

inline fun <reified T> SavedStateHandle.require(key: String): T {
    return requireNotNull(get(key)) { "required value '$key' is null or missing" }
}
