package flow.securestorage.model

import flow.models.settings.Endpoint
import org.json.JSONObject

internal object EndpointConverter {
    private const val TypeKey = "type"
    private const val HostKey = "host"

    fun Endpoint.toJson(): String {
        return JSONObject().apply {
            when (this@toJson) {
                is Endpoint.Proxy -> put(TypeKey, "Proxy")
                is Endpoint.Rutracker -> put(TypeKey, "Rutracker")
                is Endpoint.Mirror -> {
                    put(TypeKey, "Mirror")
                    put(HostKey, host)
                }
            }
        }.toString()
    }

    fun fromJson(json: String): Endpoint? {
        return runCatching {
            JSONObject(json).let { jsonObject ->
                when (jsonObject.getString(TypeKey)) {
                    "Proxy" -> Endpoint.Proxy
                    "Rutracker" -> Endpoint.Rutracker
                    "Mirror" -> Endpoint.Mirror(jsonObject.getString(HostKey))
                    else -> null
                }
            }
        }.getOrNull()
    }
}
