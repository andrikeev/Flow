package flow.menu

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import flow.account.AccountItem
import flow.designsystem.component.AppBar
import flow.designsystem.component.AppBarDefaults
import flow.designsystem.component.ConfirmationDialog
import flow.designsystem.component.ConfirmationDialogState
import flow.designsystem.component.Focusable
import flow.designsystem.component.LazyList
import flow.designsystem.component.Scaffold
import flow.designsystem.component.TextButton
import flow.designsystem.component.ThemePreviews
import flow.designsystem.component.focusableSpec
import flow.designsystem.component.rememberFocusRequester
import flow.designsystem.theme.FlowTheme
import flow.menu.MenuAction.AboutClick
import flow.menu.MenuAction.ClearBookmarksConfirmation
import flow.menu.MenuAction.ClearFavoritesConfirmation
import flow.menu.MenuAction.ClearHistoryConfirmation
import flow.menu.MenuAction.ConfirmableAction
import flow.menu.MenuAction.LoginClick
import flow.menu.MenuAction.SendFeedbackClick
import flow.menu.MenuAction.SetBookmarksSyncPeriod
import flow.menu.MenuAction.SetFavoritesSyncPeriod
import flow.menu.MenuAction.SetTheme
import flow.models.settings.SyncPeriod
import flow.models.settings.Theme
import flow.navigation.viewModel
import flow.ui.platform.LocalOpenLinkHandler
import kotlinx.coroutines.job
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import flow.designsystem.R as DesignsystemR
import flow.ui.R as UiR

@Composable
fun MenuScreen(openLogin: () -> Unit) {
    MenuScreen(
        viewModel = viewModel(),
        openLogin = openLogin,
    )
}

@Composable
private fun MenuScreen(
    viewModel: MenuViewModel,
    openLogin: () -> Unit,
) {
    val openLinkHandler = LocalOpenLinkHandler.current
    var confirmationDialogState by remember {
        mutableStateOf<ConfirmationDialogState>(ConfirmationDialogState.Hide)
    }
    var showAboutDialog by remember { mutableStateOf(false) }
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is MenuSideEffect.OpenLogin -> openLogin()
            is MenuSideEffect.OpenLink -> openLinkHandler.openLink(sideEffect.link)
            is MenuSideEffect.ShowAbout -> showAboutDialog = true
            is MenuSideEffect.ShowConfirmation -> {
                confirmationDialogState = ConfirmationDialogState.Show(
                    message = sideEffect.confirmationMessage,
                    onConfirm = sideEffect.action,
                )
            }
        }
    }
    ConfirmationDialog(
        state = confirmationDialogState,
        onDismiss = { confirmationDialogState = ConfirmationDialogState.Hide },
    )
    AboutAppDialog(
        show = showAboutDialog,
        onDismiss = { showAboutDialog = false },
    )
    val state by viewModel.collectAsState()
    MenuScreen(state, viewModel::perform)
}

@Composable
internal fun MenuScreen(
    state: MenuState,
    onAction: (MenuAction) -> Unit,
) {
    val scrollBehavior = AppBarDefaults.appBarScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppBar(
                title = { Text(stringResource(UiR.string.menu_label)) },
                appBarState = scrollBehavior.state,
            )
        },
    ) {
        val (theme, favoritesSyncPeriod, bookmarksSyncPeriod) = state
        LazyList(
            modifier = Modifier.padding(it),
            contentPadding = PaddingValues(vertical = 8.dp),
        ) {
            menuAccountItem { onAction(LoginClick) }
            menuSectionLabel { Text(stringResource(R.string.menu_label_settings)) }
            menuSelectionItem(
                title = { Text(stringResource(R.string.menu_settings_theme)) },
                items = Theme.values(),
                selected = theme,
                labelMapper = { theme -> stringResource(theme.resId) },
                onSelect = { theme -> onAction(SetTheme(theme)) },
            )
            menuSelectionItem(
                title = { Text(stringResource(R.string.menu_settings_favorites_sync)) },
                items = SyncPeriod.values(),
                selected = favoritesSyncPeriod,
                labelMapper = { syncPeriod -> stringResource(syncPeriod.resId) },
                onSelect = { syncPeriod -> onAction(SetFavoritesSyncPeriod(syncPeriod)) },
            )
            menuSelectionItem(
                title = { Text(stringResource(R.string.menu_settings_bookmarks_sync)) },
                items = SyncPeriod.values(),
                selected = bookmarksSyncPeriod,
                labelMapper = { syncPeriod -> stringResource(syncPeriod.resId) },
                onSelect = { syncPeriod -> onAction(SetBookmarksSyncPeriod(syncPeriod)) },
            )
            menuSectionLabel { Text(stringResource(R.string.menu_label_data)) }
            menuItem(
                text = { Text(stringResource(R.string.menu_data_clear_history)) },
                onClick = {
                    onAction(
                        ConfirmableAction(confirmationMessage = R.string.menu_data_clear_history_confirmation,
                            onConfirmAction = { onAction(ClearHistoryConfirmation) })
                    )
                },
            )
            menuItem(
                text = { Text(stringResource(R.string.menu_data_clear_bookmarks)) },
                onClick = {
                    onAction(
                        ConfirmableAction(confirmationMessage = R.string.menu_data_clear_bookmarks_confirmation,
                            onConfirmAction = { onAction(ClearBookmarksConfirmation) })
                    )
                },
            )
            menuItem(
                text = { Text(stringResource(R.string.menu_data_clear_favorites)) },
                onClick = {
                    onAction(
                        ConfirmableAction(confirmationMessage = R.string.menu_data_clear_favorites_confirmation,
                            onConfirmAction = { onAction(ClearFavoritesConfirmation) })
                    )
                },
            )
            menuSectionLabel { Text(stringResource(R.string.menu_label_misc)) }
//            menuItem(
//                text = { Text(stringResource(R.string.menu_misc_rights)) },
//                onClick = { onAction(MenuAction.RightsClick) },
//            )
//            menuItem(
//                text = { Text(stringResource(R.string.menu_misc_privacy)) },
//                onClick = { onAction(MenuAction.PrivacyPolicyClick) },
//            )
            menuItem(
                text = { Text(stringResource(R.string.menu_misc_contacts)) },
                onClick = { onAction(SendFeedbackClick) },
            )
            menuItem(
                text = { Text(stringResource(R.string.menu_misc_about)) },
                onClick = { onAction(AboutClick) },
            )
        }
    }
}

