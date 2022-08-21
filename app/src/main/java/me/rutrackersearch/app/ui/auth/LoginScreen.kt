package me.rutrackersearch.app.ui.auth

import android.content.res.Configuration.UI_MODE_TYPE_TELEVISION
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.FontDownload
import androidx.compose.material.icons.outlined.ImageNotSupported
import androidx.compose.material.icons.outlined.VpnKey
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices.AUTOMOTIVE_1024p
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import kotlinx.coroutines.launch
import me.rutrackersearch.app.R
import me.rutrackersearch.app.ui.auth.LoginAction.CaptchaChanged
import me.rutrackersearch.app.ui.auth.LoginAction.PasswordChanged
import me.rutrackersearch.app.ui.auth.LoginAction.SubmitClick
import me.rutrackersearch.app.ui.auth.LoginAction.UsernameChanged
import me.rutrackersearch.app.ui.common.Button
import me.rutrackersearch.app.ui.common.DynamicBox
import me.rutrackersearch.app.ui.common.Placeholder
import me.rutrackersearch.app.ui.platform.LocalPlatformType
import me.rutrackersearch.app.ui.platform.PlatformType
import me.rutrackersearch.auth.models.Captcha

@Composable
fun LoginScreen(onSuccess: () -> Unit) {
    LoginScreen(
        viewModel = hiltViewModel(),
        onSuccess = onSuccess,
    )
}

@Composable
private fun LoginScreen(
    viewModel: LoginViewModel,
    onSuccess: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(state) {
        if (state.isSuccess) {
            onSuccess()
        }
    }
    LoginScreen(state = state, onAction = viewModel::perform)
}

@Composable
private fun LoginScreen(
    state: LoginState,
    onAction: (LoginAction) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val snackbarState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    fun submit() {
        keyboardController?.hide()
        if (state.isValid) {
            onAction(SubmitClick)
        }
    }

    if (state.error != null) {
        val errorMessage = when (state.error) {
            is me.rutrackersearch.models.error.Failure.ConnectionError -> stringResource(R.string.error_no_internet)
            is me.rutrackersearch.models.error.Failure.ServerError -> stringResource(R.string.error_proxy_server)
            is me.rutrackersearch.models.error.Failure.ServiceUnavailable -> stringResource(R.string.error_site_connection)
            else -> stringResource(R.string.error_something_goes_wrong)
        }
        LaunchedEffect(state.error) {
            coroutineScope.launch { snackbarState.showSnackbar(errorMessage) }
        }
    }
    val colors = TextFieldDefaults.outlinedTextFieldColors(
        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
    )
    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        snackbarHost = { SnackbarHost(snackbarState) }
    ) { paddingValues ->
        DynamicBox(
            mobileContent = {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .imePadding()
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .onGloballyPositioned {
                            coroutineScope.launch {
                                scrollState.scrollBy(it.size.height.toFloat())
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Illustration()
                    UsernameInputField(
                        state = state,
                        onChanged = { onAction(UsernameChanged(it)) },
                        onSelectNext = { focusManager.moveFocus(FocusDirection.Next) },
                        colors = colors,
                    )
                    PasswordInputField(
                        state = state,
                        onChanged = { onAction(PasswordChanged(it)) },
                        onSelectNext = { focusManager.moveFocus(FocusDirection.Next) },
                        onSelectPrevious = { focusManager.moveFocus(FocusDirection.Previous) },
                        onSubmit = { submit() },
                        colors = colors,
                    )
                    if (state.captcha != null) {
                        CaptchaImage(
                            modifier = Modifier.size(100.dp),
                            captcha = state.captcha,
                        )
                        CaptchaInputField(
                            state = state,
                            onChanged = { onAction(CaptchaChanged(it)) },
                            onSubmit = { submit() },
                            colors = colors,
                        )
                    }
                    LoginButton(
                        modifier = Modifier.padding(16.dp),
                        state = state,
                        onSubmit = { submit() }
                    )
                }
            },
            tvContent = {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    Column(
                        modifier = Modifier.weight(0.5f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Illustration()
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(0.6f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceEvenly,
                            ) {
                                UsernameInputField(
                                    state = state,
                                    onChanged = { onAction(UsernameChanged(it)) },
                                    onSelectNext = { focusManager.moveFocus(FocusDirection.Next) },
                                    colors = colors,
                                )
                                PasswordInputField(
                                    state = state,
                                    onChanged = { onAction(PasswordChanged(it)) },
                                    onSelectNext = { focusManager.moveFocus(FocusDirection.Next) },
                                    onSelectPrevious = { focusManager.moveFocus(FocusDirection.Previous) },
                                    onSubmit = { submit() },
                                    colors = colors,
                                )
                                if (state.captcha != null) {
                                    CaptchaInputField(
                                        state = state,
                                        onChanged = { onAction(CaptchaChanged(it)) },
                                        onSelectPrevious = { focusManager.moveFocus(FocusDirection.Previous) },
                                        onSubmit = { submit() },
                                        colors = colors,
                                    )
                                }
                            }
                            if (state.captcha != null) {
                                CaptchaImage(
                                    modifier = Modifier.size(200.dp),
                                    captcha = state.captcha,
                                )
                            }
                        }
                        LoginButton(
                            modifier = Modifier.padding(top = 16.dp),
                            state = state,
                            onSubmit = { submit() },
                        )
                    }
                }
            },
        )
    }
}

