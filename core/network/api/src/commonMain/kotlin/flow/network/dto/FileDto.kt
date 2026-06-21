package flow.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class FileDto(
    val contentDisposition: String,
    val contentType: String,
    val bytes: ByteArray,
)
