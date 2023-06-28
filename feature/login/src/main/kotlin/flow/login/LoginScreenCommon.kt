package flow.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import flow.designsystem.component.Button
import flow.designsystem.component.CircularProgressIndicator
import flow.designsystem.component.Icon
import flow.designsystem.component.Placeholder
import flow.designsystem.component.Text
import flow.designsystem.component.OutlinedTextField
import flow.designsystem.component.onEnter
import flow.ui.component.rememberVisibilityState
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.AppTheme
import flow.models.auth.Captcha
import flow.ui.component.RemoteImage

@Composable
internal fun LoginScreenHeader() = Placeholder(
    modifier = Modifier.fillMaxSize(),
    titleRes = R.string.login_screen_header_title,
    subtitleRes = R.string.login_screen_header_subtitle,
)

@Composable
internal fun UsernameInputField(
    modifier: Modifier = Modifier,
    state: LoginState,
    onChanged: (TextFieldValue) -> Unit,
    onSelectNext: () -> Unit,
) = OutlinedTextField(
    modifier = modifier
        .padding(AppTheme.spaces.small)
        .onFocusEvent {
            if (it.isFocused) {
                onChanged(state.usernameInput.value)
            }
        }
        .autofill(
            autofillTypes = listOf(AutofillType.Username),
            onFill = onChanged,
        )
        .onEnter(onSelectNext),
    value = state.usernameInput.value,
    onValueChange = onChanged,
    singleLine = true,
    enabled = !state.isLoading,
    isError = state.usernameInput.isError(),
    label = {
        Text(
            stringResource(
                when (state.usernameInput) {
                    is InputState.Empty -> R.string.login_screen_username_empty_label
                    is InputState.Invalid -> R.string.login_screen_wrong_credits_label
                    else -> R.string.login_screen_username_hint
                }
            )
        )
    },
    leadingIcon = {
        Icon(
            icon = FlowIcons.Username,
            contentDescription = stringResource(R.string.login_screen_username_hint),
        )
    },
    keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Next,
        autoCorrect = false,
    ),
    keyboardActions = KeyboardActions(
        onNext = { onSelectNext() },
    ),
)

@Composable
internal fun PasswordInputField(
    modifier: Modifier = Modifier,
    state: LoginState,
    onChanged: (TextFieldValue) -> Unit,
    onSelectNext: () -> Unit,
    onSubmit: () -> Unit,
) {
    val passwordVisibility = rememberVisibilityState()
    OutlinedTextField(
        modifier = modifier
            .padding(AppTheme.spaces.small)
            .onFocusEvent {
                if (it.isFocused) {
                    onChanged(state.passwordInput.value)
                }
            }
            .autofill(
                autofillTypes = listOf(AutofillType.Password),
                onFill = onChanged,
            )
            .onEnter(onSelectNext),
        value = state.passwordInput.value,
        onValueChange = onChanged,
        singleLine = true,
        enabled = !state.isLoading,
        isError = state.passwordInput.isError(),
        visualTransformation = if (passwordVisibility.visible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        label = {
            Text(
                stringResource(
                    when (state.passwordInput) {
                        is InputState.Empty -> R.string.login_screen_password_empty_label
                        is InputState.Invalid -> R.string.login_screen_wrong_credits_label
                        else -> R.string.login_screen_password_hint
                    }
                )
            )
        },
        leadingIcon = {
            Icon(
                icon = FlowIcons.Password,
                contentDescription = stringResource(R.string.login_screen_password_hint),
            )
        },
        trailingIcon = {
            Box(modifier = Modifier.clickable(onClick = passwordVisibility::toggle)) {
                if (passwordVisibility.visible) {
                    Icon(
                        icon = FlowIcons.PasswordVisible,
                        contentDescription = stringResource(R.string.login_screen_password_hide),
                    )
                } else {
                    Icon(
                        icon = FlowIcons.PasswordHidden,
                        contentDescription = stringResource(R.string.login_screen_password_show),
                    )
                }
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = if (state.hasCaptcha) {
                ImeAction.Next
            } else {
                ImeAction.Done
            },
            autoCorrect = false,
        ),
        keyboardActions = KeyboardActions(
            onNext = { onSelectNext() },
            onDone = { onSubmit() },
        ),
    )
}

@Composable
internal fun CaptchaInputField(
    modifier: Modifier = Modifier,
    state: LoginState,
    onChanged: (TextFieldValue) -> Unit,
    onSubmit: () -> Unit,
) = OutlinedTextField(
    modifier = modifier
        .padding(AppTheme.spaces.small)
        .onEnter(onSubmit),
    value = state.captchaInput.value,
    onValueChange = onChanged,
    singleLine = true,
    enabled = !state.isLoading,
    isError = state.captchaInput.isError(),
    label = {
        Text(
            stringResource(
                when (state.captchaInput) {
                    is InputState.Empty -> R.string.login_screen_captcha_empty_label
                    is InputState.Invalid -> R.string.login_screen_captcha_empty_label
                    else -> R.string.login_screen_captcha_hint
                }
            )
        )
    },
    leadingIcon = {
        Icon(
            icon = FlowIcons.Captcha,
            contentDescription = stringResource(R.string.login_screen_captcha_hint),
        )
    },
    keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Done,
        autoCorrect = false,
    ),
    keyboardActions = KeyboardActions(
        onDone = { onSubmit() },
    ),
)

@Composable
internal fun CaptchaImage(
    modifier: Modifier = Modifier,
    captcha: Captcha,
) = Box(
    modifier = modifier.padding(AppTheme.spaces.medium),
    contentAlignment = Alignment.Center,
    content = {
        RemoteImage(
            src = captcha.url,
            contentDescription = stringResource(R.string.login_screen_captcha_hint),
            onLoading = { CircularProgressIndicator(modifier = Modifier.size(AppTheme.sizes.medium)) },
            onSuccess = { painter ->
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painter,
                    contentDescription = stringResource(R.string.login_screen_captcha_hint),
                )
            },
            onError = {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(flow.ui.R.drawable.ill_placeholder),
                    contentDescription = stringResource(R.string.login_screen_captcha_hint),
                    colorFilter = ColorFilter.tint(color = AppTheme.colors.primary),
                )
            },
        )
    },
)

@Composable
internal fun LoginButton(
    modifier: Modifier = Modifier,
    state: LoginState,
    onSubmit: () -> Unit,
) = Button(
    modifier = modifier,
    onClick = onSubmit,
    enabled = !state.isLoading && state.isValid,
    content = {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(end = AppTheme.spaces.medium)
                    .size(AppTheme.sizes.small),
            )
        }
        Text(stringResource(R.string.login_screen_action_sign_in))
    },
)

private fun Modifier.autofill(
    autofillTypes: List<AutofillType>,
    onFill: (TextFieldValue) -> Unit,
) = composed {
    val autofill = LocalAutofill.current
    val autofillNode = AutofillNode(
        autofillTypes = autofillTypes,
        onFill = { value -> onFill(TextFieldValue(value, TextRange(value.length))) },
    )
    LocalAutofillTree.current += autofillNode
    onGloballyPositioned { coordinates ->
        autofillNode.boundingBox = coordinates.boundsInWindow()
    }.onFocusChanged { focusState ->
        autofill?.run {
            if (focusState.isFocused) {
                requestAutofillForNode(autofillNode)
            } else {
                cancelAutofillForNode(autofillNode)
            }
        }
    }
}
