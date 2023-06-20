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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import flow.designsystem.component.Icon
import flow.designsystem.component.Surface
import flow.designsystem.component.Text
import flow.designsystem.drawables.FlowIcons
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
    val statusItems = remember(status, dateSeconds, date, size, seeds, leeches) {
        listOfNotNull(
            status?.let { status ->
                if (status.isValid()) {
                    StatusItem.Icon {
                        Icon(
                            icon = status.icon,
                            tint = status.color,
                            contentDescription = status.contentDescription,
                        )
                    }
                } else {
                    StatusItem.IconWithText(
                        icon = {
                            Icon(
                                icon = status.icon,
                                tint = status.color,
                                contentDescription = status.contentDescription,
                            )
                        },
                        text = { stringResource(status.resId) },
                    )
                }
            },
            seeds?.let { seeds ->
                StatusItem.IconWithText(
                    icon = {
                        Icon(
                            icon = FlowIcons.Seeds,
                            tint = AppTheme.colors.accentGreen,
                            contentDescription = stringResource(R.string.seeds),
                        )
                    },
                    text = { seeds.toString() },
                )
            },
            leeches?.let { leeches ->
                StatusItem.IconWithText(
                    icon = {
                        Icon(
                            icon = FlowIcons.Leeches,
                            tint = AppTheme.colors.accentRed,
                            contentDescription = stringResource(R.string.leeches),
                        )
                    },
                    text = { leeches.toString() },
                )
            },
            size?.takeIf(String::isNotBlank)?.let { size ->
                StatusItem.IconWithText(
                    icon = {
                        Icon(
                            icon = FlowIcons.FolderDownload,
                            tint = AppTheme.colors.accentBlue,
                            contentDescription = stringResource(R.string.size),
                        )
                    },
                    text = { size },
                )
            },
            dateSeconds?.let { dateSeconds ->
                StatusItem.Text {
                    DateFormat
                        .getMediumDateFormat(LocalContext.current)
                        .format(dateSeconds * 1000)
                }
            } ?: date?.let { date -> StatusItem.Text { date } }
        )
    }
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .height(AppTheme.sizes.mediumSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        itemsIndexed(statusItems) { index, item ->
            Box(
                modifier = Modifier
                    .fillParentMaxHeight()
                    .border(
                        width = Dp.Hairline,
                        color = AppTheme.colors.outlineVariant,
                        shape = AppTheme.shapes.small,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                when (item) {
                    is StatusItem.Icon -> {
                        Box(modifier = Modifier.padding(AppTheme.spaces.small)) {
                            item.icon()
                        }
                    }

                    is StatusItem.IconWithText -> {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = AppTheme.spaces.mediumSmall,
                                vertical = AppTheme.spaces.small,
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            item.icon()
                            Text(
                                modifier = Modifier.padding(start = AppTheme.spaces.extraSmall),
                                text = item.text()
                            )
                        }
                    }

                    is StatusItem.Text -> {
                        Text(
                            modifier = Modifier.padding(
                                horizontal = AppTheme.spaces.mediumSmall,
                                vertical = AppTheme.spaces.small,
                            ),
                            text = item.text(),
                        )
                    }
                }
            }
            if (index < statusItems.lastIndex) {
                Spacer(modifier = Modifier.width(AppTheme.spaces.small))
            }
        }
    }
}

sealed interface StatusItem {
    data class Icon(
        val icon: @Composable () -> Unit,
    ) : StatusItem

    data class IconWithText(
        val icon: @Composable () -> Unit,
        val text: @Composable () -> String,
    ) : StatusItem

    data class Text(
        val text: @Composable () -> String,
    ) : StatusItem
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
                )
            )
        }
    }
}
