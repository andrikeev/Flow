package me.rutrackersearch.app.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Functions
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import me.rutrackersearch.app.R
import me.rutrackersearch.app.ui.theme.TopicColors
import me.rutrackersearch.domain.entity.search.Order
import me.rutrackersearch.domain.entity.search.Period
import me.rutrackersearch.domain.entity.search.Sort
import me.rutrackersearch.domain.entity.settings.SyncPeriod
import me.rutrackersearch.domain.entity.settings.Theme
import me.rutrackersearch.domain.entity.topic.TorrentStatus

val Period.resId: Int
    get() = when (this) {
        Period.ALL_TIME -> R.string.search_period_all_time
        Period.TODAY -> R.string.search_period_today
        Period.LAST_THREE_DAYS -> R.string.search_period_last_three_days
        Period.LAST_WEEK -> R.string.search_period_last_week
        Period.LAST_TWO_WEEKS -> R.string.search_period_last_two_weeks
        Period.LAST_MONTH -> R.string.search_period_last_month
    }

val Sort.resId: Int
    get() = when (this) {
        Sort.DATE -> R.string.sort_date
        Sort.TITLE -> R.string.sort_title
        Sort.DOWNLOADED -> R.string.sort_downloaded
        Sort.SEEDS -> R.string.sort_seeds
        Sort.LEECHES -> R.string.sort_leeches
        Sort.SIZE -> R.string.sort_size
    }

val Order.resId: Int
    get() = when (this) {
        Order.ASCENDING -> R.string.sort_order_ascending
        Order.DESCENDING -> R.string.sort_order_descending
    }

val TorrentStatus.icon: ImageVector
    get() = when (this) {
        TorrentStatus.DUPLICATE -> Icons.Outlined.ContentCopy
        TorrentStatus.NOT_APPROVED -> Icons.Outlined.RadioButtonUnchecked
        TorrentStatus.CHECKING -> Icons.Outlined.RadioButtonUnchecked
        TorrentStatus.APPROVED -> Icons.Outlined.CheckCircleOutline
        TorrentStatus.NEED_EDIT -> Icons.Outlined.HelpOutline
        TorrentStatus.CLOSED -> Icons.Outlined.Cancel
        TorrentStatus.NO_DESCRIPTION -> Icons.Outlined.ErrorOutline
        TorrentStatus.CONSUMED -> Icons.Outlined.Functions
    }

val TorrentStatus.color: Color
    @Composable get() = when (this) {
        TorrentStatus.DUPLICATE -> TopicColors.statusOkVariant
        TorrentStatus.NOT_APPROVED -> TopicColors.statusWarning
        TorrentStatus.CHECKING -> TopicColors.statusWarning
        TorrentStatus.APPROVED -> TopicColors.statusOk
        TorrentStatus.NEED_EDIT -> TopicColors.statusWarning
        TorrentStatus.CLOSED -> TopicColors.statusError
        TorrentStatus.NO_DESCRIPTION -> TopicColors.statusWarning
        TorrentStatus.CONSUMED -> TopicColors.statusOkVariant
    }

val TorrentStatus.resId: Int
    get() = when (this) {
        TorrentStatus.DUPLICATE -> R.string.torrent_status_duplicate
        TorrentStatus.NOT_APPROVED -> R.string.torrent_status_not_approved
        TorrentStatus.CHECKING -> R.string.torrent_status_checking
        TorrentStatus.APPROVED -> R.string.torrent_status_approved
        TorrentStatus.NEED_EDIT -> R.string.torrent_status_need_edit
        TorrentStatus.CLOSED -> R.string.torrent_status_closed
        TorrentStatus.NO_DESCRIPTION -> R.string.torrent_status_no_description
        TorrentStatus.CONSUMED -> R.string.torrent_status_consumed
    }

val Theme.resId: Int
    get() = when (this) {
        Theme.SYSTEM -> R.string.theme_system
        Theme.DYNAMIC -> R.string.theme_dynamic
        Theme.DARK -> R.string.theme_dark
        Theme.LIGHT -> R.string.theme_light
    }

val SyncPeriod.resId: Int
    get() = when (this) {
        SyncPeriod.OFF -> R.string.sync_period_off
        SyncPeriod.HOUR -> R.string.sync_period_hour
        SyncPeriod.SIX_HOURS -> R.string.sync_period_six_hours
        SyncPeriod.TWELVE_HOURS -> R.string.sync_period_twelve_hours
        SyncPeriod.DAY -> R.string.sync_period_daily
        SyncPeriod.WEEK -> R.string.sync_period_weekly
    }