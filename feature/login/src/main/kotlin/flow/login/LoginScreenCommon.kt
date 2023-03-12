package flow.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import flow.designsystem.component.Button
import flow.designsystem.component.Placeholder
import flow.designsystem.drawables.FlowIcons
import flow.models.InputState
import flow.models.auth.Captcha
import flow.ui.R as UiR

@Composable
internal fun LoginScreenHeader() {
    Placeholder(
        modifier = Modifier.fillMaxSize(),
        titleRes = R.string.login_screen_header_title,
        subtitleRes = R.string.login_screen_header_subtitle,
    )
}

@Composable
internal fun UsernameInputField(
    modifier: Modifier = Modifier,
    state: LoginState,
    onChanged: (String) -> Unit,
    onSelectNext: () -> Unit,
    colors: TextFieldColors,
) {
    OutlinedTextField(
        modifier = modifier
            .padding(4.dp)
            .onKeyEvent { event ->
                when (event.key.keyCode) {
                    Key.DirectionDown.keyCode -> {
                        onSelectNext()
                        true
                    }

                    else -> {
                        false
                    }
                }
            }
            .autofill(
                autofillTypes = listOf(
                    AutofillType.Username,
                    AutofillType.EmailAddress,
                ),
                onFill = onChanged,
            ),
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
                imageVector = FlowIcons.Username,
                contentDescription = null,
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
        colors = colors,
    )
}

@Composable
internal fun PasswordInputField(
    modifier: Modifier = Modifier,
    state: LoginState,
    onChanged: (String) -> Unit,
    onSelectNext: () -> Unit,
    onSelectPrevious: () -> Unit = {},
    onSubmit: () -> Unit,
    colors: TextFieldColors,
) {
    OutlinedTextField(
        modifier = modifier
            .padding(4.dp)
            .onKeyEvent { event ->
                when (event.key.keyCode) {
                    Key.DirectionUp.keyCode -> {
                        onSelectPrevious()
                        true
                    }

                    Key.DirectionDown.keyCode -> {
                        onSelectNext()
                        true
                    }

                    else -> {
                        false
                    }
                }
            }
            .autofill(
                autofillTypes = listOf(AutofillType.Password),
                onFill = onChanged,
            ),
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
                imageVector = FlowIcons.Password,
                contentDescription = null,
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
        colors = colors,
    )
}

@Composable
internal fun CaptchaInputField(
    modifier: Modifier = Modifier,
    state: LoginState,
    onChanged: (String) -> Unit,
    onSelectPrevious: () -> Unit = {},
    onSubmit: () -> Unit,
    colors: TextFieldColors,
) {
    OutlinedTextField(
        modifier = modifier
            .padding(4.dp)
            .onKeyEvent { event ->
                when (event.key.keyCode) {
                    Key.DirectionUp.keyCode -> {
                        onSelectPrevious()
                        true
                    }

                    else -> {
                        false
                    }
                }
            },
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
                imageVector = FlowIcons.Captcha,
                contentDescription = null,
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
        colors = colors,
    )
}

@Composable
internal fun CaptchaImage(
    modifier: Modifier = Modifier,
    captcha: Captcha,
) {
    Box(
        modifier = modifier.padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        SubcomposeAsyncImage(
            model = captcha.url,
            contentDescription = null,
        ) {
            when (painter.state) {
                AsyncImagePainter.State.Empty,
                is AsyncImagePainter.State.Success -> Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painter,
                    contentDescription = stringResource(R.string.login_screen_captcha_hint),
                )

                is AsyncImagePainter.State.Loading -> CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    strokeWidth = 2.dp,
                )

                is AsyncImagePainter.State.Error -> Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(UiR.drawable.ill_placeholder),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary),
                )
            }
        }
    }
}

@Composable
internal fun LoginButton(
    modifier: Modifier = Modifier,
    state: LoginState,
    onSubmit: () -> Unit,
) {
    Button(
        modifier = modifier,
        onClick = onSubmit,
        enabled = !state.isLoading && state.isValid,
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(16.dp),
                strokeWidth = 2.dp,
            )
        }
        Text(stringResource(R.string.login_screen_action_sign_in))
    }
}

private fun Modifier.autofill(
    autofillTypes: List<AutofillType>,
    onFill: ((String) -> Unit),
) = composed {
    val autofill = LocalAutofill.current
    val autofillNode = AutofillNode(onFill = onFill, autofillTypes = autofillTypes)
    LocalAutofillTree.current += autofillNode
    onGloballyPositioned {
        autofillNode.boundingBox = it.boundsInWindow()
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
