package me.rutrackersearch.app.ui.search.input

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.NorthWest
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.job
import me.rutrackersearch.app.R
import me.rutrackersearch.app.ui.common.AppBar
import me.rutrackersearch.app.ui.common.BackButton
import me.rutrackersearch.app.ui.common.IconButton
import me.rutrackersearch.app.ui.search.input.SearchInputAction.BackClick
import me.rutrackersearch.app.ui.search.input.SearchInputAction.ClearInputClick
import me.rutrackersearch.app.ui.search.input.SearchInputAction.InputChanged
import me.rutrackersearch.app.ui.search.input.SearchInputAction.SubmitClick
import me.rutrackersearch.app.ui.search.input.SearchInputAction.SuggestClick
import me.rutrackersearch.app.ui.search.input.SearchInputAction.SuggestSelected
import me.rutrackersearch.domain.entity.search.Filter
import me.rutrackersearch.domain.entity.search.Suggest

@Composable
fun SearchInputScreen(
    onBackClick: () -> Unit,
    onSubmit: (Filter) -> Unit,
) {
    SearchInputScreen(
        viewModel = hiltViewModel(),
        onBackClick = onBackClick,
        onSubmit = onSubmit,
    )
}

@Composable
private fun SearchInputScreen(
    viewModel: SearchInputViewModel,
    onBackClick: () -> Unit,
    onSubmit: (Filter) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    SearchInputScreen(state) { action ->
        when (action) {
            BackClick -> onBackClick()
            is SubmitClick -> {
                viewModel.perform(action)
                onSubmit(state.filter.copy(query = action.query))
            }
            is SuggestClick -> {
                viewModel.perform(action)
                onSubmit(state.filter.copy(query = action.suggest.value))
            }
            ClearInputClick -> viewModel.perform(action)
            is InputChanged -> viewModel.perform(action)
            is SuggestSelected -> viewModel.perform(action)
        }
    }
}

@Composable
private fun SearchInputScreen(
    state: SearchInputState,
    onAction: (SearchInputAction) -> Unit,
) {
    val (_, searchInput, suggests) = state
    val pinnedScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(pinnedScrollBehavior.nestedScrollConnection),
        topBar = {
            AppBar(
                navigationIcon = { BackButton { onAction(BackClick) } },
                title = {
                    val keyboardController = LocalSoftwareKeyboardController.current
                    val focusRequester = remember { FocusRequester() }
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        value = searchInput,
                        placeholder = { Text(stringResource(R.string.search_input_hint)) },
                        onValueChange = { onAction(InputChanged(it)) },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        trailingIcon = {
                            AnimatedVisibility(
                                visible = state.isClearButtonVisible,
                                enter = fadeIn() + expandIn(expandFrom = Alignment.Center),
                                exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.Center),
                            ) {
                                IconButton(
                                    onClick = { onAction(ClearInputClick) },
                                    imageVector = Icons.Outlined.Cancel,
                                )
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            autoCorrect = true,
                            imeAction = ImeAction.Search,
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                onAction(SubmitClick(state.searchInput.text.takeIf(String::isNotBlank)))
                                keyboardController?.hide()
                            }
                        )
                    )
                    LaunchedEffect(Unit) {
                        coroutineContext.job.invokeOnCompletion { error ->
                            if (error == null) {
                                focusRequester.requestFocus()
                            }
                        }
                    }
                },
                scrollBehavior = pinnedScrollBehavior,
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.padding(it),
            contentPadding = PaddingValues(vertical = 8.dp),
        ) {
            items(
                items = suggests,
                key = { item -> item.value }
            ) { item ->
                SuggestItem(
                    suggest = item,
                    onClick = { onAction(SuggestClick(item)) },
                    onSubmit = { onAction(SuggestSelected(item)) },
                )
            }
        }
    }
}

@Composable
private fun SuggestItem(
    suggest: Suggest,
    onClick: () -> Unit,
    onSubmit: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Outlined.History, contentDescription = null)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                SuggestText(value = suggest.value, substring = suggest.substring)
            }
            IconButton(
                onClick = onSubmit,
                imageVector = Icons.Outlined.NorthWest,
            )
        }
    }
}

@Composable
private fun SuggestText(value: String, substring: IntRange?) {
    if (substring == null) {
        Text(value)
    } else {
        val substringStartIndex = substring.first
        val substringEndIndex = substring.last
        Text(
            text = buildAnnotatedString {
                if (substringStartIndex > 0) {
                    append(value.substring(0, substringStartIndex))
                }
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.tertiary)) {
                    append(value.substring(substring))
                }
                if (value.lastIndex > substringEndIndex) {
                    append(value.substring(substringEndIndex + 1))
                }
            }
        )
    }
}
