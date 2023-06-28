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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import flow.designsystem.component.AppBar
import flow.designsystem.component.AppBarState
import flow.designsystem.component.BackButton
import flow.designsystem.component.Icon
import flow.designsystem.component.IconButton
import flow.designsystem.component.LazyList
import flow.designsystem.component.Scaffold
import flow.designsystem.component.Surface
import flow.designsystem.component.Text
import flow.designsystem.component.TextField
import flow.designsystem.component.onEnter
import flow.designsystem.component.rememberFocusRequester
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.AppTheme
import flow.designsystem.utils.RunOnFirstComposition
import flow.models.search.Filter
import flow.models.search.Suggest
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import flow.designsystem.R as DsR

@Composable
internal fun SearchInputScreen(
    viewModel: SearchInputViewModel,
    back: () -> Unit,
    openSearchResult: (Filter) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SearchInputSideEffect.Back -> back()
            is SearchInputSideEffect.HideKeyboard -> keyboardController?.hide()
            is SearchInputSideEffect.OpenSearch -> openSearchResult(sideEffect.filter)
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
) = Scaffold(
    topBar = { appBarState ->
        SearchInputAppBar(
            inputValue = state.searchInput,
            onInputValueChange = { onAction(SearchInputAction.InputChanged(it)) },
            showClearButton = state.showClearButton,
            onClearButtonClick = { onAction(SearchInputAction.ClearInputClick) },
            onSubmitClick = { onAction(SearchInputAction.SubmitClick) },
            onBackClick = { onAction(SearchInputAction.BackClick) },
            appBarState = appBarState,
        )
    },
    content = { padding ->
        LazyList(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(vertical = AppTheme.spaces.medium),
        ) {
            items(
                items = state.suggests,
                key = { item -> item.value }
            ) { item ->
                SuggestItem(
                    suggest = item,
                    onClick = { onAction(SearchInputAction.SuggestClick(item)) },
                    onSubmit = { onAction(SearchInputAction.SuggestEditClick(item)) },
                )
            }
        }
    },
)

@Composable
private fun SearchInputAppBar(
    modifier: Modifier = Modifier,
    inputValue: TextFieldValue,
    onInputValueChange: (TextFieldValue) -> Unit,
    showClearButton: Boolean,
    onClearButtonClick: () -> Unit,
    onSubmitClick: () -> Unit,
    onBackClick: () -> Unit,
    appBarState: AppBarState,
) = AppBar(
    modifier = modifier,
    navigationIcon = { BackButton(onBackClick) },
    title = {
        SearchInputField(
            modifier = Modifier.fillMaxWidth(),
            inputValue = inputValue,
            onInputValueChange = onInputValueChange,
            showClearButton = showClearButton,
            onClearButtonClick = onClearButtonClick,
            onSubmitClick = onSubmitClick,
        )
    },
    appBarState = appBarState,
)

@Composable
private fun SearchInputField(
    modifier: Modifier = Modifier,
    inputValue: TextFieldValue,
    onInputValueChange: (TextFieldValue) -> Unit,
    showClearButton: Boolean,
    onClearButtonClick: () -> Unit,
    onSubmitClick: () -> Unit,
) {
    val focusRequester = rememberFocusRequester()
    RunOnFirstComposition { focusRequester.requestFocus() }
    TextField(
        modifier = modifier
            .focusRequester(focusRequester)
            .onEnter(onSubmitClick),
        value = inputValue,
        placeholder = { Text(stringResource(DsR.string.designsystem_hint_search)) },
        onValueChange = onInputValueChange,
        trailingIcon = {
            AnimatedVisibility(
                visible = showClearButton,
                enter = fadeIn() + expandIn(expandFrom = Alignment.Center),
                exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.Center),
            ) {
                IconButton(
                    icon = FlowIcons.Clear,
                    contentDescription = stringResource(DsR.string.designsystem_action_clear),
                    onClick = onClearButtonClick,
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
        ),
    )
}

@Composable
private fun SuggestItem(
    suggest: Suggest,
    onClick: () -> Unit,
    onSubmit: () -> Unit,
) = Surface(
    modifier = Modifier
        .fillMaxWidth()
        .height(AppTheme.sizes.default),
    onClick = onClick,
) {
    Row(
        modifier = Modifier.padding(horizontal = AppTheme.spaces.large),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon = FlowIcons.History, contentDescription = null)
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = AppTheme.spaces.large)
        ) {
            SuggestText(value = suggest.value, substring = suggest.substring)
        }
        IconButton(
            icon = FlowIcons.InsertSuggest,
            contentDescription = stringResource(DsR.string.designsystem_content_description_state_edit),
            onClick = onSubmit,
        )
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
                withStyle(style = SpanStyle(color = AppTheme.colors.accentOrange)) {
                    append(value.substring(substring))
                }
                if (value.lastIndex > substringEndIndex) {
                    append(value.substring(substringEndIndex + 1))
                }
            }
        )
    }
}
