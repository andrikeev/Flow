package flow.network.serialization

import kotlinx.serialization.json.Json

object JsonFactory {
    fun create(): Json {
        return Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
        }
    }
}
