package me.rutrackersearch.app.ui.forum.forumtree

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import me.rutrackersearch.app.ui.common.Error
import me.rutrackersearch.app.ui.common.LazyColumn
import me.rutrackersearch.app.ui.common.Loading
import me.rutrackersearch.app.ui.common.dividedItems
import me.rutrackersearch.app.ui.forum.forumtree.ForumTreeAction.CategoryClick
import me.rutrackersearch.app.ui.forum.forumtree.ForumTreeAction.ExpandClick
import me.rutrackersearch.app.ui.forum.forumtree.ForumTreeAction.RetryClick
import me.rutrackersearch.domain.entity.forum.Category

@Composable
fun ForumTreeScreen(
    selectedState: SnapshotStateList<Category>,
) {
    ForumTreeScreen(
        treeViewModel = hiltViewModel(),
        selectedState = selectedState,
    )
}

@Composable
private fun ForumTreeScreen(
    treeViewModel: ForumTreeViewModel,
    selectedState: SnapshotStateList<Category>,
) {
    val state by treeViewModel.state.collectAsState()
    val onAction: (ForumTreeAction) -> Unit = { action ->
        when (action) {
            is CategoryClick -> {
                if (selectedState.contains(action.category)) {
                    selectedState.remove(action.category)
                } else {
                    selectedState.add(action.category)
                }
            }
            else -> treeViewModel.perform(action)
        }
    }
    ForumTreeScreen(
        state = state,
        selected = selectedState,
        onAction = onAction,
    )
}

@Composable
private fun ForumTreeScreen(
    state: ForumTreeState,
    selected: List<Category>,
    onAction: (ForumTreeAction) -> Unit,
) {
    when (state) {
        is ForumTreeState.Loading -> Loading()
        is ForumTreeState.Error -> Error(
            error = state.error,
            onRetryClick = { onAction(RetryClick) },
        )
        is ForumTreeState.Loaded -> LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp),
        ) {
            dividedItems(
                items = state.forum,
                key = ForumTreeItem::id,
                contentType = { it::class },
            ) { item ->
                ForumTreeItem(
                    item = item,
                    selected = selected,
                    onExpandClick = { onAction(ExpandClick(it)) },
                    onCategoryClick = { onAction(CategoryClick(it)) },
                )
            }
        }
    }
}

@Composable
private fun ForumTreeItem(
    modifier: Modifier = Modifier,
    item: ForumTreeItem,
    selected: List<Category>,
    onExpandClick: (Expandable) -> Unit,
    onCategoryClick: (Category) -> Unit,
) {
    val isExpandable: Boolean
    val isExpanded: Boolean
    val isSelectable: Boolean
    val isSelected: Boolean
    val padding: Dp
    val onClick: () -> Unit
    when (item) {
        is ExpandableForumTreeRootGroup -> {
            isExpandable = true
            isExpanded = item.expanded
            isSelectable = false
            isSelected = false
            padding = 0.dp
            onClick = { onExpandClick(item) }
        }
        is ExpandableForumTreeGroup -> {
            isExpandable = item.expandable
            isExpanded = item.expanded
            isSelectable = false
            isSelected = false
            padding = 24.dp
            onClick = { onExpandClick(item) }
        }
        is ForumTreeCategory -> {
            isExpandable = false
            isExpanded = false
            isSelectable = true
            isSelected = selected.contains(Category(item.id, item.name))
            padding = 48.dp
            onClick = { onCategoryClick(Category(item.id, item.name)) }
        }
    }
    val color by animateColorAsState(
        with(MaterialTheme.colorScheme) {
            if (isExpanded) surfaceVariant else Color.Unspecified
        }
    )
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),
        onClick = onClick,
        color = color,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = padding, end = 8.dp),
                text = item.name,
                style = MaterialTheme.typography.bodyMedium,
            )
            when {
                isExpandable -> {
                    Crossfade(targetState = isExpanded) { isExpanded ->
                        val rotation by animateFloatAsState(
                            targetValue = if (isExpanded) 180f else 0f
                        )
                        Icon(
                            modifier = Modifier.rotate(rotation),
                            imageVector = Icons.Outlined.ExpandMore,
                            contentDescription = null,
                        )
                    }
                }
                isSelectable -> {
                    Crossfade(targetState = isSelected) { isSelected ->
                        Icon(
                            imageVector = if (isSelected) {
                                Icons.Outlined.CheckBox
                            } else {
                                Icons.Outlined.CheckBoxOutlineBlank
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}
