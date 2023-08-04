package flow.network.impl

import coil.Coil
import coil.ImageLoaderFactory
import flow.network.api.ImageLoader
import javax.inject.Inject

internal class ImageLoaderImpl @Inject constructor(
    private val imageLoaderFactory: ImageLoaderFactory,
) : ImageLoader {
    override fun setup() = Coil.setImageLoader(imageLoaderFactory)
}
