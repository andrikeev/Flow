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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import flow.auth.models.Captcha
import flow.designsystem.component.Scaffold
import flow.designsystem.component.ThemePreviews
import flow.designsystem.theme.FlowTheme
import flow.login.LoginAction.SubmitClick
import flow.models.InputState
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import flow.ui.R as UiR

@Composable
fun LoginScreen(back: () -> Unit) {
    LoginScreen(
        viewModel = hiltViewModel(),
        back = back,
    )
}

@Composable
internal fun LoginScreen(
    viewModel: LoginViewModel,
    back: () -> Unit,
) {
    val resources = LocalContext.current.resources
    val keyboardController = LocalSoftwareKeyboardController.current
    val snackbarState = remember { SnackbarHostState() }
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is LoginSideEffect.Error -> {
                snackbarState.showSnackbar(resources.getString(UiR.string.error_something_goes_wrong))
            }

            is LoginSideEffect.HideKeyboard -> keyboardController?.hide()
            is LoginSideEffect.Success -> back()
        }
    }
    val state by viewModel.collectAsState()
    LoginScreen(
        state = state,
        snackbarState = snackbarState,
        onAction = viewModel::perform,
    )
}

@Composable
internal fun LoginScreen(
    state: LoginState,
    snackbarState: SnackbarHostState = remember { SnackbarHostState() },
    onAction: (LoginAction) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    fun submit() = onAction(SubmitClick)

    val colors = TextFieldDefaults.outlinedTextFieldColors(
        focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarState) },
        content = { paddingValues ->
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
                LoginScreenHeader()
                UsernameInputField(
                    state = state,
                    onChanged = { onAction(LoginAction.UsernameChanged(it)) },
                    onSelectNext = { focusManager.moveFocus(FocusDirection.Next) },
                    colors = colors,
                )
                PasswordInputField(
                    state = state,
                    onChanged = { onAction(LoginAction.PasswordChanged(it)) },
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
                        onChanged = { onAction(LoginAction.CaptchaChanged(it)) },
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
    )
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
                passwordInput = InputState.Invalid("123"),
            ),
            onAction = {},
        )
    }
}
