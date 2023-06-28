package flow.menu

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import flow.account.AccountItem
import flow.connection.ConnectionItem
import flow.designsystem.component.AppBar
import flow.designsystem.component.Body
import flow.designsystem.component.Button
import flow.designsystem.component.ConfirmationDialog
import flow.designsystem.component.Dialog
import flow.designsystem.component.DropdownMenu
import flow.designsystem.component.Icon
import flow.designsystem.component.LazyList
import flow.designsystem.component.ProvideTextStyle
import flow.designsystem.component.Scaffold
import flow.designsystem.component.Surface
import flow.designsystem.component.Text
import flow.designsystem.component.TextButton
import flow.designsystem.component.ThemePreviews
import flow.ui.component.VisibilityState
import flow.designsystem.component.rememberConfirmationDialogState
import flow.ui.component.rememberVisibilityState
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.AppTheme
import flow.designsystem.theme.FlowTheme
import flow.designsystem.theme.isMaterialYouAvailable
import flow.menu.MenuAction.AboutClick
import flow.menu.MenuAction.ClearBookmarksConfirmation
import flow.menu.MenuAction.ClearFavoritesConfirmation
import flow.menu.MenuAction.ClearHistoryConfirmation
import flow.menu.MenuAction.ConfirmableAction
import flow.menu.MenuAction.LoginClick
import flow.menu.MenuAction.MyTipsClick
import flow.menu.MenuAction.NetMonetClick
import flow.menu.MenuAction.PayPalClick
import flow.menu.MenuAction.SendFeedbackClick
import flow.menu.MenuAction.SetBookmarksSyncPeriod
import flow.menu.MenuAction.SetFavoritesSyncPeriod
import flow.menu.MenuAction.SetTheme
import flow.models.settings.SyncPeriod
import flow.models.settings.Theme
import flow.navigation.viewModel
import flow.ui.permissions.Permission
import flow.ui.permissions.rememberPermissionState
import flow.ui.permissions.shouldShowRationale
import flow.ui.platform.LocalOpenLinkHandler
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import java.util.Calendar
import flow.designsystem.R as DsR

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
    val confirmationDialogState = rememberConfirmationDialogState()
    ConfirmationDialog(confirmationDialogState)
    val aboutDialogState = rememberVisibilityState()
    AboutAppDialog(aboutDialogState)
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is MenuSideEffect.OpenLogin -> openLogin()
            is MenuSideEffect.OpenLink -> openLinkHandler.openLink(sideEffect.link)
            is MenuSideEffect.ShowAbout -> aboutDialogState.show()
            is MenuSideEffect.ShowConfirmation -> {
                confirmationDialogState.show(
                    title = sideEffect.title,
                    text = sideEffect.confirmationMessage,
                    onConfirm = sideEffect.action,
                    onDismiss = confirmationDialogState::hide,
                )
            }
        }
    }
    val state by viewModel.collectAsState()
    MenuScreen(state, viewModel::perform)
}

@Composable
private fun MenuScreen(
    state: MenuState,
    onAction: (MenuAction) -> Unit,
) = Scaffold(
    topBar = { appBarState ->
        AppBar(
            title = { Text(stringResource(R.string.menu_title)) },
            appBarState = appBarState,
        )
    },
) { padding ->
    val (theme, favoritesSyncPeriod, bookmarksSyncPeriod) = state
    LazyList(
        modifier = Modifier.padding(padding),
        contentPadding = PaddingValues(vertical = AppTheme.spaces.medium),
    ) {
        menuAccountItem { onAction(LoginClick) }
        menuDonateItem(onAction)
        menuSectionLabel { Text(stringResource(R.string.menu_label_settings)) }
        menuSelectionItem(
            title = { Text(stringResource(R.string.menu_settings_theme)) },
            items = Theme.availableValues(),
            selected = theme,
            labelMapper = { theme -> stringResource(theme.resId) },
            onSelect = { theme -> onAction(SetTheme(theme)) },
        )
        endpointSelectionItem()
        menuSyncSelectionItem(
            title = { Text(stringResource(R.string.menu_settings_favorites_sync)) },
            items = SyncPeriod.values(),
            selected = favoritesSyncPeriod,
            labelMapper = { syncPeriod -> stringResource(syncPeriod.resId) },
            onSelect = { syncPeriod -> onAction(SetFavoritesSyncPeriod(syncPeriod)) },
        )
        menuSyncSelectionItem(
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
                    ConfirmableAction(
                        title = R.string.menu_data_clear_history_title,
                        confirmationMessage = R.string.menu_data_clear_history_confirmation,
                        onConfirmAction = { onAction(ClearHistoryConfirmation) },
                    )
                )
            },
        )
        menuItem(
            text = { Text(stringResource(R.string.menu_data_clear_bookmarks)) },
            onClick = {
                onAction(
                    ConfirmableAction(
                        title = R.string.menu_data_clear_bookmarks_title,
                        confirmationMessage = R.string.menu_data_clear_bookmarks_confirmation,
                        onConfirmAction = { onAction(ClearBookmarksConfirmation) },
                    )
                )
            },
        )
        menuItem(
            text = { Text(stringResource(R.string.menu_data_clear_favorites)) },
            onClick = {
                onAction(
                    ConfirmableAction(
                        title = R.string.menu_data_clear_favorites_title,
                        confirmationMessage = R.string.menu_data_clear_favorites_confirmation,
                        onConfirmAction = { onAction(ClearFavoritesConfirmation) },
                    )
                )
            },
        )
        menuSectionLabel { Text(stringResource(R.string.menu_label_misc)) }
        menuItem(
            text = { Text(stringResource(R.string.menu_misc_rights)) },
            onClick = { onAction(MenuAction.RightsClick) },
        )
        menuItem(
            text = { Text(stringResource(R.string.menu_misc_privacy)) },
            onClick = { onAction(MenuAction.PrivacyPolicyClick) },
        )
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

