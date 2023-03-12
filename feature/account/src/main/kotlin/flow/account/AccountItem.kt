package flow.account

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import flow.designsystem.component.ConfirmationDialog
import flow.designsystem.component.DialogState
import flow.designsystem.component.TextButton
import flow.designsystem.component.ThemePreviews
import flow.designsystem.theme.FlowTheme
import flow.models.auth.AuthState
import flow.ui.component.Avatar
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import flow.designsystem.R as DesignsystemR

@Composable
fun AccountItem(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit,
) {
    AccountItem(
        modifier = modifier,
        viewModel = hiltViewModel(),
        onLoginClick = onLoginClick,
    )
}

@Composable
internal fun AccountItem(
    modifier: Modifier = Modifier,
    viewModel: AccountViewModel,
    onLoginClick: () -> Unit,
) {
    var confirmationDialogState by remember { mutableStateOf<DialogState>(DialogState.Hide) }
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is AccountSideEffect.OpenLogin -> onLoginClick()
            is AccountSideEffect.HideLogoutConfirmation -> confirmationDialogState = DialogState.Hide
            is AccountSideEffect.ShowLogoutConfirmation -> confirmationDialogState = DialogState.Show
        }
    }
    LogoutConfirmationDialog(
        state = confirmationDialogState,
        onAction = viewModel::perform,
    )
    val state by viewModel.collectAsState()
    AccountItem(
        modifier = modifier,
        state = state,
        onAction = viewModel::perform,
    )
}

@Composable
internal fun AccountItem(
    modifier: Modifier = Modifier,
    state: AuthState,
    onAction: (AccountAction) -> Unit,
) {
    val avatarUrl = remember(state) {
        when (state) {
            is AuthState.Authorized -> state.avatarUrl
            is AuthState.Unauthorized -> null
        }
    }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Avatar(avatarUrl)
            when (state) {
                is AuthState.Authorized -> {
                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        text = state.name,
                        style = MaterialTheme.typography.bodyMedium,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = false,
                    )
                    TextButton(
                        text = stringResource(R.string.account_item_logout_action),
                        onClick = { onAction(AccountAction.LogoutClick) },
                    )
                }

                is AuthState.Unauthorized -> {
                    TextButton(
                        text = stringResource(R.string.account_item_login_action),
                        onClick = { onAction(AccountAction.LoginClick) },
                    )
                }
            }
        }
    }
}

@Composable
internal fun LogoutConfirmationDialog(
    state: DialogState,
    onAction: (AccountAction) -> Unit,
) {
    when (state) {
        is DialogState.Hide -> Unit
        is DialogState.Show -> ConfirmationDialog(
            message = stringResource(R.string.account_item_logout_confirmation),
            positiveButtonText = stringResource(DesignsystemR.string.designsystem_action_yes),
            negativeButtonText = stringResource(DesignsystemR.string.designsystem_action_no),
            onDismiss = { onAction(AccountAction.CancelLogoutClick) },
            onConfirm = { onAction(AccountAction.ConfirmLogoutClick) },
        )
    }
}

@ThemePreviews
@Composable
private fun AccountItem_Unauthorized_Preview() {
    FlowTheme {
        AccountItem(state = AuthState.Unauthorized, onAction = {})
    }
}

@ThemePreviews
@Composable
private fun AccountItem_Authorized_Preview() {
    FlowTheme {
        AccountItem(
            state = AuthState.Authorized("Long-long user name", null),
            onAction = {},
        )
    }
}

@ThemePreviews
@Composable
private fun LogoutConfirmationDialog_Preview() {
    FlowTheme {
        LogoutConfirmationDialog(
            state = DialogState.Show,
            onAction = {},
        )
    }
}
