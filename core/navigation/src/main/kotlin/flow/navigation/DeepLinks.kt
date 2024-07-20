package flow.navigation

import android.net.Uri
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

@Stable
interface DeepLinks {
    var initialDeepLink: Uri?
    var deepLink: Uri?

    companion object {
        val Default = object : DeepLinks {
            override var initialDeepLink: Uri? = null
            override var deepLink: Uri? = null
        }
    }
}

fun DeepLinks(): DeepLinks {
    return object : DeepLinks {
        override var initialDeepLink: Uri? by mutableStateOf(null)
        override var deepLink: Uri? by mutableStateOf(null)
    }
}

val LocalDeepLinks = staticCompositionLocalOf { DeepLinks.Default }