private fun LazyListScope.menuAccountItem(
    onLoginClick: () -> Unit,
) = item { AccountItem(onLoginClick = onLoginClick) }

private fun LazyListScope.menuDonateItem(
    onAction: (MenuAction) -> Unit,
) = item { MenuDonateItem(onAction = onAction) }

private fun LazyListScope.menuItem(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
) = item { MenuItem(text, onClick) }

private fun LazyListScope.endpointSelectionItem() = item { ConnectionItem() }

private fun <T> LazyListScope.menuSelectionItem(
    title: @Composable () -> Unit,
    items: Array<T>,
    selected: T,
    labelMapper: @Composable (T) -> String,
    onSelect: (T) -> Unit,
) = item { MenuSelectionItem(title, items, selected, labelMapper, onSelect) }

private fun LazyListScope.menuSyncSelectionItem(
    title: @Composable () -> Unit,
    items: Array<SyncPeriod>,
    selected: SyncPeriod,
    labelMapper: @Composable (SyncPeriod) -> String,
    onSelect: (SyncPeriod) -> Unit,
) = item { MenuSyncSelectionItem(title, items, selected, labelMapper, onSelect) }

private fun LazyListScope.menuSectionLabel(
    label: @Composable () -> Unit,
) = item {
    Box(
        modifier = Modifier.padding(
            start = AppTheme.spaces.large,
            top = AppTheme.spaces.large,
            end = AppTheme.spaces.large,
            bottom = AppTheme.spaces.small,
        ),
    ) {
        ProvideTextStyle(
            value = AppTheme.typography.labelMedium.copy(
                color = AppTheme.colors.primary,
            ),
            content = label,
        )
    }
}

@Composable
private fun MenuItem(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
) = Surface(
    modifier = Modifier
        .fillMaxWidth()
        .height(AppTheme.sizes.large),
    onClick = onClick,
) {
    Box(
        modifier = Modifier.padding(horizontal = AppTheme.spaces.large),
        contentAlignment = Alignment.CenterStart,
    ) {
        ProvideTextStyle(AppTheme.typography.bodyLarge, text)
    }
}

@Composable
private fun MenuSyncSelectionItem(
    title: @Composable () -> Unit,
    items: Array<SyncPeriod>,
    selected: SyncPeriod,
    labelMapper: @Composable (SyncPeriod) -> String,
    onSelect: (SyncPeriod) -> Unit,
) {
    val permission = rememberPermissionState(Permission.PostNotifications)
    val confirmationDialogState = rememberConfirmationDialogState()
    ConfirmationDialog(confirmationDialogState)
    MenuSelectionItem(
        title = title,
        items = items,
        selected = selected,
        labelMapper = labelMapper,
        onSelect = { syncPeriod ->
            onSelect(syncPeriod)
            if (syncPeriod != SyncPeriod.OFF) {
                if (permission.status.shouldShowRationale) {
                    confirmationDialogState.show(
                        icon = FlowIcons.Notifications,
                        R.string.permission_show_notifications_rationale_title,
                        R.string.permission_show_notifications_rationale,
                        flow.designsystem.R.string.designsystem_action_ok,
                        flow.designsystem.R.string.designsystem_action_cancel,
                        permission::requestPermission,
                        confirmationDialogState::hide,
                    )
                } else {
                    permission.requestPermission()
                }
            }
        },
    )
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
        modifier = Modifier
            .fillMaxWidth()
            .height(AppTheme.sizes.extraLarge),
        onClick = { showDropdown = true },
    ) {
        Column(
            modifier = Modifier.padding(horizontal = AppTheme.spaces.large),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
        ) {
            ProvideTextStyle(AppTheme.typography.bodyLarge, title)
            Body(
                text = labelMapper.invoke(selected),
                color = AppTheme.colors.outline,
            )
            DropdownMenu(
                expanded = showDropdown,
                onDismissRequest = { showDropdown = false },
                offset = DpOffset(24.dp, (-32).dp),
                items = items.asIterable(),
                labelMapper = labelMapper,
                onSelect = onSelect,
            )
        }
    }
}