@Composable
private fun Illustration() {
    Placeholder(
        modifier = Modifier.fillMaxSize(),
        title = { Text(stringResource(R.string.auth_title)) },
        subtitle = {
            Text(
                text = stringResource(R.string.auth_subtitle),
                textAlign = TextAlign.Center,
            )
        },
        icon = {
            Image(
                painter = painterResource(R.drawable.ill_login_screen),
                contentDescription = null,
            )
        },
    )
}

@Composable
private fun UsernameInputField(
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
            },
        value = state.usernameInput.value,
        onValueChange = onChanged,
        singleLine = true,
        enabled = !state.isLoading,
        isError = state.usernameInput.isError(),
        label = {
            Text(
                stringResource(
                    when (state.usernameInput) {
                        is InputState.Empty -> R.string.auth_username_empty_label
                        is InputState.Invalid -> R.string.auth_username_or_password_wrong_label
                        else -> R.string.auth_username
                    }
                )
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.AccountCircle,
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
private fun PasswordInputField(
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
            },
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
                        is InputState.Empty -> R.string.auth_password_empty_label
                        is InputState.Invalid -> R.string.auth_username_or_password_wrong_label
                        else -> R.string.auth_password
                    }
                )
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.VpnKey,
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
private fun CaptchaInputField(
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
                        is InputState.Empty -> R.string.auth_captcha_empty_label
                        is InputState.Invalid -> R.string.auth_captcha_empty_label
                        else -> R.string.auth_captcha
                    }
                )
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.FontDownload,
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
private fun CaptchaImage(
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
                    modifier = modifier.fillMaxSize(),
                    painter = painter,
                    contentDescription = stringResource(R.string.auth_captcha),
                )
                is AsyncImagePainter.State.Loading -> CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    strokeWidth = 2.dp,
                )
                is AsyncImagePainter.State.Error -> Icon(
                    modifier = Modifier.size(48.dp),
                    imageVector = Icons.Outlined.ImageNotSupported,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                )
            }
        }
    }
}

@Composable
private fun LoginButton(
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
        Text(stringResource(R.string.auth_action_sign_in))
    }
}

@Preview(
    group = "Mobile",
    name = "Login screen default",
    showBackground = true,
    showSystemUi = true,
)
@Preview(
    group = "TV",
    name = "Login screen default",
    showBackground = true,
    showSystemUi = true,
    device = AUTOMOTIVE_1024p,
    uiMode = UI_MODE_TYPE_TELEVISION,
)
@Composable
private fun LoginScreenPreview_DefaultState() {
    LoginScreen(state = LoginState(), onAction = {})
}

@Preview(
    group = "Mobile",
    name = "Login screen loading",
    showBackground = true,
    showSystemUi = true,
)
@Preview(
    group = "TV",
    name = "Login screen loading",
    showBackground = true,
    showSystemUi = true,
    device = AUTOMOTIVE_1024p,
    uiMode = UI_MODE_TYPE_TELEVISION,
)
@Composable
private fun LoginScreenPreview_LoadingState() {
    LoginScreen(state = LoginState(isLoading = true), onAction = {})
}

@Preview(
    group = "Mobile",
    name = "Login screen error",
    showBackground = true,
    showSystemUi = true,
)
@Preview(
    group = "TV",
    name = "Login screen error",
    showBackground = true,
    showSystemUi = true,
    device = AUTOMOTIVE_1024p,
    uiMode = UI_MODE_TYPE_TELEVISION,
)
@Composable
private fun LoginScreenPreview_ErrorState() {
    LoginScreen(
        state = LoginState(
            usernameInput = InputState.Empty,
            passwordInput = InputState.Invalid("123"),
            error = Throwable(),
        ),
        onAction = {},
    )
}

@Preview(
    group = "Mobile",
    name = "Login screen with captcha",
    showBackground = true,
    showSystemUi = true,
)
@Preview(
    group = "TV",
    name = "Login screen with captcha",
    showBackground = true,
    showSystemUi = true,
    device = AUTOMOTIVE_1024p,
    uiMode = UI_MODE_TYPE_TELEVISION,
)
@Composable
private fun LoginScreenPreview_CaptchaRequired(
    @PreviewParameter(LoginScreenPreviewParameterProvider::class) platformType: PlatformType,
) {
    CompositionLocalProvider(LocalPlatformType provides platformType) {
        LoginScreen(state = LoginState(captcha = Captcha("", "", "")), onAction = {})
    }
}

class LoginScreenPreviewParameterProvider : PreviewParameterProvider<PlatformType> {
    override val values = sequenceOf(
        PlatformType.MOBILE,
        PlatformType.TV,
    )
}