private fun LazyListScope.menuAccountItem(
    onLoginClick: () -> Unit,
) = item { AccountItem(onLoginClick = onLoginClick) }

private fun LazyListScope.menuItem(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
) = item { MenuItem(text, onClick) }

private fun <T> LazyListScope.menuSelectionItem(
    title: @Composable () -> Unit,
    items: Array<T>,
    selected: T,
    labelMapper: @Composable (T) -> String,
    onSelect: (T) -> Unit,
) = item { MenuSelectionItem(title, items, selected, labelMapper, onSelect) }

private fun LazyListScope.menuSectionLabel(
    label: @Composable () -> Unit,
) = item {
    Box(modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp)) {
        ProvideTextStyle(
            value = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.primary,
            ),
            content = label,
        )
    }
}

@Composable
private fun MenuItem(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(56.dp),
        onClick = onClick,
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            ProvideTextStyle(MaterialTheme.typography.bodyLarge, text)
        }
    }
}

@Composable
private fun <T> MenuSelectionItem(
    title: @Composable () -> Unit,
    items: Array<T>,
    selected: T,
    labelMapper: @Composable (T) -> String,
    onSelect: (T) -> Unit,
) {
    var showDropdown by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier.fillMaxWidth().height(72.dp),
        onClick = { showDropdown = true },
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
        ) {
            ProvideTextStyle(MaterialTheme.typography.bodyLarge, title)
            Text(
                text = labelMapper.invoke(selected),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.outline,
                ),
            )
            DropdownMenu(
                expanded = showDropdown,
                onDismissRequest = { showDropdown = false },
                offset = DpOffset(24.dp, (-32).dp),
            ) {
                val focusRequester = rememberFocusRequester()
                LaunchedEffect(showDropdown) {
                    coroutineContext.job.invokeOnCompletion { error ->
                        if (error == null) {
                            focusRequester.requestFocus()
                        }
                    }
                }
                items.forEachIndexed { index, item ->
                    Focusable(
                        modifier = Modifier.focusRequester(
                            if (index == 0) {
                                focusRequester
                            } else {
                                FocusRequester.Default
                            }
                        ), spec = focusableSpec(color = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                onSelect(item)
                                showDropdown = false
                            },
                            text = { Text(labelMapper.invoke(item)) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AboutAppDialog(
    show: Boolean,
    onDismiss: () -> Unit,
) {
    if (show) {
        val packageInfo = getPackageInfo()
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                Icon(
                    painter = painterResource(UiR.drawable.ic_notification),
                    contentDescription = null,
                )
            },
            iconContentColor = MaterialTheme.colorScheme.primary,
            title = { Text(packageInfo.getAppName()) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(stringResource(R.string.app_version, packageInfo.getAppVersionName()))
                    Text(stringResource(R.string.app_copyright))
                }
            },
            confirmButton = {
                TextButton(
                    text = stringResource(DesignsystemR.string.designsystem_action_ok),
                    onClick = onDismiss,
                )
            },
        )
    }
}

@Composable
private fun getPackageInfo(): PackageInfo? {
    val context = LocalContext.current
    return remember {
        runCatching {
            val packageManager = context.packageManager
            val packageName = context.packageName
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION") packageManager.getPackageInfo(packageName, 0)
            }
        }.getOrElse { null }
    }
}

@Composable
private fun PackageInfo?.getAppName(): String {
    return this?.applicationInfo?.labelRes?.let { stringResource(it) } ?: "null"
}

@Composable
private fun PackageInfo?.getAppVersionName(): String = this?.versionName ?: "null"

private val Theme.resId: Int
    get() = when (this) {
        Theme.SYSTEM -> R.string.theme_system
        Theme.DYNAMIC -> R.string.theme_dynamic
        Theme.DARK -> R.string.theme_dark
        Theme.LIGHT -> R.string.theme_light
    }

private val SyncPeriod.resId: Int
    get() = when (this) {
        SyncPeriod.OFF -> R.string.sync_period_off
        SyncPeriod.HOUR -> R.string.sync_period_hour
        SyncPeriod.SIX_HOURS -> R.string.sync_period_six_hours
        SyncPeriod.TWELVE_HOURS -> R.string.sync_period_twelve_hours
        SyncPeriod.DAY -> R.string.sync_period_daily
        SyncPeriod.WEEK -> R.string.sync_period_weekly
    }


@ThemePreviews
@Composable
private fun MenuScreen_Preview() {
    FlowTheme {
        MenuScreen(state = MenuState(), onAction = {})
    }
}

@ThemePreviews
@Composable
private fun AboutDialog_Preview() {
    FlowTheme {
        AboutAppDialog(show = true, onDismiss = {})
    }
}