@Composable
private fun MenuDonateItem(onAction: (MenuAction) -> Unit) {
    val donateDialogState = rememberVisibilityState()
    if (donateDialogState.visible) {
        Dialog(
            iconContentColor = AppTheme.colors.primary,
            title = { Text(text = stringResource(R.string.support_development_title)) },
            text = {
                Column {
                    Text(text = stringResource(R.string.support_development_line1))
                    Spacer(modifier = Modifier.height(AppTheme.spaces.medium))
                    Text(text = stringResource(R.string.support_development_line2))
                    Spacer(modifier = Modifier.height(AppTheme.spaces.large))
                    Button(
                        modifier = Modifier
                            .padding(horizontal = AppTheme.spaces.large)
                            .fillMaxWidth(),
                        text = stringResource(R.string.support_development_paypal),
                        onClick = { onAction(PayPalClick) },
                        color = AppTheme.colors.accentBlue,
                    )
                    Button(
                        modifier = Modifier
                            .padding(horizontal = AppTheme.spaces.large)
                            .fillMaxWidth(),
                        text = stringResource(R.string.support_development_netmonet),
                        onClick = { onAction(NetMonetClick) },
                        color = AppTheme.colors.accentOrange,
                    )
                    Button(
                        modifier = Modifier
                            .padding(horizontal = AppTheme.spaces.large)
                            .fillMaxWidth(),
                        text = stringResource(R.string.support_development_mytips),
                        onClick = { onAction(MyTipsClick) },
                        color = AppTheme.colors.accentGreen,
                    )
                }
            },
            onDismissRequest = donateDialogState::hide,
            confirmButton = {
                TextButton(
                    text = stringResource(DsR.string.designsystem_action_close),
                    onClick = donateDialogState::hide,
                )
            },
        )
    }
    val itemState = rememberVisibilityState(true)
    if (itemState.visible) {
        Surface(
            modifier = Modifier.padding(AppTheme.spaces.large),
            onClick = { donateDialogState.show() },
            shape = AppTheme.shapes.large,
            tonalElevation = AppTheme.elevations.small,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppTheme.spaces.large),
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd,
                ) {
                    Icon(
                        modifier = Modifier
                            .size(AppTheme.sizes.small)
                            .clickable(onClick = itemState::hide),
                        icon = FlowIcons.Clear,
                        contentDescription = null,
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(AppTheme.spaces.medium)
                            .size(AppTheme.sizes.medium),
                        icon = FlowIcons.StarFull,
                        contentDescription = null,
                    )
                    Text(
                        modifier = Modifier.padding(AppTheme.spaces.medium),
                        text = stringResource(R.string.support_development_title),
                        style = AppTheme.typography.titleMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun AboutAppDialog(state: VisibilityState) {
    if (state.visible) {
        val packageInfo = getPackageInfo()
        Dialog(
            icon = {
                Icon(
                    icon = FlowIcons.AppIcon,
                    contentDescription = null,
                )
            },
            iconContentColor = AppTheme.colors.primary,
            title = { Text(packageInfo.getAppName()) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(stringResource(R.string.app_version, packageInfo.getAppVersionName()))
                    Text(
                        stringResource(
                            R.string.app_copyright,
                            Calendar.getInstance().get(Calendar.YEAR)
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    text = stringResource(DsR.string.designsystem_action_ok),
                    onClick = state::hide,
                )
            },
            onDismissRequest = state::hide,
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

private fun Theme.Companion.availableValues(): Array<Theme> {
    return if (isMaterialYouAvailable()) {
        Theme.values()
    } else {
        Theme.values().filter { it != Theme.DYNAMIC }.toTypedArray()
    }
}

@ThemePreviews
@Composable
private fun AboutDialog_Preview() {
    FlowTheme(isDynamic = false) {
        val dialogState = rememberVisibilityState(true)
        AboutAppDialog(dialogState)
    }
}

@ThemePreviews
@Composable
private fun MenuDonateItem_Preview() {
    FlowTheme(isDynamic = false) {
        MenuDonateItem {}
    }
}

