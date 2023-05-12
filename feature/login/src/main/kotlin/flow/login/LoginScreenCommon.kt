package flow.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import flow.designsystem.component.Button
import flow.designsystem.component.CircularProgressIndicator
import flow.designsystem.component.Icon
import flow.designsystem.component.Placeholder
import flow.designsystem.component.Text
import flow.designsystem.component.TextField
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.AppTheme
import flow.models.InputState
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
    onChanged: (String) -> Unit,
    onSelectNext: () -> Unit,
) = TextField(
    modifier = modifier.padding(AppTheme.spaces.small),
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
        keyboardType = KeyboardType.Ascii,
        imeAction = ImeAction.Next,
        autoCorrect = false,
    ),
    keyboardActions = KeyboardActions(
        onNext = { onSelectNext() }
    ),
)

@Composable
internal fun PasswordInputField(
    modifier: Modifier = Modifier,
    state: LoginState,
    onChanged: (String) -> Unit,
    onSelectNext: () -> Unit,
    onSubmit: () -> Unit,
) = TextField(
    modifier = modifier.padding(AppTheme.spaces.small),
    value = state.passwordInput.value,
    onValueChange = onChanged,
    singleLine = true,
    enabled = !state.isLoading,
    isError = state.passwordInput.isError(),
    visualTransformation = PasswordVisualTransformation(),
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

@Composable
internal fun CaptchaInputField(
    modifier: Modifier = Modifier,
    state: LoginState,
    onChanged: (String) -> Unit,
    onSubmit: () -> Unit,
) = TextField(
    modifier = modifier.padding(AppTheme.spaces.small),
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
