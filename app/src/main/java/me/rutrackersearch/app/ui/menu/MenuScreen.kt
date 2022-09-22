package me.rutrackersearch.app.ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.job
import me.rutrackersearch.app.BuildConfig
import me.rutrackersearch.app.R
import me.rutrackersearch.app.ui.common.AppBar
import me.rutrackersearch.app.ui.common.ConfirmationDialog
import me.rutrackersearch.app.ui.common.ConfirmationDialogState
import me.rutrackersearch.app.ui.common.ContentElevation
import me.rutrackersearch.app.ui.common.DynamicBox
import me.rutrackersearch.app.ui.common.Focusable
import me.rutrackersearch.app.ui.common.FocusableLazyColumn
import me.rutrackersearch.app.ui.common.FocusableLazyListScope
import me.rutrackersearch.app.ui.common.LazyColumn
import me.rutrackersearch.app.ui.common.TextButton
import me.rutrackersearch.app.ui.common.account.AccountItem
import me.rutrackersearch.app.ui.common.focusableSpec
import me.rutrackersearch.app.ui.common.rememberFocusRequester
import me.rutrackersearch.app.ui.common.resId
import me.rutrackersearch.app.ui.menu.MenuAction.ClearBookmarksClick
import me.rutrackersearch.app.ui.menu.MenuAction.ClearFavoritesClick
import me.rutrackersearch.app.ui.menu.MenuAction.ClearHistoryClick
import me.rutrackersearch.app.ui.menu.MenuAction.LoginClick
import me.rutrackersearch.app.ui.menu.MenuAction.SetBookmarksSyncPeriod
import me.rutrackersearch.app.ui.menu.MenuAction.SetFavoritesSyncPeriod
import me.rutrackersearch.app.ui.menu.MenuAction.SetTheme
import me.rutrackersearch.app.ui.platform.LocalOpenLinkHandler
import me.rutrackersearch.app.ui.theme.availableThemes
import me.rutrackersearch.models.settings.SyncPeriod
import me.rutrackersearch.models.settings.Theme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun MenuScreen(openLogin: () -> Unit) {
    MenuScreen(
        viewModel = hiltViewModel(),
        openLogin = openLogin,
    )
}

@Composable
private fun MenuScreen(
    viewModel: MenuViewModel,
    openLogin: () -> Unit,
) {
    val openLinkHandler = LocalOpenLinkHandler.current
    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is MenuSideEffect.OpenLogin -> openLogin()
            is MenuSideEffect.OpenLink -> openLinkHandler.openLink(sideEffect.link)
        }
    }
    val state by viewModel.collectAsState()
    DynamicBox(
        mobileContent = { MenuScreen(state, viewModel::perform) },
        tvContent = { TVMenuScreen(state, viewModel::perform) },
    )
}

@Composable
private fun MenuScreen(
    state: MenuState,
    onAction: (MenuAction) -> Unit,
) {
    val (theme, favoritesSyncPeriod, bookmarksSyncPeriod) = state
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppBar(
                title = { Text(stringResource(R.string.menu_label)) },
                scrollBehavior = scrollBehavior,
            )
        },
    ) {
        var confirmationDialogState by remember {
            mutableStateOf<ConfirmationDialogState>(ConfirmationDialogState.Hide)
        }
        ConfirmationDialog(
            state = confirmationDialogState,
            onDismiss = { confirmationDialogState = ConfirmationDialogState.Hide },
        )
        var showAboutDialog by remember { mutableStateOf(false) }
        AboutAppDialog(
            show = showAboutDialog,
            onDismiss = { showAboutDialog = false },
        )
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(vertical = 8.dp),
        ) {
            menuAccountItem { onAction(LoginClick) }
            menuSectionLabel { Text(stringResource(R.string.menu_label_settings)) }
            menuSelectionItem(
                title = { Text(stringResource(R.string.menu_settings_theme)) },
                items = Theme.availableThemes(),
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
                    confirmationDialogState = ConfirmationDialogState.Show(
                        message = R.string.menu_data_clear_history_confirmation,
                        onConfirm = { onAction(ClearHistoryClick) },
                    )
                },
            )
            menuItem(
                text = { Text(stringResource(R.string.menu_data_clear_bookmarks)) },
                onClick = {
                    confirmationDialogState = ConfirmationDialogState.Show(
                        message = R.string.menu_data_clear_bookmarks_confirmation,
                        onConfirm = { onAction(ClearBookmarksClick) },
                    )
                },
            )
            menuItem(
                text = { Text(stringResource(R.string.menu_data_clear_favorites)) },
                onClick = {
                    confirmationDialogState = ConfirmationDialogState.Show(
                        message = R.string.menu_data_clear_favorites_confirmation,
                        onConfirm = { onAction(ClearFavoritesClick) },
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
                onClick = { onAction(MenuAction.SendFeedbackClick) },
            )
            menuItem(
                text = { Text(stringResource(R.string.menu_misc_about)) },
                onClick = { showAboutDialog = true },
            )
        }
    }
}

