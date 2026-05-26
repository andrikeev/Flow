package flow.network.impl

import coil3.SingletonImageLoader
import flow.network.api.ImageLoader
import javax.inject.Inject

internal class ImageLoaderImpl @Inject constructor(
    private val imageLoaderFactory: SingletonImageLoader.Factory,
) : ImageLoader {
    override fun setup() = SingletonImageLoader.setSafe(imageLoaderFactory)
}
