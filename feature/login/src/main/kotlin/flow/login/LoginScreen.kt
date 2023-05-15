package flow.login

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import flow.designsystem.component.LocalSnackbarHostState
import flow.designsystem.component.Scaffold
import flow.designsystem.component.ThemePreviews
import flow.designsystem.theme.AppTheme
import flow.designsystem.theme.FlowTheme
import flow.login.LoginAction.SubmitClick
import flow.models.auth.Captcha
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun LoginScreen(
    viewModel: LoginViewModel,
    back: () -> Unit,
) {
    val resources = LocalContext.current.resources
    val keyboardController = LocalSoftwareKeyboardController.current
    val snackbarState = LocalSnackbarHostState.current
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is LoginSideEffect.Error -> {
                snackbarState.showSnackbar(resources.getString(flow.ui.R.string.error_something_goes_wrong))
            }

            is LoginSideEffect.HideKeyboard -> keyboardController?.hide()
            is LoginSideEffect.Success -> back()
        }
    }
    val state by viewModel.collectAsState()
    LoginScreen(
        state = state,
        onAction = viewModel::perform,
    )
}

@Composable
internal fun LoginScreen(
    state: LoginState,
    onAction: (LoginAction) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    fun submit() = onAction(SubmitClick)

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(
                    start = AppTheme.spaces.large,
                    top = padding.calculateTopPadding(),
                    end = AppTheme.spaces.large,
                    bottom = padding.calculateBottomPadding(),
                )
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
            LoginScreenHeader()
            UsernameInputField(
                state = state,
                onChanged = { onAction(LoginAction.UsernameChanged(it)) },
                onSelectNext = { focusManager.moveFocus(FocusDirection.Next) },
            )
            PasswordInputField(
                state = state,
                onChanged = { onAction(LoginAction.PasswordChanged(it)) },
                onSelectNext = { focusManager.moveFocus(FocusDirection.Next) },
                onSubmit = { submit() },
            )
            if (state.captcha != null) {
                CaptchaImage(
                    modifier = Modifier.size(100.dp),
                    captcha = state.captcha,
                )
                CaptchaInputField(
                    state = state,
                    onChanged = { onAction(LoginAction.CaptchaChanged(it)) },
                    onSubmit = { submit() },
                )
            }
            LoginButton(
                modifier = Modifier.padding(AppTheme.spaces.large),
                state = state,
                onSubmit = { submit() }
            )
        }
    }
}

@ThemePreviews
@Composable
private fun LoginScreenPreview_DefaultState() {
    FlowTheme {
        LoginScreen(
            state = LoginState(),
            onAction = {},
        )
    }
}

@ThemePreviews
@Composable
private fun LoginScreenPreview_LoadingState() {
    FlowTheme {
        LoginScreen(
            state = LoginState(isLoading = true),
            onAction = {},
        )
    }
}

@ThemePreviews
@Composable
private fun LoginScreenPreview_CaptchaRequired() {
    FlowTheme {
        LoginScreen(
            state = LoginState(captcha = Captcha("", "", "")),
            onAction = {},
        )
    }
}

@ThemePreviews
@Composable
private fun LoginScreenPreview_ErrorState() {
    FlowTheme {
        LoginScreen(
            state = LoginState(
                usernameInput = InputState.Empty,
                passwordInput = InputState.Invalid(TextFieldValue("123")),
            ),
            onAction = {},
        )
    }
}
