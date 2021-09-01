package me.rutrackersearch.app.ui.common

import android.text.format.DateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.rutrackersearch.app.ui.theme.TopicColors
import me.rutrackersearch.domain.entity.forum.Category
import me.rutrackersearch.domain.entity.topic.Author
import me.rutrackersearch.domain.entity.topic.Torrent
import me.rutrackersearch.domain.entity.topic.TorrentStatus
import me.rutrackersearch.domain.entity.topic.isValid

@Composable
fun TorrentStatus(
    modifier: Modifier = Modifier,
    torrent: Torrent,
    contentPadding: PaddingValues = PaddingValues(),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    itemsPadding: PaddingValues = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
) {
    with(torrent) {
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
                        imageVector = status.icon,
                        tint = status.color,
                        contentDescription = null,
                    )
                    if (!status.isValid()) {
                        Text(
                            modifier = Modifier.padding(start = 4.dp),
                            text = stringResource(status.resId),
                        )
                    }
                }
            }
            if (status.isValid()) {
                seeds?.let { seeds ->
                    withDivider {
                        Icon(
                            imageVector = Icons.Outlined.FileUpload,
                            tint = TopicColors.seeds,
                            contentDescription = null,
                        )
                        Text(seeds.toString())
                    }
                }
                leeches?.let { leeches ->
                    withDivider {
                        Icon(
                            imageVector = Icons.Outlined.FileDownload,
                            tint = TopicColors.leaches,
                            contentDescription = null,
                        )
                        Text(leeches.toString())
                    }
                }
                size?.let { size ->
                    withDivider {
                        Icon(
                            imageVector = Icons.Outlined.AttachFile,
                            tint = TopicColors.file,
                            contentDescription = null,
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
            status = TorrentStatus.APPROVED,
            date = 1632306880063,
            size = "92.73 GB",
            seeds = 28,
            leeches = 0,
        )
    )
}
