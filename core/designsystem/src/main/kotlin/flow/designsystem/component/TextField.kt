package flow.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import flow.designsystem.R
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.AppTheme
import flow.designsystem.theme.FlowTheme
import flow.designsystem.utils.RunOnFirstComposition

@Composable
@NonRestartableComposable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) = OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    modifier = modifier,
    enabled = enabled,
    readOnly = readOnly,
    textStyle = textStyle,
    label = label,
    placeholder = placeholder,
    leadingIcon = leadingIcon,
    trailingIcon = trailingIcon,
    supportingText = supportingText,
    isError = isError,
    visualTransformation = visualTransformation,
    keyboardOptions = keyboardOptions,
    keyboardActions = keyboardActions,
    singleLine = singleLine,
    maxLines = maxLines,
    interactionSource = interactionSource,
    shape = AppTheme.shapes.medium,
    colors = TextFieldDefaults.textFieldColors(),
)

@Composable
fun SearchInputField(
    modifier: Modifier = Modifier,
    inputValue: TextFieldValue,
    onInputValueChange: (TextFieldValue) -> Unit,
    showClearButton: Boolean,
    onClearButtonClick: () -> Unit,
    onSubmitClick: () -> Unit,
) {
    val focusRequester = rememberFocusRequester()
    RunOnFirstComposition { focusRequester.requestFocus() }
    androidx.compose.material3.TextField(
        modifier = modifier.focusRequester(focusRequester),
        value = inputValue,
        placeholder = { Text(stringResource(R.string.designsystem_hint_search)) },
        onValueChange = onInputValueChange,
        trailingIcon = {
            AnimatedVisibility(
                visible = showClearButton,
                enter = fadeIn() + expandIn(expandFrom = Alignment.Center),
                exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.Center),
            ) {
                IconButton(
                    icon = FlowIcons.Clear,
                    contentDescription = stringResource(R.string.designsystem_action_clear),
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
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            focusedTrailingIconColor = LocalContentColor.current,
        ),
    )
}

private object TextFieldDefaults {
    const val DisabledOpacity = 0.37f
    private val FocusedColor: Color
        @Composable
        get() = AppTheme.colors.primary
    private val UnfocusedColor: Color
        @Composable
        get() = AppTheme.colors.outline
    private val DisabledColor: Color
        @Composable
        get() = AppTheme.colors.outlineVariant
    private val ErrorColor: Color
        @Composable
        get() = AppTheme.colors.error

    @Composable
    fun textFieldColors(
        textColor: Color = LocalContentColor.current,
        disabledTextColor: Color = textColor.copy(alpha = DisabledOpacity),
        containerColor: Color = Color.Transparent,
        cursorColor: Color = FocusedColor,
        errorCursorColor: Color = ErrorColor,
        textSelectionColors: TextSelectionColors = LocalTextSelectionColors.current,
        focusedIndicatorColor: Color = FocusedColor,
        unfocusedIndicatorColor: Color = UnfocusedColor,
        errorIndicatorColor: Color = ErrorColor,
        disabledIndicatorColor: Color = DisabledColor,
        focusedLeadingIconColor: Color = FocusedColor,
        unfocusedLeadingIconColor: Color = UnfocusedColor,
        disabledLeadingIconColor: Color = DisabledColor,
        errorLeadingIconColor: Color = ErrorColor,
        focusedTrailingIconColor: Color = FocusedColor,
        unfocusedTrailingIconColor: Color = UnfocusedColor,
        disabledTrailingIconColor: Color = DisabledColor,
        errorTrailingIconColor: Color = ErrorColor,
        focusedLabelColor: Color = FocusedColor,
        unfocusedLabelColor: Color = UnfocusedColor,
        disabledLabelColor: Color = DisabledColor,
        errorLabelColor: Color = ErrorColor,
        placeholderColor: Color = UnfocusedColor,
        disabledPlaceholderColor: Color = DisabledColor,
        focusedSupportingTextColor: Color = FocusedColor,
        unfocusedSupportingTextColor: Color = UnfocusedColor,
        disabledSupportingTextColor: Color = DisabledColor,
        errorSupportingTextColor: Color = ErrorColor,
    ): TextFieldColors = androidx.compose.material3.TextFieldDefaults.textFieldColors(
        textColor = textColor,
        disabledTextColor = disabledTextColor,
        containerColor = containerColor,
        cursorColor = cursorColor,
        errorCursorColor = errorCursorColor,
        selectionColors = textSelectionColors,
        focusedIndicatorColor = focusedIndicatorColor,
        unfocusedIndicatorColor = unfocusedIndicatorColor,
        errorIndicatorColor = errorIndicatorColor,
        disabledIndicatorColor = disabledIndicatorColor,
        focusedLeadingIconColor = focusedLeadingIconColor,
        unfocusedLeadingIconColor = unfocusedLeadingIconColor,
        disabledLeadingIconColor = disabledLeadingIconColor,
        errorLeadingIconColor = errorLeadingIconColor,
        focusedTrailingIconColor = focusedTrailingIconColor,
        unfocusedTrailingIconColor = unfocusedTrailingIconColor,
        disabledTrailingIconColor = disabledTrailingIconColor,
        errorTrailingIconColor = errorTrailingIconColor,
        focusedLabelColor = focusedLabelColor,
        unfocusedLabelColor = unfocusedLabelColor,
        disabledLabelColor = disabledLabelColor,
        errorLabelColor = errorLabelColor,
        placeholderColor = placeholderColor,
        disabledPlaceholderColor = disabledPlaceholderColor,
        focusedSupportingTextColor = focusedSupportingTextColor,
        unfocusedSupportingTextColor = unfocusedSupportingTextColor,
        disabledSupportingTextColor = disabledSupportingTextColor,
        errorSupportingTextColor = errorSupportingTextColor,
    )
}

@ThemePreviews
@Composable
private fun TextFieldPreview() {
    FlowTheme(isDynamic = false) {
        Surface {
            Column {
                TextField(
                    modifier = Modifier.padding(AppTheme.spaces.medium),
                    value = "Input text",
                    onValueChange = {},
                )
                TextField(
                    modifier = Modifier.padding(AppTheme.spaces.medium),
                    value = "Disabled input text",
                    onValueChange = {},
                    enabled = false,
                )
                TextField(
                    modifier = Modifier.padding(AppTheme.spaces.medium),
                    value = "Error input text",
                    onValueChange = {},
                    isError = true,
                )
            }
        }
    }
}
