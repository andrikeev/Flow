package flow.connection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import flow.designsystem.component.Body
import flow.designsystem.component.BodyLarge
import flow.designsystem.component.Button
import flow.designsystem.component.CheckBox
import flow.designsystem.component.Icon
import flow.designsystem.component.OutlinedTextField
import flow.designsystem.component.Surface
import flow.designsystem.component.Text
import flow.designsystem.component.TextButton
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.AppTheme
import flow.models.settings.Proxy
import flow.models.settings.ProxyType
import flow.ui.component.ModalBottomDialog
import flow.designsystem.R as DsR

@Composable
internal fun ProxySettingsDialog(
    proxy: Proxy,
    onConfirm: (Proxy) -> Unit,
    onDismiss: () -> Unit,
) {
    var enabled by remember { mutableStateOf(proxy.enabled) }
    var type by remember { mutableStateOf(proxy.type) }
    var host by remember { mutableStateOf(proxy.host) }
    var port by remember { mutableStateOf(proxy.port.takeIf { it > 0 }?.toString().orEmpty()) }
    var username by remember { mutableStateOf(proxy.username) }
    var password by remember { mutableStateOf(proxy.password) }

    ModalBottomDialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = AppTheme.spaces.large,
                    vertical = AppTheme.spaces.medium,
                ),
        ) {
            BodyLarge(stringResource(R.string.connection_proxy_title))
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            onClick = { enabled = !enabled },
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = AppTheme.spaces.large,
                    vertical = AppTheme.spaces.small,
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spaces.medium),
            ) {
                CheckBox(
                    selectState = if (enabled) ToggleableState.On else ToggleableState.Off,
                    onClick = { enabled = !enabled },
                )
                BodyLarge(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.connection_proxy_enabled),
                )
            }
        }

        Box(
            modifier = Modifier.padding(
                start = AppTheme.spaces.large,
                top = AppTheme.spaces.medium,
                end = AppTheme.spaces.large,
            ),
        ) {
            Body(
                text = stringResource(R.string.connection_proxy_type),
                color = AppTheme.colors.outline,
            )
        }
        ProxyTypeItem(
            label = stringResource(R.string.connection_proxy_type_http),
            selected = type == ProxyType.HTTP,
            enabled = enabled,
            onClick = { type = ProxyType.HTTP },
        )
        ProxyTypeItem(
            label = stringResource(R.string.connection_proxy_type_socks),
            selected = type == ProxyType.SOCKS,
            enabled = enabled,
            onClick = { type = ProxyType.SOCKS },
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = AppTheme.spaces.large,
                    vertical = AppTheme.spaces.small,
                ),
            value = host,
            onValueChange = { host = it.trim() },
            enabled = enabled,
            singleLine = true,
            label = { Text(stringResource(R.string.connection_proxy_host)) },
            placeholder = { Text(stringResource(R.string.connection_proxy_host_hint)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = AppTheme.spaces.large,
                    vertical = AppTheme.spaces.small,
                ),
            value = port,
            onValueChange = { value -> port = value.filter(Char::isDigit).take(5) },
            enabled = enabled,
            singleLine = true,
            label = { Text(stringResource(R.string.connection_proxy_port)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = AppTheme.spaces.large,
                    vertical = AppTheme.spaces.small,
                ),
            value = username,
            onValueChange = { username = it },
            enabled = enabled,
            singleLine = true,
            label = { Text(stringResource(R.string.connection_proxy_username)) },
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = AppTheme.spaces.large,
                    vertical = AppTheme.spaces.small,
                ),
            value = password,
            onValueChange = { password = it },
            enabled = enabled,
            singleLine = true,
            label = { Text(stringResource(R.string.connection_proxy_password)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = AppTheme.spaces.large,
                    vertical = AppTheme.spaces.medium,
                ),
            horizontalArrangement = Arrangement.spacedBy(
                space = AppTheme.spaces.medium,
                alignment = Alignment.End,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(
                text = stringResource(DsR.string.designsystem_action_cancel),
                onClick = onDismiss,
            )
            Button(
                text = stringResource(DsR.string.designsystem_action_apply),
                onClick = {
                    onConfirm(
                        Proxy(
                            enabled = enabled,
                            type = type,
                            host = host.trim(),
                            port = port.toIntOrNull() ?: 0,
                            username = username.trim(),
                            password = password,
                        ),
                    )
                },
            )
        }
    }
}

@Composable
private fun ProxyTypeItem(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) = Surface(
    modifier = Modifier
        .fillMaxWidth()
        .defaultMinSize(minHeight = AppTheme.sizes.large),
    enabled = enabled,
    onClick = onClick,
) {
    Row(
        modifier = Modifier.padding(horizontal = AppTheme.spaces.large),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spaces.mediumSmall),
    ) {
        Icon(
            icon = if (selected) FlowIcons.Selected else FlowIcons.NotSelected,
            tint = if (enabled) AppTheme.colors.onSurface else AppTheme.colors.outline,
            contentDescription = null,
        )
        BodyLarge(
            modifier = Modifier.weight(1f),
            text = label,
        )
    }
}
