package me.rutrackersearch.app.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

object Borders {
    val thin
        @Composable
        get() = BorderStroke(
            width = Dp.Hairline,
            color = MaterialTheme.colorScheme.outline
        )
}

@Suppress("unused")
val MaterialTheme.borders
    get() = Borders
