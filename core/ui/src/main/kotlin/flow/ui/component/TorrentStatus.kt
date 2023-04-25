package flow.ui.component

import android.text.format.DateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import flow.designsystem.component.Divider
import flow.designsystem.component.Icon
import flow.designsystem.component.Text
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.AppTheme
import flow.models.forum.Category
import flow.models.topic.Author
import flow.models.topic.Torrent
import flow.models.topic.TorrentStatus
import flow.models.topic.isValid

@Composable
fun TorrentStatus(
    modifier: Modifier = Modifier,
    torrent: Torrent,
    contentPadding: PaddingValues = PaddingValues(),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    itemsPadding: PaddingValues = PaddingValues(
        horizontal = AppTheme.spaces.mediumSmall,
        vertical = AppTheme.spaces.extraSmall,
    ),
) = TorrentStatus(
    modifier = modifier,
    status = torrent.status,
    date = torrent.date,
    size = torrent.size,
    seeds = torrent.seeds,
    leeches = torrent.leeches,
    contentPadding = contentPadding,
    horizontalArrangement = horizontalArrangement,
    itemsPadding = itemsPadding,
)

@Composable
fun TorrentStatus(
    modifier: Modifier = Modifier,
    status: TorrentStatus? = null,
    date: Long? = null,
    size: String? = null,
    seeds: Int? = null,
    leeches: Int? = null,
    contentPadding: PaddingValues = PaddingValues(),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    itemsPadding: PaddingValues = PaddingValues(
        horizontal = AppTheme.spaces.mediumSmall,
        vertical = AppTheme.spaces.extraSmall,
    ),
) {
    LazyRow(
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = horizontalArrangement,
    ) {
        var divider = false
        fun withDivider(content: @Composable LazyItemScope.() -> Unit) {
            item {
                if (divider) {
                    Divider(
                        modifier = Modifier
                            .padding(itemsPadding)
                            .height(20.dp)
                            .width(1.dp),
                    )
                }
                content()
                divider = true
            }
        }
        status?.let { status ->
            withDivider {
                Icon(
                    icon = status.icon,
                    tint = status.color,
                    contentDescription = null, //TODO: add contentDescription
                )
                if (!status.isValid()) {
                    Text(
                        modifier = Modifier.padding(start = AppTheme.spaces.small),
                        text = stringResource(status.resId),
                    )
                }
            }
        }
        if (status.isValid()) {
            seeds?.let { seeds ->
                withDivider {
                    Icon(
                        icon = FlowIcons.Seeds,
                        tint = AppTheme.colors.accentGreen,
                        contentDescription = null, //TODO: add contentDescription
                    )
                    Text(seeds.toString())
                }
            }
            leeches?.let { leeches ->
                withDivider {
                    Icon(
                        icon = FlowIcons.Leaches,
                        tint = AppTheme.colors.accentRed,
                        contentDescription = null, //TODO: add contentDescription
                    )
                    Text(leeches.toString())
                }
            }
            size?.let { size ->
                withDivider {
                    Icon(
                        icon = FlowIcons.File,
                        tint = AppTheme.colors.accentBlue,
                        contentDescription = null, //TODO: add contentDescription
                    )
                    Text(size)
                }
            }
            date?.let { date ->
                withDivider {
                    Text(
                        DateFormat
                            .getDateFormat(LocalContext.current)
                            .format(date * 1000)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun TorrentStatusPreview() {
    TorrentStatus(
        torrent = Torrent(
            id = "1",
            title = "Сияние / The Shining (Стэнли Кубрик / S 23 3 Kubrick) 2x MVO + DVO + 4x AVO (Володарский, Гаврилов, Живов, Кузнецов) + VO + Sub Rus, Eng + Comm + Original Eng",
            author = Author(name = "qooble"),
            category = Category(id = "1", name = "UHD Video"),
            tags = "[1980, США, ужасы, триллер, UHD BDRemux 2160p] [US Version]",
            status = flow.models.topic.TorrentStatus.APPROVED,
            date = 1632306880063,
            size = "92.73 GB",
            seeds = 28,
            leeches = 0,
        )
    )
}
