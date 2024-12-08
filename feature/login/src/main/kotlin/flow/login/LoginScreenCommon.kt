package flow.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentDataType
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDataType
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import flow.designsystem.component.Button
import flow.designsystem.component.CircularProgressIndicator
import flow.designsystem.component.Icon
import flow.designsystem.component.IconButton
import flow.designsystem.component.OutlinedTextField
import flow.designsystem.component.Placeholder
import flow.designsystem.component.Text
import flow.designsystem.component.onEnter
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.AppTheme
import flow.models.auth.Captcha
import flow.ui.component.RemoteImage
import flow.ui.component.rememberVisibilityState

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
        .semantics {
            contentType = ContentType.Username
            contentDataType = ContentDataType.Text
        }
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
                },
            ),
        )
    },
    leadingIcon = {
        Icon(
            icon = FlowIcons.Username,
            contentDescription = stringResource(R.string.login_screen_username_hint),
        )
    },
    keyboardOptions = KeyboardOptions(
        capitalization = KeyboardCapitalization.None,
        keyboardType = KeyboardType.Email,
        imeAction = ImeAction.Next,
        autoCorrectEnabled = false,
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
            .semantics {
                contentType = ContentType.Password
                contentDataType = ContentDataType.Text
            }
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
                    },
                ),
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
            capitalization = KeyboardCapitalization.None,
            keyboardType = KeyboardType.Password,
            imeAction = if (state.hasCaptcha) {
                ImeAction.Next
            } else {
                ImeAction.Done
            },
            autoCorrectEnabled = false,
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
                },
            ),
        )
    },
    leadingIcon = {
        Icon(
            icon = FlowIcons.Captcha,
            contentDescription = stringResource(R.string.login_screen_captcha_hint),
        )
    },
    keyboardOptions = KeyboardOptions(
        capitalization = KeyboardCapitalization.None,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Done,
        autoCorrectEnabled = false,
    ),
    keyboardActions = KeyboardActions(
        onDone = { onSubmit() },
    ),
)

@Composable
internal fun CaptchaImage(
    captcha: Captcha,
    onRetry: () -> Unit,
) = RemoteImage(
    src = captcha.url,
    onLoading = {
        Box(
            modifier = Modifier
                .padding(AppTheme.spaces.medium)
                .size(100.dp),
            contentAlignment = Alignment.Center,
            content = {
                CircularProgressIndicator(modifier = Modifier.size(AppTheme.sizes.medium))
            },
        )
    },
    onSuccess = { painter ->
        Image(
            modifier = Modifier
                .padding(AppTheme.spaces.medium)
                .size(100.dp),
            painter = painter,
            contentDescription = stringResource(R.string.login_screen_captcha_hint),
        )
    },
    onError = {
        Column(
            modifier = Modifier.padding(AppTheme.spaces.medium),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier = Modifier.size(100.dp),
                painter = painterResource(flow.ui.R.drawable.ill_placeholder),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = AppTheme.colors.primary),
            )
            IconButton(
                icon = FlowIcons.Reload,
                contentDescription = stringResource(R.string.login_screen_captcha_reload),
                onClick = onRetry,
            )
        }
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
