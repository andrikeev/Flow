package me.rutrackersearch.app.ui.common.auth

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import me.rutrackersearch.app.R
import me.rutrackersearch.app.ui.common.ConfirmationDialog
import me.rutrackersearch.app.ui.common.ConfirmationDialogState
import me.rutrackersearch.app.ui.common.TextButton
import me.rutrackersearch.domain.entity.user.AuthState

@Composable
fun AccountItem(
    modifier: Modifier = Modifier,
    onLogin: () -> Unit,
) {
    AccountItem(
        modifier = modifier,
        viewModel = hiltViewModel(),
        onLogin = onLogin,
    )
}

@Composable
private fun AccountItem(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel,
    onLogin: () -> Unit,
) {
    val state by viewModel.state.collectAsState(AuthState.Unauthorized)
    AccountItem(
        modifier = modifier,
        state = state,
        onLogin = onLogin,
        onLogout = viewModel::logout,
    )
}

@Composable
private fun AccountItem(
    modifier: Modifier = Modifier,
    state: AuthState,
    onLogin: () -> Unit,
    onLogout: () -> Unit,
) {
    var confirmationDialogState by remember {
        mutableStateOf<ConfirmationDialogState>(ConfirmationDialogState.Hide)
    }

    fun onLogoutWithConfirmation() {
        confirmationDialogState = ConfirmationDialogState.Show(
            message = R.string.confirmation_simple,
            onConfirm = { onLogout() }
        )
    }
    ConfirmationDialog(
        state = confirmationDialogState,
        onDismiss = { confirmationDialogState = ConfirmationDialogState.Hide }
    )

    val avatarUrl = when (state) {
        is AuthState.Authorized -> state.account.avatarUrl
        AuthState.Unauthorized -> null
    }.orEmpty()
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
                        text = state.account.name,
                        style = MaterialTheme.typography.bodyMedium,
                        overflow = TextOverflow.Clip,
                        softWrap = false,
                    )
                    TextButton(
                        text = stringResource(R.string.action_logout),
                        onClick = { onLogoutWithConfirmation() },
                    )
                }
                AuthState.Unauthorized -> {
                    TextButton(
                        text = stringResource(R.string.action_login),
                        onClick = onLogin,
                    )
                }
            }
        }
    }
}

@Composable
private fun Avatar(url: String?) {
    AsyncImage(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .size(48.dp)
            .clip(CircleShape),
        model = url,
        placeholder = painterResource(R.drawable.ill_avatar_placeholder),
        error = painterResource(R.drawable.ill_avatar_placeholder),
        contentDescription = null,
    )
}

@Preview(
    showBackground = true,
    widthDp = 200,
)
@Preview(
    showBackground = true,
    widthDp = 500,
)
@Composable
private fun AccountItem_Preview() {
    AccountItem(state = AuthState.Unauthorized, onLogin = {}, onLogout = {})
}
