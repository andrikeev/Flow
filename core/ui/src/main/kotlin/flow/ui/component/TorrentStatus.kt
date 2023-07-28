package flow.ui.component

import android.text.format.DateFormat
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import flow.designsystem.component.Icon
import flow.designsystem.component.Surface
import flow.designsystem.component.Text
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.drawables.Icon
import flow.designsystem.theme.AppTheme
import flow.designsystem.theme.FlowTheme
import flow.models.forum.Category
import flow.models.topic.Author
import flow.models.topic.Torrent
import flow.models.topic.TorrentStatus
import flow.models.topic.isValid
import flow.ui.R

@Composable
fun TorrentStatus(
    modifier: Modifier = Modifier,
    torrent: Torrent,
) = TorrentStatus(
    modifier = modifier,
    status = torrent.status,
    dateSeconds = torrent.date,
    size = torrent.size,
    seeds = torrent.seeds,
    leeches = torrent.leeches,
)

@Composable
fun TorrentStatus(
    modifier: Modifier = Modifier,
    status: TorrentStatus? = null,
    dateSeconds: Long? = null,
    date: String? = null,
    size: String? = null,
    seeds: Int? = null,
    leeches: Int? = null,
) {
    val dateFormat = DateFormat.getMediumDateFormat(LocalContext.current)
    val statusItems = remember(status, dateSeconds, date, size, seeds, leeches) {
        listOfNotNull(
            status?.let(StatusItem::Status),
            seeds?.let { StatusItem.Seeds(it.toString()) },
            leeches?.let { StatusItem.Leaches(it.toString()) },
            size?.takeIf(String::isNotBlank)?.let(StatusItem::Size),
            dateSeconds?.let { dateSeconds ->
                StatusItem.Date(dateFormat.format(dateSeconds * 1000))
            } ?: date?.let(StatusItem::Date),
        )
    }
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .height(AppTheme.sizes.mediumSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        itemsIndexed(statusItems) { index, item ->
            StatusItem(
                modifier = Modifier.fillParentMaxHeight(),
                item = item,
            )
            if (index < statusItems.lastIndex) {
                Spacer(modifier = Modifier.width(AppTheme.spaces.small))
            }
        }
    }
}

@Composable
private fun StatusItem(
    modifier: Modifier = Modifier,
    item: StatusItem,
) = Box(
    modifier = modifier
        .border(
            width = Dp.Hairline,
            color = when (item) {
                is StatusItem.Date -> AppTheme.colors.onSurface
                is StatusItem.Leaches -> AppTheme.colors.accentRed
                is StatusItem.Seeds -> AppTheme.colors.accentGreen
                is StatusItem.Size -> AppTheme.colors.accentBlue
                is StatusItem.Status -> item.status.color
            }.copy(alpha = 0.37f),
            shape = AppTheme.shapes.small,
        ),
    contentAlignment = Alignment.Center,
) {
    when (item) {
        is StatusItem.Date -> Text(
            modifier = Modifier.padding(
                horizontal = AppTheme.spaces.mediumSmall,
                vertical = AppTheme.spaces.small,
            ),
            text = item.date,
        )

        is StatusItem.Leaches -> StatusItemWithIcon(
            icon = FlowIcons.Leeches,
            contentDescription = stringResource(R.string.leeches),
            text = item.leeches,
            tint = AppTheme.colors.accentRed,
        )

        is StatusItem.Seeds -> StatusItemWithIcon(
            icon = FlowIcons.Seeds,
            contentDescription = stringResource(R.string.seeds),
            text = item.seeds,
            tint = AppTheme.colors.accentGreen,
        )

        is StatusItem.Size -> StatusItemWithIcon(
            icon = FlowIcons.FileSize,
            contentDescription = stringResource(R.string.size),
            text = item.size,
            tint = AppTheme.colors.accentBlue,
        )

        is StatusItem.Status -> if (item.status.isValid()) {
            Box(modifier = Modifier.padding(AppTheme.spaces.small)) {
                Icon(
                    icon = item.status.icon,
                    contentDescription = stringResource(item.status.resId),
                    tint = item.status.color,
                )
            }
        } else {
            StatusItemWithIcon(
                icon = item.status.icon,
                contentDescription = stringResource(item.status.resId),
                text = stringResource(item.status.resId),
                tint = item.status.color,
            )
        }
    }
}

@Composable
private fun StatusItemWithIcon(
    icon: Icon,
    contentDescription: String,
    text: String,
    tint: Color,
) = Row(
    modifier = Modifier.padding(
        horizontal = AppTheme.spaces.mediumSmall,
        vertical = AppTheme.spaces.small,
    ),
    verticalAlignment = Alignment.CenterVertically,
) {
    Icon(
        icon = icon,
        tint = tint,
        contentDescription = contentDescription,
    )
    Text(
        modifier = Modifier.padding(start = AppTheme.spaces.extraSmall),
        text = text,
        color = tint,
    )
}

private sealed interface StatusItem {
    data class Date(val date: String) : StatusItem
    data class Leaches(val leeches: String) : StatusItem
    data class Seeds(val seeds: String) : StatusItem
    data class Size(val size: String) : StatusItem
    data class Status(val status: TorrentStatus) : StatusItem
}

@Preview
@Composable
fun TorrentStatusPreview() {
    FlowTheme {
        Surface {
            TorrentStatus(
                torrent = Torrent(
                    id = "1",
                    title = "Сияние / The Shining (Стэнли Кубрик / S 23 3 Kubrick) 2x MVO + DVO + 4x AVO (Володарский, Гаврилов, Живов, Кузнецов) + VO + Sub Rus, Eng + Comm + Original Eng",
                    author = Author(name = "qooble"),
                    category = Category(id = "1", name = "UHD Video"),
                    tags = "[1980, США, ужасы, триллер, UHD BDRemux 2160p] [US Version]",
                    status = flow.models.topic.TorrentStatus.APPROVED,
                    date = 1632306880,
                    size = "92.73 GB",
                    seeds = 28,
                    leeches = 0,
                ),
            )
        }
    }
}