@Composable
private fun TVMenuScreen(
    state: MenuState,
    onAction: (MenuAction) -> Unit,
) {
    val (theme, favoritesSyncPeriod, bookmarksSyncPeriod) = state
    var confirmationDialogState by remember {
        mutableStateOf<ConfirmationDialogState>(ConfirmationDialogState.Hide)
    }
    ConfirmationDialog(
        state = confirmationDialogState,
        onDismiss = { confirmationDialogState = ConfirmationDialogState.Hide },
    )
    var showAboutDialog by remember { mutableStateOf(false) }
    AboutAppDialog(
        show = showAboutDialog,
        onDismiss = { showAboutDialog = false },
    )
    FocusableLazyColumn(
        contentPadding = PaddingValues(32.dp),
        focusableSpec = focusableSpec(
            scale = 1.01f,
            elevation = ContentElevation.small,
            shape = MaterialTheme.shapes.medium,
        ),
    ) {
        menuAccountItem { onAction(LoginClick) }
        menuSectionLabel { Text(stringResource(R.string.menu_label_settings)) }
        menuSelectionItem(
            title = { Text(stringResource(R.string.menu_settings_theme)) },
            items = Theme.availableThemes(),
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
                confirmationDialogState = ConfirmationDialogState.Show(
                    message = R.string.menu_data_clear_history_confirmation,
                    onConfirm = { onAction(ClearHistoryClick) },
                )
            },
        )
        menuItem(
            text = { Text(stringResource(R.string.menu_data_clear_bookmarks)) },
            onClick = {
                confirmationDialogState = ConfirmationDialogState.Show(
                    message = R.string.menu_data_clear_bookmarks_confirmation,
                    onConfirm = { onAction(ClearBookmarksClick) },
                )
            },
        )
        menuItem(
            text = { Text(stringResource(R.string.menu_data_clear_favorites)) },
            onClick = {
                confirmationDialogState = ConfirmationDialogState.Show(
                    message = R.string.menu_data_clear_favorites_confirmation,
                    onConfirm = { onAction(ClearFavoritesClick) },
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
            onClick = { onAction(MenuAction.SendFeedbackClick) },
        )
        menuItem(
            text = { Text(stringResource(R.string.menu_misc_about)) },
            onClick = { showAboutDialog = true },
        )
    }
}

private fun LazyListScope.menuAccountItem(
    onClick: () -> Unit
) = item { AccountItem(onLogin = onClick) }

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

private fun FocusableLazyListScope.menuAccountItem(
    onClick: () -> Unit
) = focusableItem { AccountItem(onLogin = onClick) }

private fun FocusableLazyListScope.menuItem(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
) = focusableItem { MenuItem(text, onClick) }

private fun <T> FocusableLazyListScope.menuSelectionItem(
    title: @Composable () -> Unit,
    items: Array<T>,
    selected: T,
    labelMapper: @Composable (T) -> String,
    onSelect: (T) -> Unit,
) = focusableItem { MenuSelectionItem(title, items, selected, labelMapper, onSelect) }

@Composable
private fun MenuItem(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
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
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
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
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_notification),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = stringResource(R.string.app_name),
                    )
                }
            },
            text = {
                Column {
                    Text(stringResource(R.string.app_version, BuildConfig.VERSION_NAME))
                    Text(stringResource(R.string.app_copyright))
                }
            },
            confirmButton = {
                TextButton(
                    text = stringResource(R.string.action_ok),
                    onClick = onDismiss,
                )
            },
        )
    }
}
