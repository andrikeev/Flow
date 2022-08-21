package me.rutrackersearch.app.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.rutrackersearch.models.forum.Category

@Composable
fun CategoryListItem(
    modifier: Modifier = Modifier,
    category: Category,
    onClick: () -> Unit,
) {
    Surface(onClick = onClick) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                text = category.name,
                style = MaterialTheme.typography.bodyMedium,
            )
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
            )
        }
    }
}