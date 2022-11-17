package flow.search.input

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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import flow.designsystem.component.AppBar
import flow.designsystem.component.AppBarDefaults
import flow.designsystem.component.AppBarScrollBehavior
import flow.designsystem.component.BackButton
import flow.designsystem.component.IconButton
import flow.designsystem.component.RunOnComposition
import flow.designsystem.component.Scaffold
import flow.designsystem.component.rememberFocusRequester
import flow.designsystem.drawables.FlowIcons
import flow.models.search.Filter
import flow.models.search.Suggest
import flow.search.R
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SearchInputScreen(
    back: () -> Unit,
    openSearch: (Filter) -> Unit,
) {
    SearchInputScreen(
        viewModel = hiltViewModel(),
        back = back,
        openSearch = openSearch,
    )
}

@Composable
private fun SearchInputScreen(
    viewModel: SearchInputViewModel,
    back: () -> Unit,
    openSearch: (Filter) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SearchInputSideEffect.Back -> back()
            is SearchInputSideEffect.HideKeyboard -> keyboardController?.hide()
            is SearchInputSideEffect.OpenSearch -> openSearch(sideEffect.filter)
        }
    }
    val state by viewModel.collectAsState()
    SearchInputScreen(
        state = state,
        onAction = viewModel::perform,
    )
}

@Composable
private fun SearchInputScreen(
    state: SearchInputState,
    onAction: (SearchInputAction) -> Unit,
) {
    val scrollBehavior = AppBarDefaults.appBarScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SearchInputAppBar(
                inputValue = state.searchInput,
                onInputValueChange = { onAction(SearchInputAction.InputChanged(it)) },
                showClearButton = state.showClearButton,
                onClearButtonClick = { onAction(SearchInputAction.ClearInputClick) },
                onSubmitClick = { onAction(SearchInputAction.SubmitClick) },
                onBackClick = { onAction(SearchInputAction.BackClick) },
                scrollBehavior = scrollBehavior,
            )
        },
        content = { padding ->
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(vertical = 8.dp),
            ) {
                items(
                    items = state.suggests,
                    key = { item -> item.value }
                ) { item ->
                    SuggestItem(
                        suggest = item,
                        onClick = { onAction(SearchInputAction.SuggestClick(item)) },
                        onSubmit = { onAction(SearchInputAction.SuggestSelected(item)) },
                    )
                }
            }
        },
    )
}

@Composable
private fun SearchInputAppBar(
    modifier: Modifier = Modifier,
    inputValue: TextFieldValue,
    onInputValueChange: (TextFieldValue) -> Unit,
    showClearButton: Boolean,
    onClearButtonClick: () -> Unit,
    onSubmitClick: () -> Unit,
    onBackClick: () -> Unit,
    scrollBehavior: AppBarScrollBehavior,
) {
    AppBar(
        modifier = modifier,
        navigationIcon = { BackButton(onBackClick) },
        title = {
            val focusRequester = rememberFocusRequester()
            RunOnComposition { focusRequester.requestFocus() }
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                value = inputValue,
                placeholder = { Text(stringResource(R.string.search_screen_input_hint)) },
                onValueChange = onInputValueChange,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                trailingIcon = {
                    AnimatedVisibility(
                        visible = showClearButton,
                        enter = fadeIn() + expandIn(expandFrom = Alignment.Center),
                        exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.Center),
                    ) {
                        IconButton(
                            onClick = onClearButtonClick,
                            imageVector = FlowIcons.Clear,
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
                    onSearch = { onSubmitClick() }
                )
            )
        },
        appBarState = scrollBehavior.state,
    )
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
            Icon(imageVector = FlowIcons.History, contentDescription = null)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                SuggestText(value = suggest.value, substring = suggest.substring)
            }
            IconButton(
                onClick = onSubmit,
                imageVector = FlowIcons.ArrowTopLeft,
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
