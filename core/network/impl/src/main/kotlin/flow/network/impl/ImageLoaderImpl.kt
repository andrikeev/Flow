package flow.network.impl

import coil3.SingletonImageLoader
import flow.network.api.ImageLoader

internal class ImageLoaderImpl(
    private val imageLoaderFactory: SingletonImageLoader.Factory,
) : ImageLoader {
    override fun setup() = SingletonImageLoader.setSafe(imageLoaderFactory)
}
