package flow.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import flow.designsystem.component.CircularProgressIndicator
import flow.designsystem.component.Icon
import flow.designsystem.drawables.FlowIcons
import flow.designsystem.theme.AppTheme

@Composable
fun RemoteImage(
    src: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest
            .Builder(LocalContext.current)
            .data(src)
            .size(Size.ORIGINAL)
            .build()
    )
    when (val state = painter.state) {
        is AsyncImagePainter.State.Empty,
        is AsyncImagePainter.State.Loading -> {
            Box(
                modifier = modifier
                    .padding(AppTheme.spaces.medium)
                    .size(AppTheme.sizes.medium),
                content = { CircularProgressIndicator() },
            )
        }

        is AsyncImagePainter.State.Error -> {
            Icon(
                modifier = modifier
                    .padding(AppTheme.spaces.medium)
                    .size(AppTheme.sizes.large),
                icon = FlowIcons.ImagePlaceholder,
                tint = AppTheme.colors.outline,
                contentDescription = contentDescription,
            )
        }

        is AsyncImagePainter.State.Success -> {
            val size = state.painter.intrinsicSize
            androidx.compose.foundation.Image(
                modifier = Modifier
                    .width(width = size.width.dp)
                    .aspectRatio(size.width / size.height),
                painter = state.painter,
                contentDescription = contentDescription,
                contentScale = ContentScale.Fit,
            )
        }
    }
}

@Composable
fun RemoteImage(
    src: String?,
    onLoading: @Composable () -> Unit,
    onError: @Composable () -> Unit,
    onSuccess: @Composable (Painter) -> Unit,
) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest
            .Builder(LocalContext.current)
            .data(src)
            .size(Size.ORIGINAL)
            .build()
    )
    when (val state = painter.state) {
        is AsyncImagePainter.State.Empty,
        is AsyncImagePainter.State.Loading -> onLoading()
        is AsyncImagePainter.State.Error -> onError()
        is AsyncImagePainter.State.Success -> onSuccess(state.painter)
    }
}

@Composable
fun RemoteImage(
    src: String?,
    modifier: Modifier = Modifier,
    contentDescription: String?,
    onLoading: @Composable () -> Unit,
    onSuccess: @Composable (Painter) -> Unit,
    onError: @Composable () -> Unit,
) = SubcomposeAsyncImage(
    modifier = modifier,
    model = src,
    contentDescription = contentDescription,
    filterQuality = FilterQuality.High,
    content = {
        when (painter.state) {
            is AsyncImagePainter.State.Loading -> onLoading()
            is AsyncImagePainter.State.Success -> onSuccess(painter)
            is AsyncImagePainter.State.Empty -> onError()
            is AsyncImagePainter.State.Error -> onError()
        }
    },
)
