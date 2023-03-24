package flow.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage

@Composable
fun RemoteImage(
    modifier: Modifier = Modifier,
    src: String?,
    contentDescription: String?,
    placeholder: Painter? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    colorFilter: ColorFilter? = null,
) = AsyncImage(
    modifier = modifier,
    model = src,
    contentDescription = contentDescription,
    placeholder = placeholder,
    error = placeholder,
    fallback = placeholder,
    alignment = alignment,
    contentScale = contentScale,
    colorFilter = colorFilter,
)

@Composable
fun RemoteImage(
    src: String?,
    contentDescription: String?,
    onLoading: @Composable () -> Unit,
    onSuccess: @Composable (Painter) -> Unit,
    onError: @Composable () -> Unit,
) = SubcomposeAsyncImage(
    model = src,
    contentDescription = contentDescription,
    content = {
        when (painter.state) {
            is AsyncImagePainter.State.Loading -> onLoading()
            is AsyncImagePainter.State.Success -> onSuccess(painter)
            is AsyncImagePainter.State.Empty -> onError()
            is AsyncImagePainter.State.Error -> onError()
        }
    },
)
