package flow.designsystem.component

import android.view.KeyEvent
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
import androidx.compose.ui.input.key.onKeyEvent
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
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
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
        modifier = modifier
            .focusRequester(focusRequester)
            .onEnter(onSubmitClick),
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
        focusedTextColor: Color = LocalContentColor.current,
        unfocusedTextColor: Color = LocalContentColor.current,
        disabledTextColor: Color = LocalContentColor.current.copy(alpha = DisabledOpacity),
        errorTextColor: Color = ErrorColor,
        focusedContainerColor: Color = Color.Transparent,
        unfocusedContainerColor: Color = Color.Transparent,
        disabledContainerColor: Color = Color.Transparent,
        errorContainerColor: Color = Color.Transparent,
        cursorColor: Color = FocusedColor,
        errorCursorColor: Color = ErrorColor,
        selectionColors: TextSelectionColors = LocalTextSelectionColors.current,
        focusedIndicatorColor: Color = FocusedColor,
        unfocusedIndicatorColor: Color = UnfocusedColor,
        disabledIndicatorColor: Color = DisabledColor,
        errorIndicatorColor: Color = ErrorColor,
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
        focusedPlaceholderColor: Color = UnfocusedColor,
        unfocusedPlaceholderColor: Color = UnfocusedColor,
        disabledPlaceholderColor: Color = DisabledColor,
        errorPlaceholderColor: Color = ErrorColor,
        focusedSupportingTextColor: Color = FocusedColor,
        unfocusedSupportingTextColor: Color = UnfocusedColor,
        disabledSupportingTextColor: Color = DisabledColor,
        errorSupportingTextColor: Color = ErrorColor,
        focusedPrefixColor: Color = FocusedColor,
        unfocusedPrefixColor: Color = UnfocusedColor,
        disabledPrefixColor: Color = DisabledColor,
        errorPrefixColor: Color = ErrorColor,
        focusedSuffixColor: Color = FocusedColor,
        unfocusedSuffixColor: Color = UnfocusedColor,
        disabledSuffixColor: Color = DisabledColor,
        errorSuffixColor: Color = ErrorColor,
    ): TextFieldColors = androidx.compose.material3.TextFieldDefaults.colors(
        focusedTextColor = focusedTextColor,
        unfocusedTextColor = unfocusedTextColor,
        disabledTextColor = disabledTextColor,
        errorTextColor = errorTextColor,
        focusedContainerColor = focusedContainerColor,
        unfocusedContainerColor = unfocusedContainerColor,
        disabledContainerColor = disabledContainerColor,
        errorContainerColor = errorContainerColor,
        cursorColor = cursorColor,
        errorCursorColor = errorCursorColor,
        selectionColors = selectionColors,
        focusedIndicatorColor = focusedIndicatorColor,
        unfocusedIndicatorColor = unfocusedIndicatorColor,
        disabledIndicatorColor = disabledIndicatorColor,
        errorIndicatorColor = errorIndicatorColor,
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
        focusedPlaceholderColor = focusedPlaceholderColor,
        unfocusedPlaceholderColor = unfocusedPlaceholderColor,
        disabledPlaceholderColor = disabledPlaceholderColor,
        errorPlaceholderColor = errorPlaceholderColor,
        focusedSupportingTextColor = focusedSupportingTextColor,
        unfocusedSupportingTextColor = unfocusedSupportingTextColor,
        disabledSupportingTextColor = disabledSupportingTextColor,
        errorSupportingTextColor = errorSupportingTextColor,
        focusedPrefixColor = focusedPrefixColor,
        unfocusedPrefixColor = unfocusedPrefixColor,
        disabledPrefixColor = disabledPrefixColor,
        errorPrefixColor = errorPrefixColor,
        focusedSuffixColor = focusedSuffixColor,
        unfocusedSuffixColor = unfocusedSuffixColor,
        disabledSuffixColor = disabledSuffixColor,
        errorSuffixColor = errorSuffixColor,
    )
}

fun Modifier.onEnter(block: () -> Unit): Modifier =
    this then onKeyEvent { keyEvent ->
        if (keyEvent.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
            block()
            true
        } else {
            false
        }
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
