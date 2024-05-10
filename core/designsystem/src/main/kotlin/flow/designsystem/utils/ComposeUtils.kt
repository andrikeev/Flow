package flow.designsystem.utils

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import flow.designsystem.theme.AppTheme
import kotlinx.coroutines.job

@Composable
fun RunOnFirstComposition(block: () -> Unit) {
    LaunchedEffect(Unit) {
        coroutineContext.job.invokeOnCompletion { error ->
            if (error == null) {
                block()
            }
        }
    }
}

@Composable
fun rememberSystemBarStyle(): SystemBarStyle {
    val isDark = AppTheme.colors.isDark
    return remember(isDark) {
        if (isDark) {
            SystemBarStyle.dark(Color.TRANSPARENT)
        } else {
            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        }
    }
}

val Context.componentActivity: ComponentActivity
    get() {
        return when (this) {
            is ComponentActivity -> this
            is ContextWrapper -> this.baseContext.componentActivity
            else -> error("No ComponentActivity provided")
        }
    }
