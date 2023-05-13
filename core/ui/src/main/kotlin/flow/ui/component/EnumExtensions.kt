package flow.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.drawables.Icon
import flow.designsystem.theme.AppTheme
import flow.models.search.Order
import flow.models.search.Period
import flow.models.search.Sort
import flow.models.topic.TorrentStatus
import flow.ui.R

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

val TorrentStatus.icon: Icon
    get() = when (this) {
        TorrentStatus.DUPLICATE -> FlowIcons.TorrentStatus.Duplicate
        TorrentStatus.NOT_APPROVED -> FlowIcons.TorrentStatus.Checking
        TorrentStatus.CHECKING -> FlowIcons.TorrentStatus.Checking
        TorrentStatus.APPROVED -> FlowIcons.TorrentStatus.Approved
        TorrentStatus.NEEDS_EDIT -> FlowIcons.TorrentStatus.NeedsEdit
        TorrentStatus.CLOSED -> FlowIcons.TorrentStatus.Closed
        TorrentStatus.NO_DESCRIPTION -> FlowIcons.TorrentStatus.NoDescription
        TorrentStatus.CONSUMED -> FlowIcons.TorrentStatus.Consumed
    }

val TorrentStatus.color: Color
    @Composable get() = when (this) {
        TorrentStatus.DUPLICATE -> AppTheme.colors.accentBlue
        TorrentStatus.NOT_APPROVED -> AppTheme.colors.accentOrange
        TorrentStatus.CHECKING -> AppTheme.colors.accentOrange
        TorrentStatus.APPROVED -> AppTheme.colors.accentGreen
        TorrentStatus.NEEDS_EDIT -> AppTheme.colors.accentRed
        TorrentStatus.CLOSED -> AppTheme.colors.accentRed
        TorrentStatus.NO_DESCRIPTION -> AppTheme.colors.accentOrange
        TorrentStatus.CONSUMED -> AppTheme.colors.accentBlue
    }

val TorrentStatus.contentDescription: String
    @Composable get() = when (this) {
        TorrentStatus.DUPLICATE -> stringResource(R.string.torrent_status_duplicate)
        TorrentStatus.NOT_APPROVED -> stringResource(R.string.torrent_status_not_approved)
        TorrentStatus.CHECKING -> stringResource(R.string.torrent_status_checking)
        TorrentStatus.APPROVED -> stringResource(R.string.torrent_status_approved)
        TorrentStatus.NEEDS_EDIT -> stringResource(R.string.torrent_status_needs_edit)
        TorrentStatus.CLOSED -> stringResource(R.string.torrent_status_closed)
        TorrentStatus.NO_DESCRIPTION -> stringResource(R.string.torrent_status_no_description)
        TorrentStatus.CONSUMED -> stringResource(R.string.torrent_status_consumed)
    }

val TorrentStatus.resId: Int
    get() = when (this) {
        TorrentStatus.DUPLICATE -> R.string.torrent_status_duplicate
        TorrentStatus.NOT_APPROVED -> R.string.torrent_status_not_approved
        TorrentStatus.CHECKING -> R.string.torrent_status_checking
        TorrentStatus.APPROVED -> R.string.torrent_status_approved
        TorrentStatus.NEEDS_EDIT -> R.string.torrent_status_needs_edit
        TorrentStatus.CLOSED -> R.string.torrent_status_closed
        TorrentStatus.NO_DESCRIPTION -> R.string.torrent_status_no_description
        TorrentStatus.CONSUMED -> R.string.torrent_status_consumed
    }
